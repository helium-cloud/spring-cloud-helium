package org.helium.data.spark.spi;


import com.alibaba.fastjson.JSON;
import com.feinno.superpojo.util.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.protobuf.generated.ClientProtos;
import org.apache.hadoop.hbase.util.Base64;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalyst.expressions.GenericRow;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.helium.data.spark.SparkClient;
import org.helium.data.spark.entity.SparkHBaseSource;
import org.helium.framework.annotations.ServiceImplementation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Serializable;
import scala.Tuple2;

import java.io.IOException;
import java.util.*;

/**
 * Spark客户端实现类
 * <pre>
 *     临时表的生命周期是和创建该DataFrame的SQLContext有关系的，SQLContext生命周期结束，该临时表的生命周期也结束
 * </pre>
 */
@ServiceImplementation
public class SparkClientImpl implements SparkClient, Serializable {

    private final static Logger LOGGER = LoggerFactory.getLogger(SparkClientImpl.class);
    private static HashMap<String, List<StructField>> mapListField = new HashMap();
    private Properties config;

    public SparkClientImpl(Properties config) {
        this.config = config;
    }

    @Override
    public SparkSession create() {
        return create(null);
    }

    /**
     * 创建SparkSession
     * 提供了一个统一的切入点来使用Spark的各项功能
     *
     * @param appName
     * @return
     */
    @Override
    public SparkSession create(String appName) {
        //1.优先通过指定方式获取
        if (StringUtils.isNullOrEmpty(appName)) {
            appName = config.getProperty("defaultAppName", "default");
        }
        String master = config.getProperty("master");
        SparkSession sparkSession = SparkSession
                .builder()
                .appName(appName)
                .master(master)
                .getOrCreate();
        return sparkSession;
    }

    @Override
    public Dataset<Row> initDataSet(SparkSession sparkSession, Configuration configuration, SparkHBaseSource source) {
        return initDataSet(sparkSession, configuration, source, null);
    }

    @Override
    public Dataset<Row> initDataSet(SparkSession sparkSession, Configuration configuration, SparkHBaseSource source, List<StructField> mapList) {
        Dataset<Row> ds = null;
        try {
            //1.初始化
            String tableName = source.getTableName();
            String dataCf = source.getDataCf();
            String dataCl = source.getDataCl();
            configuration.set(TableInputFormat.INPUT_TABLE, tableName);
            //构建扫描条件
            ClientProtos.Scan proto = ProtobufUtil.toScan(source.getScan());
            String scanToString = Base64.encodeBytes(proto.toByteArray());
            configuration.set(TableInputFormat.SCAN, scanToString);

            JavaSparkContext sc = JavaSparkContext.fromSparkContext(sparkSession.sparkContext());
            //获得数据集
            JavaPairRDD<ImmutableBytesWritable, Result> hBaseRDD = sc.newAPIHadoopRDD(configuration,
                    TableInputFormat.class, ImmutableBytesWritable.class, Result.class);
            //
            //进行数据相关操作
            JavaPairRDD<String, Integer> levels = hBaseRDD.mapToPair(
                    new PairFunction<Tuple2<ImmutableBytesWritable, Result>, String, Integer>() {
                        @Override
                        public Tuple2<String, Integer> call(
                                Tuple2<ImmutableBytesWritable, Result> immutableBytesWritableResultTuple2)
                                throws Exception {
                            byte[] o = immutableBytesWritableResultTuple2._2().getValue(
                                    //根据列族列名获取数据
                                    Bytes.toBytes(dataCf), Bytes.toBytes(dataCl));
                            if (o != null) {
                                return new Tuple2<String, Integer>(Bytes.toString(o), 1);
                            }
                            return null;
                        }
                    });
            //2.
            //静态字段处理
            final List<StructField> mapListFinal = mapList;
            if (mapListFinal != null) {
                mapListField.put(tableName, mapList);
            }

            JavaRDD<String> levelsKeys = levels.keys();
            JavaRDD<Row> rowRDD = levelsKeys.map(new Function<String, Row>() {
                @Override
                public Row call(String line) throws Exception {
                    //2.1 数据集处理
                    Map<String, String> maps = (Map) JSON.parse(line);
                    List<String> keys = new ArrayList();
                    List<String> values = new ArrayList();
                    for (Map.Entry<String, String> entry : maps.entrySet()) {
                        keys.add((String.valueOf(entry.getKey())));
                        values.add(String.valueOf(entry.getValue()));
                    }

                    if (mapListFinal == null) {
                        //2.2数据项处理
                        List<StructField> dynamicMapList = mapListField.get(tableName);
                        //TODO 并发问题可能存在
                        synchronized (mapListField) {
                            if (dynamicMapList == null) {
                                dynamicMapList = new ArrayList<>();
                                mapListField.put(tableName, dynamicMapList);
                            }
                            if (dynamicMapList.size() < values.size()) {
                                for (String it : keys) {
                                    boolean toAdd = true;
                                    for (StructField typeItem : dynamicMapList) {
                                        if (typeItem.name().equals(it)) {
                                            toAdd = false;
                                            continue;
                                        }
                                    }
                                    if (toAdd) {
                                        StructField field = DataTypes.createStructField(it, DataTypes.StringType, true);
                                        dynamicMapList.add(field);
                                    }

                                }
                            }
                        }
                    }

                    Row row = new GenericRow(values.toArray());
                    return row;
                }
            });
            rowRDD.collect();
            //构建当前表的数据结构信息schema
            StructType schema = DataTypes.createStructType(mapListField.get(tableName));
            SQLContext sqlContext = sparkSession.sqlContext();
            ds = sqlContext.createDataFrame(rowRDD, schema);
            //注册临时表
            ds.createOrReplaceTempView(source.getSparkTableName());
        } catch (IOException e) {
            LOGGER.error("initDataset Exception", e);
        }
        return ds;
    }

    /**
     * 获取键值对操作PairRDD
     *
     * @see org.helium.hbase.HBaseClient getConnection().getConfiguration()
     * @param sparkSession
     * @param configuration
     * @param source
     * @return
     */
    @Override
    public JavaPairRDD<ImmutableBytesWritable, Result> getJavaPairRDD(SparkSession sparkSession, Configuration configuration, SparkHBaseSource source) {
        try {
            String tableName = source.getTableName();
            configuration.set(TableInputFormat.INPUT_TABLE, tableName);
            //构建扫描条件
            ClientProtos.Scan proto = ProtobufUtil.toScan(source.getScan());
            String scanToString = Base64.encodeBytes(proto.toByteArray());
            configuration.set(TableInputFormat.SCAN, scanToString);

            JavaSparkContext sc = JavaSparkContext.fromSparkContext(sparkSession.sparkContext());
            //获得数据集
            JavaPairRDD<ImmutableBytesWritable, Result> javaPairRDD = sc.newAPIHadoopRDD(configuration,
                    TableInputFormat.class, ImmutableBytesWritable.class, Result.class);
            return javaPairRDD;
        } catch (Exception e) {
            LOGGER.error("SparkClientImpl getJavaPairRDD error", e);
        }
        return null;
    }
}

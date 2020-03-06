import com.alibaba.fastjson.JSON;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.protobuf.generated.ClientProtos;
import org.apache.hadoop.hbase.util.Base64;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalyst.expressions.GenericRow;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import scala.Tuple2;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class HBaseSparkSQLTest implements Serializable {

    private static HashMap<String, List<StructField>> mapListField = new HashMap();

    public static void main(String[] args) {
        System.setProperty("spark.serializer", "org.apache.spark.serializer.KryoSerializer");
        HBaseSparkSQLTest urcsLogAnalysis = new HBaseSparkSQLTest();
        urcsLogAnalysis.searchFromHbase("SparkSQLTest", "local", "1234:product_cuser_tb");
    }

    //combineByKey把同一个key的value拿出来进行分析(求均值)
    public static JavaPairRDD combineByKey(JavaPairRDD pairRDD) {
        return pairRDD.combineByKey(
                //注意三个参数都是对相同的key的操作
                //第一个参数，如果这个key是初次出现则返回一个 value:1的初值tuple
                new Function<Integer, Tuple2<Integer, Integer>>() {
                    public Tuple2<Integer, Integer> call(Integer o) throws Exception {
                        return new Tuple2<Integer, Integer>(o, 1);
                    }
                },
                //第二个参数如果不是第二次出现的key，则初值tuple的value字段+这个key的value，然后初值tuple的num字段+1
                //第三个以此类推
                new Function2<Tuple2<Integer, Integer>, Integer, Tuple2<Integer, Integer>>() {
                    public Tuple2<Integer, Integer> call(Tuple2<Integer, Integer> o, Integer o2) throws Exception {
                        return new Tuple2<Integer, Integer>(o._1() + o2, o._2() + 1);
                    }
                },
                //最后初值tuple加完后（其实可以算均值了）但是因为第二个参数是在各个分区上计算完的结果，还需要最后的汇总
                //第三个参数就是汇总各个分区的value的和，num的和。这样最后返回的就是 key:[value的和，value的个数]
                new Function2<Tuple2<Integer, Integer>, Tuple2<Integer, Integer>, Tuple2<Integer, Integer>>() {
                    public Tuple2<Integer, Integer> call(Tuple2<Integer, Integer> o, Tuple2<Integer, Integer> o2) throws Exception {
                        return new Tuple2<Integer, Integer>(o._1() + o2._1(), o._2() + o2._2());
                    }
                });
    }

    //mapValues只对value进行map运算key保持返回等大的pairRdd
    public static JavaPairRDD mapValues(JavaPairRDD pairRDD) {
        return pairRDD.mapValues(new Function<Integer, String>() {
            public String call(Integer o) throws Exception {
                return o.toString() + "st";
            }
        });
    }

    //flatMapValues只对value进行的flatmap运算返回扩大版pairRdd
    public static JavaPairRDD flatMapValues(JavaPairRDD pairRDD) {
        return pairRDD.flatMapValues(new Function<Integer, Iterable>() {
            public List call(Integer o) throws Exception {
                return Arrays.asList(o.toString() + "st", "多个");
            }
        });
    }

    public static void eachPrint(JavaPairRDD rdd) {
        System.out.println("-------------------------------------------");
        rdd.foreach(new VoidFunction() {
            public void call(Object o) throws Exception {
                Tuple2 tuple2 = (Tuple2) o;
                System.out.println(tuple2._1 + ":" + tuple2._2);
            }
        });
    }

    public static void eachPrint(JavaRDD rdd) {
        System.out.println("-------------------------------------------");
        rdd.foreach(new VoidFunction() {
            public void call(Object s) throws Exception {
                System.out.println(s);
            }
        });
    }

    public void searchFromHbase(String appName, String master, String tb) {

        try {
            //1.初始化Spark
            SparkSession sparkSession = SparkSession
                    .builder()
                    .appName(appName)
                    .master(master)
                    .config("spark.some.config.option", "some-value")
                    .getOrCreate();
            JavaSparkContext sc = JavaSparkContext.fromSparkContext(sparkSession.sparkContext());
            //读取hbase-site.xml等配置
            Configuration conf = HBaseConfiguration.create();
            conf.set("hbase.zookeeper.quorum", "wkfhb");
            conf.set("hbase.zookeeper.property.clientPort", "2181");
//

            SparkConf sparkConf = new SparkConf().setMaster(master).setAppName(appName);

//            JavaSparkContext sc = new JavaSparkContext(sparkConf);


            //读取hbase-site.xml等配置
//            Configuration conf = HBaseConfiguration.create();

            conf.set("hbase.zookeeper.quorum", "wkfhb");
            conf.set("hbase.zookeeper.property.clientPort", "2181");

            Scan scan = new Scan();
            scan.addFamily(Bytes.toBytes("data"));
            scan.addColumn(Bytes.toBytes("data"), Bytes.toBytes("DATA_CL"));

            //设置查询的表
            conf.set(TableInputFormat.INPUT_TABLE, tb);
            ClientProtos.Scan proto = ProtobufUtil.toScan(scan);
            String ScanToString = Base64.encodeBytes(proto.toByteArray());
            //设置扫描的列
            conf.set(TableInputFormat.SCAN, ScanToString);

            //2.初始化数据集
            //读取Hbase表数据方式(从数据库中获取查询内容生成RDD)
            //拿到键值对
            JavaPairRDD<ImmutableBytesWritable, Result> hBaseRDD = sc.newAPIHadoopRDD(conf, TableInputFormat.class, ImmutableBytesWritable.class, Result.class);

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
                                    Bytes.toBytes("data"), Bytes.toBytes("DATA_CL"));
                            if (o != null) {
                                return new Tuple2<String, Integer>(Bytes.toString(o), 1);
                            }
                            return null;
                        }
                    });
            //2.
            //静态字段处理
            final List<StructField> mapListFinal =  new ArrayList<>();

            JavaRDD<String> levelsKeys = levels.keys();
            JavaRDD<Row> rowRDD = levelsKeys.map(new Function<String, Row>() {
                @Override
                public Row call(String line) throws Exception {
                    //2.1 数据集处理
                    Map<String, String> maps = (Map) JSON.parse(line);
                    List<String> values = new ArrayList();
                    List<String> items = new ArrayList();
                    for (Map.Entry<String, String> entry : maps.entrySet()) {
                        values.add(String.valueOf(entry.getValue()));
                        items.add((String.valueOf(entry.getKey())));
                    }

                    if (mapListFinal == null){
                        //2.2数据项处理
                        List<StructField> dynamicMapList = mapListField.get("1234:product_cuser_tb");
                        //TODO 并发问题可能存在
                        synchronized (mapListField) {
                            if (dynamicMapList == null) {
                                dynamicMapList = new ArrayList<>();
                                mapListField.put("1234:product_cuser_tb", dynamicMapList);
                            }
                            if (dynamicMapList.size() < values.size()) {
                                for (String it : items) {
                                    boolean toAdd = true;
                                    for (StructField typeItem : dynamicMapList) {
                                        if (typeItem.name().equals(it)){
                                            toAdd = false;
                                            continue;
                                        }
                                    }
                                    if (toAdd){
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
            StructType schema = DataTypes.createStructType(mapListField.get("1234:product_cuser_tb"));
            SQLContext sqlContext = sparkSession.sqlContext();
            Dataset<Row> dataFrame = sqlContext.createDataFrame(rowRDD, schema);
            //
            //注册临时表
            dataFrame.createOrReplaceTempView("1234:product_cuser_tb");

            dataFrame.show();

            //打印
            dataFrame.printSchema();
        } catch (IOException e) {



//            //遍历数据 collect foreach
//            List<Tuple2<ImmutableBytesWritable, Result>> output = hBaseRDD.collect();
//            for (Tuple2 tuple : output) {
//                System.out.println(tuple._1 + "：" + tuple._2);
//            }
//
//            System.out.println("sss:" + hBaseRDD.count());


//            JavaRDD<Row> rowRDD = jsonObjectJavaRDD.map(new Function<String, Row>() {
//                public Row call(String line) throws Exception {
//                    String[] parts = line.split(",");
//                    String sid = parts[0];
//                    String sname = parts[1];
//                    int sage = Integer.parseInt(parts[2]);
//
//                    return RowFactory.create(sid, sname, sage);
//                }
//            });
//
//            ArrayList<StructField> fields = new ArrayList<StructField>();
//            StructField field = null;
//            field = DataTypes.createStructField("sid", DataTypes.StringType, true);
//            fields.add(field);
//            field = DataTypes.createStructField("sname", DataTypes.StringType, true);
//            fields.add(field);
//            field = DataTypes.createStructField("sage", DataTypes.IntegerType, true);
//            fields.add(field);
//
//            StructType schema = DataTypes.createStructType(fields);
//
//            Dataset<Row> df = spark.createDataFrame(rowRDD, schema);


//            //数据累加
//            JavaPairRDD<String, Integer> counts = amount.reduceByKey(new Function2<Integer, Integer, Integer>() {
//                public Integer call(Integer i1, Integer i2) {
//                    System.out.println("---------------"+i1+":"+i2);
//                    return i1 + i2;
//                }
//            });

//            //打印出最终结果
//            List<Tuple2<String, Integer>> output = counts.collect();
//            for (Tuple2 tuple : output) {
//                System.out.println(tuple._1 + ": " + tuple._2);
////                if(Long.valueOf(tuple._2.toString()) >= Long.valueOf(basicParameter.getMap().get("times"))){
////
////                    CUserBean bean = new CUserBean();
////                    bean.setUserId(tuple._1.toString());
////                    //bean = getUserDao().get(bean);
////                    list.add(bean);
////
////                }
//            }

            //sc.close();
        } catch (Exception e) {
//            LOGGER.error("consumptionAmount is error!",e);
            e.printStackTrace();
        }
    }
}

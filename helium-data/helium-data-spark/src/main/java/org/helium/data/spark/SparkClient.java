package org.helium.data.spark;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructField;
import org.helium.data.spark.entity.SparkHBaseSource;
import org.helium.data.spark.spi.SparkClientLoader;
import org.helium.framework.annotations.FieldLoaderType;

import java.util.List;

/**
 * Spark客户端
 * <p>
 *     1.Spark数据处理采用HBase作为数据源
 *     2.支持
 * </p>
 */
@FieldLoaderType(loaderType = SparkClientLoader.class)
public interface SparkClient {

    SparkSession create();

    /**
     * 创建SparkSession
     * 提供了一个统一的切入点来使用Spark的各项功能
     * @param appName
     * @return
     */
    SparkSession create(String appName);

    /**
     * 非类型化数据集操作
     * Schema默认为DataTypes.StringType
     *
     * @param sparkSession
     * @param configuration
     * @param source
     * @return
     */
    Dataset<Row> initDataSet(SparkSession sparkSession, final Configuration configuration, SparkHBaseSource source);

    /**
     * 获取HBase库中数据集RDD
     * 1.以HBase当作数据源
     * 2.
     * @param sparkSession
     * @param configuration
     * @param source
     * @param structFieldList 指定Spark数据结构信息Schema
     * @return
     */
    Dataset<Row> initDataSet(SparkSession sparkSession, final Configuration configuration, SparkHBaseSource source, List<StructField> structFieldList);


    JavaPairRDD<ImmutableBytesWritable, Result> getJavaPairRDD(SparkSession sparkSession, final Configuration configuration, SparkHBaseSource source);
}

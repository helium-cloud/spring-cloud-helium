package org.helium.data.spark.entity;

import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import sun.misc.BASE64Encoder;

/**
 * HBase数据集
 */
public class SparkHBaseSource {

    /**HBase表名*/
    private String tableName;

    /**spark临时表名,用于SQL查询使用*/
    private String sparkTableName;

    /**HBase列族*/
    private String dataCf;

    /**HBase列*/
    private String dataCl;

    /**构建HBase扫描条件*/
    private Scan scan;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDataCf() {
        return dataCf;
    }

    public void setDataCf(String dataCf) {
        this.dataCf = dataCf;
    }

    public String getDataCl() {
        return dataCl;
    }

    public void setDataCl(String dataCl) {
        this.dataCl = dataCl;
    }

    public Scan getScan() {
        return scan;
    }

    public void setScan(Scan scan) {
        this.scan = scan;
    }


    public String getSparkTableName() {
        return sparkTableName;
    }

    public void setSparkTableName(String sparkTableName) {
        this.sparkTableName = sparkTableName;
    }

    public static SparkHBaseSource createConfig(String tableName){
        SparkHBaseSource sparkHBaseSource = new SparkHBaseSource();

        sparkHBaseSource.setDataCf("DATA_CF");
        sparkHBaseSource.setDataCl("DATA_CL");

        Scan scan = new Scan();
        scan.addFamily(Bytes.toBytes(sparkHBaseSource.getDataCf()));
        scan.addColumn(Bytes.toBytes(sparkHBaseSource.getDataCf()), Bytes.toBytes(sparkHBaseSource.getDataCl()));
        sparkHBaseSource.setScan(scan);
        sparkHBaseSource.setTableName(tableName);
        sparkHBaseSource.setSparkTableName(new BASE64Encoder().encode(tableName.getBytes()));
        return sparkHBaseSource;
    }
}

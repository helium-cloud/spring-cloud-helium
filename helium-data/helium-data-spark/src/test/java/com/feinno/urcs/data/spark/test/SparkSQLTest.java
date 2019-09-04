package com.feinno.urcs.data.spark.test;

import org.apache.spark.sql.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

public class SparkSQLTest {
    public static void main(String[] args) throws InterruptedException {
        SparkSQLTest urcsLogAnalysis = new SparkSQLTest();
        urcsLogAnalysis.searchFromJson("SparkSQLTest", "local", "/Users/wuhao/data/code/gitfeinno/helium/helium-data/helium-data-spark/src/test/resources/111.json");
    }

    public void searchFromJson(String appName, String master, String path) throws InterruptedException {

        //1.Init Spark session
        SparkSession sparkSession = SparkSession
                .builder()
                .appName(appName)
                .master(master)
                .config("spark.some.config.option", "some-value")
                .getOrCreate();
        //2. 初始化数据集
        Dataset<Row> people = sparkSession.read().json(path);
        people.printSchema();
        people.createOrReplaceTempView("people");
        people.show();
        //3. 查询数据集
        long start = System.currentTimeMillis();
        SQLContext sqlContext = people.sqlContext();
        System.out.println( start + " do search start-------------------");
        AtomicLong atomicLong = new AtomicLong();
        int threadNumSize = 30;
        int workNum = 1000;
        for (int i =0; i < threadNumSize; i++) {
            Thread threadNum = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0 ; j < workNum; j++) {
                        atomicLong.getAndIncrement();
                        sqlContext.sql("SELECT * FROM people WHERE name = 'Bear1'");
                    }
                }
            });
            threadNum.start();

        }
        while (true){
            if (atomicLong.get() > (threadNumSize * workNum - 1)){
                break;
            }
            Thread.sleep(100);

        }

        System.out.println( " do result:" + atomicLong.get());
        System.out.println( System.currentTimeMillis() - start + " do search end-------------------");


    }

//    public void searchFromJson(String appName, String master, String path) {
//
//        //1.Init Spark session
//        SparkSession sparkSession = SparkSession
//                .builder()
//                .appName(appName)
//                .master(master)
//                .config("spark.some.config.option", "some-value")
//                .getOrCreate();
//        //2. 初始化数据集
//        Dataset<Row> people = sparkSession.read().json(path);
//        people.printSchema();
//        people.createOrReplaceTempView("people");
//        people.show();
//        //3. 查询数据集
//        Dataset<Row> namesDF = sparkSession.sql("SELECT * FROM people WHERE name like '%est%'");
//        namesDF.show();
//    }
}

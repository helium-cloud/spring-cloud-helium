package org.helium.hbase.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.helium.hbase.HBaseClient;
import org.helium.hbase.HTableClient;
import org.helium.hbase.spi.HBaseClientManager;
import org.helium.hbase.spi.HBaseManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

public class HBaseClientManagerTest {
    public static void main(String[] args) throws Exception {
//        testHBaseManager(null);
//        String path = "helium-data-hbase/src/test/resources/hbase/UHB.properties";
//        Properties properties = new Properties();
//        properties.load(new FileReader(path));
//        HTableClient hTableClient = HBaseManager.INSTANCE.getHTableClient("HUB", properties);
//        createTable(hTableClient, "URCS_LOG", new String[]{"type_cf", "data_cf"});
        getHbaseClient("URCS_DATA_WKF_DB");
    }


    /**
     * 新建hbase表
     *
     * @param tableName 表名
     * @param cfs       列族数组
     * @return 是否创建成功
     */
    public static boolean createTable(HTableClient hTableClient, String tableName, String[] cfs) {
        try (HBaseAdmin admin = (HBaseAdmin) hTableClient.getConnection().getAdmin()) {
            if (admin.tableExists(tableName)) {
                return false;
            }
            HTableDescriptor tableDescriptor = new HTableDescriptor(TableName.valueOf(tableName));
            // 列族相关信息
            Arrays.stream(cfs).forEach(cf -> {
                HColumnDescriptor columnDescriptor = new HColumnDescriptor(cf);
                columnDescriptor.setMaxVersions(1);
                // 增加列族
                tableDescriptor.addFamily(columnDescriptor);
            });
            // 创建表
            admin.createTable(tableDescriptor);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }


    public static void Test(String[] args) throws Exception {
        // Instantiating configuration class
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "myhbase");;
        Connection conn = ConnectionFactory.createConnection(conf);
        Admin admin = conn.getAdmin();				// Instantiating table descriptor class


        HTableDescriptor tableDescriptor = new HTableDescriptor(TableName.valueOf("emp"));
        // Adding column families to table descriptor
        //
        tableDescriptor.addFamily(new HColumnDescriptor("personal"));
        tableDescriptor.addFamily(new HColumnDescriptor("professional"));
        // Execute the table through admin
        System.out.println("Creating the table ");
        admin.createTable(tableDescriptor);
        System.out.println("Table created ");
    }


    public static void TestNameSpace(String[] args) throws Exception {
        // Instantiating configuration class
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "myhbase");;
        Connection conn = ConnectionFactory.createConnection(conf);
        Admin admin = conn.getAdmin();				// Instantiating table descriptor class


        for (int i =100; i < 100000; i++) {
            System.out.println("Creating the table ");
            String nameSpace =  "my_ns" + i;


            //create tableDesc, with  namespace  name "my_ns" and table name "mytable "
            HTableDescriptor tableDesc =  new  HTableDescriptor(TableName. valueOf (  nameSpace + ":mytable" ));
            tableDesc.addFamily(new HColumnDescriptor("personal"));
            tableDesc.addFamily(new HColumnDescriptor("professional"));
            tableDesc.setDurability(Durability. SYNC_WAL );
            admin.createTable(tableDesc);
            System.out.println("Table created ");
        }

    }

    public static void getHbaseClient(String hbaseName) throws Exception{
        String path = "helium-data-hbase/src/test/resources/hbase/URCS_DATA_WKF_DB.properties";
        Properties properties = new Properties();
        properties.load(new FileReader(path));

//        HBaseClient hBaseClient = HBaseClientManager.INSTANCE.getHBaseClient(hbaseName,properties);
        HBaseClient hBaseClient = HBaseClientManager.INSTANCE.getHBaseClient("URCS_DATA_WKF_DB");
        hBaseClient.getAdmin().tableExists(TableName.valueOf("1:suggestion_tb"));

    }


    public static void testHBaseManager(String[] args) throws Exception {
//        // Instantiating configuration class
//        MessageArgs messageArgs = new MessageArgs();
//
//        messageArgs.setContent("hello");
//        messageArgs.setPeer("123");
//
//        messageArgs.setDate(new Date());
//        messageArgs.setOwner("456");
//        messageArgs.setMessageID("abc");
//
//        String path = "helium-data-hbase/src/test/resources/hbase/UHB.properties";
//        Properties properties = new Properties();
//        properties.load(new FileReader(path));
//        HBaseClient hBaseClient = HBaseClientManager.INSTANCE.getHBaseClient("UHB", properties);
//
//
//        System.out.println(hBaseClient.createNameSpace("1234"));
//        System.out.println(hBaseClient.createTable("1234:product_cuser_tb", new String[]{"data"}));
//
//        try {
//            Put putData = new Put(Bytes.toBytes("xxxxrow1"));
//            // 参数分别:列族、列、值
//            // 添加内容
//            //putData.addColumn(Bytes.toBytes(DATA_CF), Bytes.toBytes(DATA_CL), Bytes.toBytes(productBean.toJsonObject().toString()));
//            putData.addColumn(Bytes.toBytes("data"), Bytes.toBytes("DATA_CL"), Bytes.toBytes(messageArgs.toJsonObject().toString()));
//            Table hTable = hBaseClient.getTable("1234", "product_cuser_tb");
//
//
//            hTable.put(putData);
//            hTable.close();
//        } catch (Exception e){
//            e.printStackTrace();
//        }


    }



}

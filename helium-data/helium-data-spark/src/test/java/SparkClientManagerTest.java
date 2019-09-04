import com.alibaba.fastjson.JSONObject;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.helium.data.spark.SparkClient;
import org.helium.data.spark.entity.SparkHBaseSource;
import org.helium.data.spark.spi.SparkClientManager;
import org.helium.hbase.HBaseClient;
import org.helium.hbase.spi.HBaseClientManager;
import scala.Serializable;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SparkClientManagerTest{
    public static void main(String[] args) throws Exception {
        int nums = 1;
        //testInsert(nums);
        testSparkClient(nums);
    }

    public static void testSparkClient(int nums) throws Exception {
        // Instantiating configuration class
        String path = "helium-data-spark/src/test/resources/USP.properties";
        Properties properties = new Properties();
        properties.load(new FileReader(path));
        SparkClient sparkClient = SparkClientManager.INSTANCE.getSparkClient("USP", properties);

        String pathh = "helium-data-spark/src/test/resources/UHB.properties";
        Properties propertiesh = new Properties();
        propertiesh.load(new FileReader(pathh));
        HBaseClient hBaseClient = HBaseClientManager.INSTANCE.getHBaseClient("UHB", propertiesh);
        long start = System.currentTimeMillis();
        System.out.println(start);

        String tableName = "test_tb" + nums;
        SparkSession sparkSession = sparkClient.create("test");
        System.out.println("cura"  + (System.currentTimeMillis() - start));
        SparkHBaseSource source = SparkHBaseSource.createConfig("1111:test_tb" + nums);
        source.setSparkTableName("test_tb" + nums);
        System.out.println("init start"  + (System.currentTimeMillis() - start));
        Dataset<Row> dataset = sparkClient.initDataSet(sparkSession, hBaseClient.getConnection().getConfiguration(), source);
        System.out.println("init ok"  + (System.currentTimeMillis() - start));
        dataset.sqlContext().sql("select * from " + tableName + " where id like '%050'").show();
//        Dataset<Row> datasetCount = dataset.sqlContext().sql("select id, name from " + "test_tb" + nums + " where id > 'id5000' and id < 'id5002'");
//        datasetCount.show();
        System.out.println("end"  + (System.currentTimeMillis() - start));

    }

    public static void testInsert(int nums) throws Exception {
        // Instantiating configuration class

        String pathh = "helium-data-spark/src/test/resources/UHB.properties";
        Properties propertiesh = new Properties();
        propertiesh.load(new FileReader(pathh));
        HBaseClient hBaseClient = HBaseClientManager.INSTANCE.getHBaseClient("UHB", propertiesh);
        try {
            HTableDescriptor tableDescriptor = new HTableDescriptor(TableName.valueOf("1111:test_tb" + nums));
            // Adding column families to table descriptor
            //
            tableDescriptor.addFamily(new HColumnDescriptor("DATA_CF"));
            hBaseClient.getAdmin().createTable(tableDescriptor);
        } catch (Exception e){
            e.printStackTrace();
        }

        long start = System.currentTimeMillis();
        System.out.println(start);
        List<Put> list = new ArrayList<>();
        for (int i =0; i < 100000; i++) {
            Put putData = new Put(Bytes.toBytes( "sss" + i));
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", "id"+i);
            jsonObject.put("name", "name" + i);
            putData.addColumn(Bytes.toBytes("DATA_CF"), Bytes.toBytes("DATA_CL"), Bytes.toBytes(jsonObject.toJSONString()));
            list.add(putData);
        }
        Table hTable = hBaseClient.getTable("1111", "test_tb" + nums);
        hTable.put(list);
        System.out.println(System.currentTimeMillis() - start);


    }



}

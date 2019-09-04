package org.helium.hbase.test;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.helium.framework.BeanIdentity;
import org.helium.framework.spi.Bootstrap;
import org.helium.hbase.HTableClient;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by lvmingwei on 16-6-23.
 */
public class HBasePerfmonTest {

    private static AtomicInteger counter = new AtomicInteger(0);

    public static void main(String args[]) throws Exception {
        Bootstrap.INSTANCE.addPath("helium-data-services/build/resources/test");
        Bootstrap.INSTANCE.addPath("helium-data-services/src/test/resources/");
        Bootstrap.INSTANCE.addPath("helium-data-services/build/main");
        Bootstrap.INSTANCE.initialize("bootstrap-hbase.xml", false, false);
        for (int i = 0; i < 30; i++) {
            new HBasePerfmonTest().start();
        }
        new HBasePerfmonTest().print();
    }

    public void print() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int prv = 0;
                while (true) {
                    try {
                        int current = counter.intValue();
                        int num = current - prv;
                        prv = current;
                        System.out.println(new Date().toString() + ":" + num);
                        Thread.currentThread().sleep(1000);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    public void start() throws Exception {
        HBaseServerTest test = (HBaseServerTest) Bootstrap.INSTANCE.getBean(new BeanIdentity("org/helium/hbase/test", "HBaseServerTest")).getBean();
        test.ping();
        HTableClient table = test.getHTableClient();
        String content = new String(new byte[256]);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        addRow(table, String.valueOf(counter.incrementAndGet()), "MESSAGE", "content", content);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
        // createTable(table.getConfiguration(), "RCS_T1", new String[]{"MESSAGE"});
//        for (int i = 0; i < 100; i++) {
//            addRow(table, String.valueOf(i), "MESSAGE", "count", "T" + i);
//            System.out.println("Put row:" + i);
//        }
//        getRow(table, String.valueOf(38));
//        delRow(table, String.valueOf(38));
//        getRow(table, String.valueOf(38));
    }

    private void createTable(Configuration conf, String tableName, String[] family) throws IOException {
        TableName tableNameObj = TableName.valueOf(tableName);
        HBaseAdmin admin = new HBaseAdmin(conf);
        HTableDescriptor desc = new HTableDescriptor(tableNameObj);
        for (int i = 0; i < family.length; i++) {
            HColumnDescriptor column = new HColumnDescriptor(family[i]);
            column.setTimeToLive(86400);// 测试代码,值存储一天
            column.setBlocksize(128 * 1024);
            desc.addFamily(column);
        }
        if (admin.tableExists(tableNameObj)) {
            System.out.println("table Exists!");
            admin.disableTable(tableNameObj);
            admin.deleteTable(tableNameObj);
        }
        admin.createTable(desc);
        System.out.println("create table Success!");
    }

    private void addRow(HTableClient table, String row, String columnFamily, String column, String value) throws IOException {
        Put put = new Put(Bytes.toBytes(row));// 指定行
        // 参数分别:列族、列、值
        put.add(Bytes.toBytes(columnFamily), Bytes.toBytes(column), Bytes.toBytes(value));
        table.put(put);
    }

    private void delRow(HTableClient table, String row) throws Exception {
        Delete del = new Delete(Bytes.toBytes(row));
        table.delete(del);
    }

    // 获取一条数据
    private Cell[] getRow(HTableClient table, String row) throws Exception {
        Get get = new Get(Bytes.toBytes(row));
        Result result = table.get(get);
        // 输出结果,raw方法返回所有keyvalue数组
        Cell[] cells = result.rawCells();
        for (int i = 0; i < cells.length; i++) {
            Cell cell = cells[i];
            String time = new Date(cell.getTimestamp()).toString();
            String qualifier = new String(cell.getQualifier());
            String value = new String(cell.getValue());
            System.out.println("timestamp:" + time + ", qualifier:" + qualifier + ", value:" + value);
        }
        return cells;
    }
}

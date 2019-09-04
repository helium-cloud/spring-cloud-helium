package org.helium.hbase.spi;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import com.google.protobuf.Service;
import com.google.protobuf.ServiceException;
import org.helium.hbase.HTableClient;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.client.coprocessor.Batch;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.ipc.CoprocessorRpcChannel;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by lvmingwei on 16-6-22.
 */
public class HTableClientImpl implements HTableClient {

    private TableName tableName;

    private Properties config;

    private Connection connection;

    protected HTableClientImpl(Connection connection, String tableName, Properties config) {
        this.connection = connection;
        this.tableName = TableName.valueOf(tableName);
        this.config = config;
    }


    private Table getTable() {
        try {
            Table table = connection.getTable(tableName);
            return table;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void returnTable(Table table) {
        try {
            if (table != null) {
                // table.close() 会自动commits
                table.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public TableName getName() {
        Table table = getTable();
        try {
            return table.getName();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            returnTable(table);
        }
    }

    @Override
    public Configuration getConfiguration() {
        Table table = getTable();
        try {
            return table.getConfiguration();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            returnTable(table);
        }
    }

    @Override
    public HTableDescriptor getTableDescriptor() throws IOException {
        Table table = getTable();
        try {
            return table.getTableDescriptor();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            returnTable(table);
        }
    }

    @Override
    public boolean exists(Get get) throws IOException {
        Table table = getTable();
        try {
            return table.exists(get);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            returnTable(table);
        }
    }

    @Override
    public boolean[] existsAll(List<Get> gets) throws IOException {
        Table table = getTable();
        try {
            return table.existsAll(gets);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            returnTable(table);
        }
    }


    @Override
    public void batch(List<? extends Row> actions, Object[] results) throws IOException, InterruptedException {
        Table table = getTable();
        try {
            table.batch(actions, results);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            returnTable(table);
        }
    }

    @Override
    public Object[] batch(List<? extends Row> actions) throws IOException, InterruptedException {
        Table table = getTable();
        try {
            return table.batch(actions);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            returnTable(table);
        }
    }

    @Override
    public <R> void batchCallback(List<? extends Row> actions, Object[] results, Batch.Callback<R> callback) throws IOException, InterruptedException {
        Table table = getTable();
        try {
            table.batchCallback(actions, results, callback);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            returnTable(table);
        }
    }

    @Override
    public <R> Object[] batchCallback(List<? extends Row> actions, Batch.Callback<R> callback) throws IOException, InterruptedException {
        Table table = getTable();
        try {
            return table.batchCallback(actions, callback);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            returnTable(table);
        }
    }

    @Override
    public Result get(Get get) throws IOException {
        Table table = getTable();
        try {
            return table.get(get);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            returnTable(table);
        }
    }

    @Override
    public Result[] get(List<Get> gets) throws IOException {
        Table table = getTable();
        try {
            return table.get(gets);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            returnTable(table);
        }
    }

    @Override
    public ResultScanner getScanner(Scan scan) throws IOException {
        Table table = getTable();
        try {
            return table.getScanner(scan);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            returnTable(table);
        }
    }

    @Override
    public ResultScanner getScanner(byte[] family) throws IOException {
        Table table = getTable();
        try {
            return table.getScanner(family);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            returnTable(table);
        }
    }

    @Override
    public ResultScanner getScanner(byte[] family, byte[] qualifier) throws IOException {
        Table table = getTable();
        try {
            return table.getScanner(family, qualifier);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            returnTable(table);
        }
    }

    @Override
    public void put(Put put) throws IOException {
        Table table = getTable();
        try {
            table.put(put);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            returnTable(table);
        }
    }

    @Override
    public void put(List<Put> puts) throws IOException {
        Table table = getTable();
        try {
            table.put(puts);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            returnTable(table);
        }
    }

    @Override
    public boolean checkAndPut(byte[] row, byte[] family, byte[] qualifier, byte[] value, Put put) throws IOException {
        Table table = getTable();
        try {
            return table.checkAndPut(row, family, qualifier, value, put);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            returnTable(table);
        }
    }

    @Override
    public boolean checkAndPut(byte[] row, byte[] family, byte[] qualifier, CompareFilter.CompareOp compareOp, byte[] value, Put put) throws IOException {
        Table table = getTable();
        try {
            return table.checkAndPut(row, family, qualifier, compareOp, value, put);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            returnTable(table);
        }
    }

    @Override
    public void delete(Delete delete) throws IOException {
        Table table = getTable();
        try {
            table.delete(delete);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            returnTable(table);
        }
    }

    @Override
    public void delete(List<Delete> deletes) throws IOException {
        Table table = getTable();
        try {
            table.delete(deletes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            returnTable(table);
        }
    }

    @Override
    public boolean checkAndDelete(byte[] row, byte[] family, byte[] qualifier, byte[] value, Delete delete) throws IOException {
        Table table = getTable();
        try {
            return table.checkAndDelete(row, family, qualifier, value, delete);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            returnTable(table);
        }
    }

    @Override
    public boolean checkAndDelete(byte[] row, byte[] family, byte[] qualifier, CompareFilter.CompareOp compareOp, byte[] value, Delete delete) throws IOException {
        Table table = getTable();
        try {
            return table.checkAndDelete(row, family, qualifier, compareOp, value, delete);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            returnTable(table);
        }
    }

    @Override
    public void mutateRow(RowMutations rm) throws IOException {
        Table table = getTable();
        try {
            table.mutateRow(rm);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            returnTable(table);
        }
    }

    @Override
    public Result append(Append append) throws IOException {
        Table table = getTable();
        try {
            return table.append(append);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            returnTable(table);
        }
    }

    @Override
    public Result increment(Increment increment) throws IOException {
        Table table = getTable();
        try {
            return table.increment(increment);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            returnTable(table);
        }
    }

    @Override
    public long incrementColumnValue(byte[] row, byte[] family, byte[] qualifier, long amount) throws IOException {
        Table table = getTable();
        try {
            return table.incrementColumnValue(row, family, qualifier, amount);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            returnTable(table);
        }
    }

    @Override
    public long incrementColumnValue(byte[] row, byte[] family, byte[] qualifier, long amount, Durability durability) throws IOException {
        Table table = getTable();
        try {
            return table.incrementColumnValue(row, family, qualifier, amount, durability);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            returnTable(table);
        }
    }


    @Override
    public void close() throws IOException {
        Table table = getTable();
        try {
            table.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            returnTable(table);
        }
    }

    @Override
    public CoprocessorRpcChannel coprocessorService(byte[] row) {
        Table table = getTable();
        try {
            return table.coprocessorService(row);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            returnTable(table);
        }
    }

    @Override
    public <T extends Service, R> Map<byte[], R> coprocessorService(Class<T> service, byte[] startKey, byte[] endKey, Batch.Call<T, R> callable) throws ServiceException, Throwable {
        Table table = getTable();
        try {
            return table.coprocessorService(service, startKey, endKey, callable);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            returnTable(table);
        }
    }

    @Override
    public <T extends Service, R> void coprocessorService(Class<T> service, byte[] startKey, byte[] endKey, Batch.Call<T, R> callable, Batch.Callback<R> callback) throws ServiceException, Throwable {
        Table table = getTable();
        try {
            table.coprocessorService(service, startKey, endKey, callable, callback);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            returnTable(table);
        }
    }


    @Override
    public long getWriteBufferSize() {
        Table table = getTable();
        try {
            return table.getWriteBufferSize();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            returnTable(table);
        }
    }

    @Override
    public void setWriteBufferSize(long writeBufferSize) throws IOException {
        Table table = getTable();
        try {
            table.setWriteBufferSize(writeBufferSize);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            returnTable(table);
        }
    }

    @Override
    public <R extends Message> Map<byte[], R> batchCoprocessorService(Descriptors.MethodDescriptor methodDescriptor, Message request, byte[] startKey, byte[] endKey, R responsePrototype) throws ServiceException, Throwable {
        Table table = getTable();
        try {
            return table.batchCoprocessorService(methodDescriptor, request, startKey, endKey, responsePrototype);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            returnTable(table);
        }
    }

    @Override
    public <R extends Message> void batchCoprocessorService(Descriptors.MethodDescriptor methodDescriptor, Message request, byte[] startKey, byte[] endKey, R responsePrototype, Batch.Callback<R> callback) throws ServiceException, Throwable {
        Table table = getTable();
        try {
            table.batchCoprocessorService(methodDescriptor, request, startKey, endKey, responsePrototype, callback);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            returnTable(table);
        }
    }

    @Override
    public boolean checkAndMutate(byte[] row, byte[] family, byte[] qualifier, CompareFilter.CompareOp compareOp, byte[] value, RowMutations mutation) throws IOException {
        Table table = getTable();
        try {
            return table.checkAndMutate(row, family, qualifier, compareOp, value, mutation);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            returnTable(table);
        }
    }

    @Override
    public Connection getConnection() {
        return connection;
    }
}

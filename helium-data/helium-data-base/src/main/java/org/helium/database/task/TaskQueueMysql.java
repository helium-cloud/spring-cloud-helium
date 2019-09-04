package org.helium.database.task;

import org.helium.database.DataRow;
import org.helium.database.DataTable;
import org.helium.database.Database;
import org.helium.database.sharding.ShardedDatabase;
import org.helium.framework.annotations.FieldSetter;
import org.helium.framework.annotations.ServiceImplementation;
import org.helium.framework.annotations.ServiceSetter;
import org.helium.framework.spi.task.TaskArgs;
import org.helium.framework.tag.Initializer;
import org.helium.framework.task.TaskConsumer;
import org.helium.framework.task.TaskQueue;
import org.helium.framework.task.TaskStorageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * QPS单台 4000左右取决于MYSQL的写入性能
 * limit单条一百
 */
@ServiceImplementation(id = TaskQueue.BEAN_ID + TaskStorageType.MYSQL_TYPE)
public class TaskQueueMysql implements TaskQueue {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskQueueMysql.class);
    public static final String TASK_TABLE_NAME = "HELIUM_TASK_LIST";

    public static final String SQL_INSERT = "insert into HELIUM_TASK_LIST%s(TaskId,EventName,Args) values(?,?,?)";
    public static final String SQL_DELETE = "delete from HELIUM_TASK_LIST%s where Tid = ?";
    public static final String SQL_DELETE_LIST = "DELETE FROM HELIUM_TASK_LIST%s WHERE `Tid` > ?  order by `Tid` ASC limit ?;";
    public static final String SQL_GET = "select Tid,TaskId,EventName,Args from HELIUM_TASK_LIST%s order by `Tid` ASC limit ?";


    @FieldSetter("${TASK_DB}")
    private ShardedDatabase<Long> shardedDatabse;

    @ServiceSetter
    private TaskConsumer taskConsumer;

    @Initializer
    public void init() {
        try {
            LOGGER.info("TaskQueueMysql init start");
            initTable(TASK_TABLE_NAME);
            taskConsumer.putStorage(TaskStorageType.MYSQL_TYPE, this);
            //taskConsumer
            LOGGER.info("TaskQueueMysql init complete");
        } catch (Exception e) {
            LOGGER.error("TaskQueueMysql init error.{}", e);
        }
    }

    public long getLimit(){
        return 100;
    }


    private Database getSharding(int partition) {
        long partitionLong = partition;
        return shardedDatabse.getSharding(partitionLong);
    }

    public void initTable(String tableNamePre) throws Exception {
        try {
            for (int i = 0; i < 6; i++) {
                Database database = getSharding(i);
                String tableName = tableNamePre + i;
                if (!checkedTableIsExist(database, tableName)) {

                    String createTableSql = "CREATE TABLE `" + tableName + "` (\n" +
                            "  `Tid` bigint(40) NOT NULL AUTO_INCREMENT,\n" +
                            "  `TaskId` varchar(80) NOT NULL,\n" +
                            "  `EventName` varchar(80) NOT NULL,\n" +
                            "  `Args` blob NOT NULL,\n" +
                            "  `OperaTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                            "  PRIMARY KEY (`Tid`)\n" +
                            ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
                    database.executeNonQuery(createTableSql);
                } else {
                    LOGGER.info("{} is already exists! ", tableName);
                }
            }
        } catch (Exception e) {
            LOGGER.error("initTable error:", e);
        }
    }

    private boolean checkedTableIsExist(Database db, String tableName) {
        String sql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA= ? AND TABLE_NAME = ?";
        try {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("checkedTableIsExist db.getName={},tableName={}", db.getTableSchema(), tableName);
            }
            DataTable dt = db.executeTable(sql, db.getTableSchema(), tableName);
            if (dt != null && dt.getRowCount() > 0) {
                return true;
            }
        } catch (Exception e) {
            LOGGER.error("check table({}) is exist error! ex ={}", tableName, e);
        }
        return false;
    }

    @Override
    public void put(int partition, TaskArgs taskArgs) {
        try {
            String sql = String.format(SQL_INSERT, partition);
            Database database = getSharding(partition);
            database.executeInsert(sql, taskArgs.getId(), taskArgs.getEventName(), taskArgs.getArgStr());
        } catch (Exception e) {
            LOGGER.error("put error", e);
        }
    }

    @Override
    public List<TaskArgs> poolList(int partition) {
        List<TaskArgs> result = new ArrayList<TaskArgs>();
        try {

            String sql = String.format(SQL_GET, partition);
            Database database = getSharding(partition);
            DataTable dataTable = database.executeTable(sql, getLimit());
            for (DataRow dr : dataTable.getRows()) {
                TaskArgs taskArgs = new TaskArgs();
                taskArgs.setId(dr.getString("TaskId"));
                taskArgs.setTid(dr.getLong("Tid"));
                taskArgs.setEventName(dr.getString("EventName"));
                taskArgs.setArgStr(dr.getBytes("Args"));
                result.add(taskArgs);
            }
            return result;
        } catch (Exception e) {
            LOGGER.error("poolList error", e);
        }
        return result;
    }

    @Override
    public TaskArgs pool(int partition) {
        try {
            TaskArgs taskArgs = null;
            String sql = String.format(SQL_GET, partition);
            Database database = getSharding(partition);
            DataTable dataTable = database.executeTable(sql, 1);

            for (DataRow dr : dataTable.getRows()) {
                taskArgs = new TaskArgs();
                taskArgs.setId(dr.getString("TaskId"));
                taskArgs.setTid(dr.getLong("Tid"));
                taskArgs.setEventName(dr.getString("EventName"));
                taskArgs.setArgStr(dr.getBytes("Args"));
            }
            return taskArgs;
        } catch (Exception e) {
            LOGGER.error("poolList error", e);
        }
        return null;
    }

    @Override
    public void delete(int partition, TaskArgs taskArgs) {
        try {
            if (taskArgs == null) {
                return;
            }
            String sql = String.format(SQL_DELETE, partition);
            Database database = getSharding(partition);
            database.executeNonQuery(sql, taskArgs.getTid());
        } catch (Exception e) {
            LOGGER.error("delete error!taskId = {};e", taskArgs.getId(), e);
        }
    }

    @Override
    public void delete(int partition, List<TaskArgs> list) {
        try {
            if (list == null || list.size() == 0){
                return;
            }
            String sql = String.format(SQL_DELETE_LIST, partition);
            Database database = getSharding(partition);
            database.executeNonQuery(sql, 0, list.size());
        } catch (SQLException e) {
            LOGGER.error("delete error!partition = {};e", partition, e);
        }
    }

    @Override
    public boolean isEmpty(int partition) {
        return false;
    }
}

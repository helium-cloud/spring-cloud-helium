package org.helium.redis.widgets.redis.client;

import com.feinno.superpojo.SuperPojoManager;
import com.feinno.superpojo.util.StringUtils;
import org.helium.database.DataRow;
import org.helium.database.DataTable;
import org.helium.database.Database;
import org.helium.database.spi.DatabaseManager;
import org.helium.framework.configuration.FieldLoader;
import org.helium.framework.entitys.SetterNode;
import org.helium.redis.widgets.redis.client.sentinel.PropertyItem;
import org.helium.redis.widgets.redis.client.sentinel.RoleConfig;
import org.helium.redis.widgets.redis.client.sentinel.RoleConfigItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yibo on 2017-3-29.
 */
public class SentinelClusterLoader2 implements FieldLoader {

    @Override
    public Object loadField(SetterNode node) {
        try {
            // suport file xml config
            String nodeInnerText = node.getInnerText();
            if (StringUtils.isNullOrEmpty(nodeInnerText)) {
                throw new Exception("nodeInnerText should not null");
            }
            RoleConfig roleConfig = SuperPojoManager.parseXmlFrom(nodeInnerText, RoleConfig.class);
            String roleName = roleConfig.getRoleName();
            List<CFG_RedisSentinels> list = getItemsFromFileConfig(roleConfig);
            RedisSentinelClient2 client = RedisSentinelManager2.INSTANCE.getRedisClient(roleName, list);
            return new SentinelCluster2(client);
        } catch (Exception ex) {
            throw new RuntimeException("SentinelClusterLoader2 loadField RedisCluster failed:", ex);
        }
    }


    private DbTableRole parseFieldText(String nodeText) throws Exception {

        String[] items = nodeText.split(":");
        DbTableRole result = new DbTableRole(items[0], items[1], items[2]);
        return result;
    }


    private boolean isDBConfig(String path) {
        if (path.indexOf(":") != -1) {
            return true;
        }

        return false;
    }

    private boolean isFileConfig(String path) {
        if (path.indexOf("/") != -1) {
            return true;
        }

        return false;
    }


    private List<CFG_RedisSentinels> getItemsFromFileConfig(RoleConfig roleConfig) {
        List<CFG_RedisSentinels> result = new ArrayList<>();

        for (RoleConfigItem configItem : roleConfig.getItems()) {
            CFG_RedisSentinels item = new CFG_RedisSentinels();

            item.setRoleName(roleConfig.getRoleName());
            item.setPolicy(configItem.getPolicy());
            item.setNodeOrder(configItem.getNodeOrder());
            item.setAddrs(configItem.getMasterAddr());

            String propertiesExt = buildPropertyString(configItem.getPropertyItems());

            item.setPropertiesExt(propertiesExt);

            item.setWeight(configItem.getWeight());
            item.setEnabled(configItem.getEnabled());
            item.setMasterName(configItem.getMasterName());
            result.add(item);
        }

        return result;
    }

    private String buildPropertyString(List<PropertyItem> items) {
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (PropertyItem item : items) {
            if (count > 0) {
                sb.append("\n");
            }

            sb.append(item.getKey() + "=" + item.getValue());
            ++count;
        }

        String result = sb.toString();
        return result;
    }

    private List<CFG_RedisSentinels> getItemsFromDB(DbTableRole dbTableRole) throws Exception {

        String sql = "SELECT * FROM `" + dbTableRole.getTableName() + "` WHERE Enabled='1' and RoleName='" + dbTableRole.getRoleName() + "'";

        Database v6_faedb = DatabaseManager.INSTANCE.getDatabase(dbTableRole.getDbName());

        DataTable dataTable = v6_faedb.executeTable(sql);

        List<CFG_RedisSentinels> result = new ArrayList<>();

        for (DataRow row : dataTable.getRows()) {

            CFG_RedisSentinels item = new CFG_RedisSentinels();

            item.setRoleName(row.getString("RoleName"));
            item.setPolicy(row.getString("Policy"));
            item.setNodeOrder(row.getInt("NodeOrder"));
            item.setAddrs(row.getString("Addrs"));

            item.setPropertiesExt(row.getString("PropertiesExt"));
            item.setWeight(row.getInt("Weight"));
            item.setEnabled(row.getInt("Enabled"));
            item.setMasterName(row.getString("MasterName"));
            result.add(item);
        }

        return result;
    }
}
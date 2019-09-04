package org.helium.database.spi;

import com.alibaba.fastjson.JSONObject;
import org.helium.database.ConnectionString;
import org.helium.database.Database;
import org.helium.database.DatabaseOperator;
import org.helium.framework.configuration.FieldLoader;
import org.helium.framework.entitys.SetterNode;
import org.helium.framework.entitys.SetterNodeLoadType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by Coral on 8/15/16.
 */
public class DatabaseOperatorLoader implements FieldLoader {
	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseFieldLoader.class);

	@Override
	public Object loadField(SetterNode node) {
		String dbName = node.getInnerText();
		DatabaseOperator databaseOperator = null;
		try {
			String dbStr = node.getValue();
			SetterNodeLoadType loadType = node.getLoadType();
			ConnectionString cs = null;
			switch (loadType) {
				//配置中心或者value加载
				case CONFIG_VALUE:
					cs = ConnectionString.fromText(dbStr);
					databaseOperator = DatabaseManager.INSTANCE.getDatabaseOperator(dbName, cs);
					break;
				//动态加载
				case CONFIG_DYNAMIC:
					cs = ConnectionString.fromText(dbStr);
					databaseOperator = DatabaseManager.INSTANCE.getDatabaseOperator(dbName, cs);
					break;
				//helium加载
				case CONFIG_PROVIDE:
				case UNKNOWN:
					databaseOperator = DatabaseManager.INSTANCE.getDatabaseOperator(dbName, null);
					break;
				default:
					databaseOperator = DatabaseManager.INSTANCE.getDatabaseOperator(dbName, null);
					break;
			}
		} catch (IOException e) {
			LOGGER.error("loadField{},", JSONObject.toJSONString(node), e);
		}
		return databaseOperator;
	}
}

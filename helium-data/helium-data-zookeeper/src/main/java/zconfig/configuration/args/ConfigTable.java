package zconfig.configuration.args;

import zconfig.configuration.ConfigurationManager;

import java.util.*;
import java.util.Map.Entry;

/**
 * 
 * 抽象配置表, 可以从配置库中直接映射出来
 * 
 * @auther gaolei
 */
public class ConfigTable<K, V extends ConfigTableItem>
{
	private Date version;
	private String tableName;
	private Hashtable<K, V> innerTable;
	private Class<K> keyType;
	private Class<V> valueType;

	public ConfigTable(String tableName, Hashtable<K, V> table, Date version, Class<K> keyType, Class<V> valueType)
	{
		this.tableName = tableName;
		this.innerTable = table;
		this.version = version;
		this.keyType = keyType;
		this.valueType = valueType;
	}

	public String getTableName()
	{
		return tableName;
	}

	public void setTableName(String tableName)
	{
		this.tableName = tableName;
	}

	public Date getVersion()
	{
		return version;
	}

	public void setVersion(Date version)
	{
		this.version = version;
	}

	public ConfigTable(String name, Date version)
	{
		tableName = name;
		this.version = version;
	}

	/**
	 *
	 * 查不到Key的话, 会报异常, 如果不希望得到异常, 请用tryGet方法
	 * @param key
	 * @return
	 * @throws IllegalArgumentException
	 */
	public V get(K key)
	{
		V val;
		if (innerTable.containsKey(key)) {
			val = innerTable.get(key);
			return val;
		} else {
			throw new IllegalArgumentException("TABLE: " + tableName + " Key: " + key.toString());
		}
	}

	/**
	 *
	 * 不存在的值会返回空, 如果希望抛出异常, 请用get()方法
	 * @param key
	 * @return
	 */
	public V tryGet(K key)
	{
		return innerTable.get(key);
	}

	public Enumeration<K> getKeys()
	{
		return innerTable.keys();
	}

	public Collection<V> getValues()
	{
		return innerTable.values();
	}

	public int getCount()
	{
		return innerTable.size();
	}

	public Set<Entry<K, V>> getSet()
	{
		return innerTable.entrySet();
	}

	public Hashtable<K, V> getHashtable()
	{
		return innerTable;
	}

	public void runAfterLoad() throws Exception
	{
		for (Entry<K, V> e : innerTable.entrySet())
			((ConfigTableItem) e.getValue()).afterLoad();
	}

	public void addEvent(final ConfigUpdateAction<ConfigTable<K, V>> updateCallback) throws ConfigurationException
	{
		ConfigurationManager.getInstance().callBackConfigData(keyType, valueType, this.getTableName(), updateCallback);
	}
}

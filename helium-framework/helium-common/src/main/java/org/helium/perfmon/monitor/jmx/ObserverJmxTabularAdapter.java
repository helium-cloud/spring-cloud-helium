package org.helium.perfmon.monitor.jmx;

import org.helium.superpojo.type.TimeSpan;
import org.helium.perfmon.observation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.*;
import javax.management.modelmbean.DescriptorSupport;
import javax.management.openmbean.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Feinno Observer 性能监控 JMX 适配器。
 * <p>
 * 该适配器主要用来实现Feinno Observer 与标准 JMX 性能监控模式之间的对接，当有请求的时候实现自动的数据跟踪，长时间无数据请求时，自动终止数据采集，节约系统性能
 * <p>
 * Created by Coral on 2015/8/10.
 */
public class ObserverJmxTabularAdapter implements DynamicMBean {

    private final static Logger logger = LoggerFactory.getLogger(ObserverJmxTabularAdapter.class);

    /**
     * 创建并注册一个JMX适配器到指定的 {@link MBeanServer} 中，如果过程中有异常会被准换成 {@link RuntimeException}
     *
     * @param mBeanServer       mBeanServer
     * @param category          监控条目名称
     * @param inspectorInterval 数据采集周期，单位是秒
     * @param keepingAliveTime  在无数据请求的情况下持续采集的时间长度，单位是秒
     * @return 新创建的JMX适配器实例
     */
    public static ObserverJmxTabularAdapter register(MBeanServer mBeanServer, String category, int inspectorInterval, int keepingAliveTime) {
        ObserverJmxTabularAdapter adapter = new ObserverJmxTabularAdapter(category, inspectorInterval, keepingAliveTime);
        register(mBeanServer, adapter);
        return adapter;
    }

    /**
     * 注册一个指定的JMX适配器到指定的 {@link MBeanServer} 中，如果过程中有异常会被准换成 {@link RuntimeException}
     *
     * @param mBeanServer mBeanServer
     * @param adapter     JMX适配器实例
     */
    public static void register(MBeanServer mBeanServer, ObserverJmxTabularAdapter adapter) {
        try {
            mBeanServer.registerMBean(adapter, adapter.getObjectName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 停止并从指定 {@link MBeanServer} 注销指定的JMX适配器，如果过程中有异常会被准换成 {@link RuntimeException}
     *
     * @param mBeanServer mBeanServer
     * @param adapter     JMX适配器实例
     */
    public static void unregister(MBeanServer mBeanServer, ObserverJmxTabularAdapter adapter) {
        if (adapter != null) {
            adapter.shutdown();
            try {
                mBeanServer.unregisterMBean(adapter.getObjectName());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    Observable observable;

    final Object obLock = new Object();

    String category;

    MBeanInfo mBeanInfo;

    CompositeType rowType;

    String[] rowItemNames;

    TabularType tabularType;

    final Object typeLock = new Object();

    AtomicBoolean reporting = new AtomicBoolean(false);

    AtomicBoolean ready = new AtomicBoolean(false);

    List<ObserverReportColumn> columns;

    boolean active = true;

    long deadLine;

    long keepingAliveTime;

    long inspectorInterval;

    private List<ObserverReportRow> lastReportData;

    public ObserverJmxTabularAdapter(String category, int inspectorInterval, int keepingAliveTime) {

        if (category == null || category.isEmpty()) {
            throw new IllegalArgumentException("The category must not null or empty");
        }

        this.category = category;
        this.inspectorInterval = inspectorInterval * TimeSpan.SECOND_MILLIS;
        this.keepingAliveTime = keepingAliveTime * TimeSpan.SECOND_MILLIS;
    }

    @Override
    public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
        if (!category.equals(attribute)) {
            throw new AttributeNotFoundException("Cannot find " + attribute + " attribute ");
        }
        if (orderReport() && lastReportData != null) {
            TabularData result = new TabularDataSupport(tabularType);
            for (ObserverReportRow row : lastReportData) {
                Object[] values = new Object[rowItemNames.length];
                values[0] = row.getInstanceName();
                int i = 1;
                try {
                    for (String valueStr : row.getData()) {
                        values[i] = columns.get(i - 1).getType().parse(valueStr);
                        i++;
                    }
                    result.put(new CompositeDataSupport(rowType, rowItemNames, values));
                } catch (Exception e) {
                    logger.error("Build Performance attribute fault.", e);
                    break;
                }
            }
            return result;
        } else {
            return null;
        }
    }

    @Override
    public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        throw new UnsupportedOperationException("Setting attribute is not supported");
    }

    @Override
    public AttributeList getAttributes(String[] attributes) {
        AttributeList resultList = new AttributeList();
        if (attributes.length == 0){
			return resultList;
		}

        for (String attribute : attributes) {
            try {
                Object value = getAttribute(attribute);
                resultList.add(new Attribute(attribute, value));
            } catch (Exception e) {
                logger.error("Build Performance attribute fault.", e);
            }
        }
        return (resultList);
    }

    @Override
    public AttributeList setAttributes(AttributeList attributes) {
        throw new UnsupportedOperationException("Setting attribute is not supported");
    }

    @Override
    public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException, ReflectionException {
        throw new UnsupportedOperationException("Invoking is not supported");
    }

    @Override
    public MBeanInfo getMBeanInfo() {
        MBeanInfo result;
        if (mBeanInfo != null) {
            result = mBeanInfo;
        } else if (initialize()) {
            OpenMBeanAttributeInfoSupport[] attributes = new OpenMBeanAttributeInfoSupport[1];
            attributes[0] = new OpenMBeanAttributeInfoSupport(category,
                    String.format("Table of %s monitor data", category),
                    tabularType, true, false, false);
            result = new OpenMBeanInfoSupport(this.getClass().getName(),
                    String.format("%s %s", category, this.getClass().getSimpleName()),
                    attributes, null, null, null);
        } else {
            Descriptor beanDescriptor = new DescriptorSupport();
            beanDescriptor.setField("ready", false);
            result = new MBeanInfo(category,
                    String.format("%s %s (not ready)", category, this.getClass().getSimpleName()),
                    null, null, null, null, beanDescriptor);
        }
        return result;
    }

    /**
     * 停止数据采集，通常于注销之前调用
     */
    public void shutdown() {
        active = false;
    }

    /**
     * 获取用来注册JMX 的 {@link ObjectName}
     *
     * @return JMX ObjectName 对象实例
     */
    public ObjectName getObjectName() {
        try {
            return new ObjectName(String.format("org.helium.observer.tabular:type=%s", category));
        } catch (MalformedObjectNameException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 尝试初始化监控条目的相关信息以及相关返回值等信息，如果找不到监控条目则肯定无法初始化成功
     * <p>
     * 该方法可以返回调用从而保证 JMX适配器 和 监控条目 之间没有创建顺序要求
     *
     * @return 是否初始化成功，如果成功则返回true否则返回false
     */
    private boolean initialize() {
        if (!ready.get()) {
            if (observable == null) {
                synchronized (obLock) {
                    if (observable == null) {
                        observable = ObserverManager.getObserverItem(category);
                    }
                }
            }
            if (observable != null) {
                try {
                    initDataType();
                    ready.set(true);
                } catch (OpenDataException e) {
                    logger.error("Type info initialize fault.", e);
                }

            }
        }
        return ready.get();
    }


    /**
     * 订阅数据采集，最新采集的数据会存储到 {@link #lastReportData} 里面，如果已经订阅了，则会重置订阅生存时间，使订阅持续有效
     * <p>
     * 该方法会自动调用初始化方法 {@link #initialize()}
     *
     * @return 如果订阅成功返回true，否则返回false
     */
    boolean orderReport() {
        if (!initialize() || !active) {
            return false;
        }

        // delay deadline
        deadLine = System.currentTimeMillis() + keepingAliveTime;
        logger.info("{} inspector dead line delay to {}", category, deadLine);

        if (!reporting.get()) {
            reporting.set(true);
            TimeSpan timeSpan = new TimeSpan(inspectorInterval);
            logger.info("Add {} inspector timeSpan={}", category, timeSpan);
            ObserverManager.addInspector(observable, ObserverReportMode.ALL, timeSpan, report -> {
                if (System.currentTimeMillis() > deadLine || !active) {
                    logger.info("Shutdown {} inspector ", category);
                    reporting.set(false);
                    lastReportData = null;
                    return false;
                } else {
                    if (!report.getRows().isEmpty()) {
                        lastReportData = report.getRows();
                    }
                    return true;
                }
            });
        }
        return true;
    }


    /**
     * 初始化 JMX 数据请求返回结果的数据类型信息
     *
     * @throws OpenDataException
     */
    private void initDataType() throws OpenDataException {

        if (rowType == null || tabularType == null) {
            synchronized (typeLock) {
                if (rowType == null || tabularType == null) {

                    columns = observable.getObserverColumns();
                    int columnCount = columns.size() + 1;
                    OpenType[] itemTypes = new OpenType[columnCount];
                    rowItemNames = new String[columnCount];
                    String[] itemDescriptions = new String[columnCount];
                    itemTypes[0] = SimpleType.STRING;
                    rowItemNames[0] = "InstanceName";
                    itemDescriptions[0] = "The monitor instance name";
                    int i = 1;
                    for (ObserverReportColumn column : columns) {
                        rowItemNames[i] = columnToAttr(column.getName());
                        itemDescriptions[i] = column.getName();
                        itemTypes[i] = transformType(column.getType());
                        i++;
                    }

                    rowType = new CompositeType("MonitorRowData", "Monitor data for an row", rowItemNames, itemDescriptions, itemTypes);
                    tabularType = new TabularType("MonitorTabular", "Table of monitor data", rowType, new String[]{"InstanceName"});
                }
            }
        }
    }

    /**
     * 把 监控条目数据类型对象 {@link ObserverReportColumnType} 转换为对应的 JMX数据类型对象 {@link OpenType}
     *
     * @param columnType 监控条目数据类型对象
     * @return JMX数据类型对象
     */
    static OpenType transformType(ObserverReportColumnType columnType) {
        switch (columnType) {
            case DOUBLE:
            case RATIO:
                return SimpleType.DOUBLE;
            case LONG:
                return SimpleType.LONG;
            default:
                return SimpleType.STRING;
        }
    }

    static final Pattern wordPattern = Pattern.compile("\\w+");

    static public String columnToAttr(String columnName) {
        Matcher matcher = wordPattern.matcher(columnName);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String word = matcher.group();
            sb.append(Character.toUpperCase(word.charAt(0)));
            if (word.length() > 1) {
                sb.append(word.substring(1));
            }
        }
        if (sb.length() == 0) {
            throw new IllegalArgumentException(String.format("Invalid column name '%s'", columnName));
        } else {
            return sb.toString();
        }
    }
}

package zconfig.configuration.args;

import org.helium.util.Func;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InvalidObjectException;
import java.lang.reflect.Field;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;


public class SearchIndex<T>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchIndex.class);

    private Field[] fields;

    private SortedMap<IndexObject, T> indexEntrys = new TreeMap<IndexObject, T>();

    private String strClassName = null; // 存放被检索对象类名

    /**
     * 构造函数，构造函数，根据提供的对象类型作为查询条件的字段构造一个有序的SortedMap，之后必须初始化被检索对象列表才能使用，
     * 即build方法必须执行。
     *
     * @param clazz
     *            被检索对象类型
     * @param indexFields
     *            检索条件字段
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     */
    public SearchIndex(Class<T> clazz, String... indexFields)
    {
        this(clazz, null, indexFields);
    }

    /**
     * 构造函数，根据提供的对象类型、对象列表和作为查询条件的字段构造一个有序的SortedMap
     *
     * @param clazz
     *            被检索对象类型
     * @param list
     *            被排序、检索的对象列表
     * @param indexFields
     *            检索条件字段
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     */
    public SearchIndex(Class<T> clazz, Iterable<T> list, String... indexFields)
    {
        if (clazz == null) {
            throw new IllegalArgumentException("parameter clazz is null");
        }

        strClassName = clazz.getName();
        fields = new Field[indexFields.length];
        for (int i = 0; i < indexFields.length; i++) {
            try {
                if (clazz.getDeclaredField(indexFields[i]) != null) {
                    fields[i] = clazz.getDeclaredField(indexFields[i]);
                }
            } catch (NoSuchFieldException e) {
                //
                // 此处加NoSuchFieldException的异常捕获，然后又抛出RuntimeException
                // 目的是格式化异常提示信息并避免无意义的Exception声明
                String strMsg = String.format("Fields Not Found: %s.%s", clazz.getName(), indexFields[i]);
                throw new IllegalArgumentException(strMsg);
            }
        }

        if (list != null) {
            build(list);
        }
    }

    /**
     * 构造Key、Value形式的数据，写入SortedMap
     *
     * @param list
     *            被排序、检索的对象列表
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public void build(Iterable<T> list)
    {
        int serial = 0;
        indexEntrys.clear();
        for (T item : list) {
            Comparable[] vals = new Comparable[fields.length];
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                try {
                    vals[i] = (Comparable) fields[i].get(item);
                } catch (Exception e) {
                    LOGGER.error("Never got here {}", e);
                    throw new RuntimeException("SearchIndex reflection error" + e.toString());
                }
            }
            IndexObject indexObj = new IndexObject(vals, 0);
            if (indexEntrys.containsKey(indexObj)) {
                serial++;
                indexObj.serialNo = serial;
            }
            indexEntrys.put(indexObj, item);
        }
    }

    /**
     * 根据查询条件，查询第一个对象
     *
     * @param startFields
     *            以此值开始的条件字段，参数个数小于等于初始化SearchIndex时指定的条件参数字段个数。
     * @return
     */
    public T findFirst(String... startFields)
    {
        List<T> rets = find(startFields);
        if (rets.size() == 0) {
            return null;
        }
        return rets.get(0);
    }

    /**
     * 根据查询条件，查询所有符合条件的对象
     *
     * @param startFields
     * @return List<T>
     */

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<T> find(Comparable... startFields)
    {
        if (startFields.length > fields.length) {
            throw new IllegalArgumentException("Too many search field");
        }

        List<T> list = new ArrayList<T>();
        if (indexEntrys.size() == 0)
            return list;

        Comparable[] values = new Comparable[fields.length];
        for (int i = 0; i < startFields.length; i++) {
            values[i] = startFields[i];
        }

        IndexObject startObject = new IndexObject(values, 0);

        int start = searchFirstItem(startObject);
        if (start < 0)
            return list;

        Object[] arrKeySet = indexEntrys.keySet().toArray();// 得到keySet组成的数组

        if (!((IndexObject) arrKeySet[start]).startsWith(startObject))
            start++;

        for (int i = start; i < indexEntrys.size() && ((IndexObject) arrKeySet[i]).startsWith(startObject); i++) {
            list.add(indexEntrys.get((IndexObject) arrKeySet[i]));
        }

        return list;
    }

    /**
     * 二分查找的业务逻辑
     *
     * @param indexObj
     * @return
     */
    @SuppressWarnings("unchecked")
    private int searchFirstItem(IndexObject indexObj)
    {
        int index;
        int begin = 0;
        int end = indexEntrys.size() - 1;

        IndexObject obj;
        Object[] arrKeySet = indexEntrys.keySet().toArray();// 得到keySet组成的数组
        while (end - begin > 1) {
            int middle = (end + begin) / 2;
            obj = (IndexObject) arrKeySet[middle];

            int compareResult = indexObj.compareTo(obj);
            if (compareResult == 0) {
                index = middle;
                return index;
            } else {
                if (compareResult > 0) {
                    begin = middle;
                } else {
                    end = middle;
                }
            }
        }

        if (end == begin) {
            if (indexObj.compareTo(arrKeySet[begin]) >= 0)
                return begin;
        } else {
            if (indexObj.compareTo(arrKeySet[begin]) >= 0)
                return begin;

            if (indexObj.compareTo(arrKeySet[end]) >= 0) {
                return end;
            }
        }
        return 0;
    }

    public List<T> findKeys(int keyFieldCount, String... startFields)
    {
        return findKeys(keyFieldCount, null, startFields);
    }

    /**
     * 根据查询条件取出符合条件的对象，进而对符合条件的对象进行自定义的处理，对处理结果返回true的对象，组成List返回。
     * 此处的自定义处理过程必须实现Func接口的exec()方法。
     *
     * @param keyFieldCount
     *            作为查询条件的字段数，其取值范围在参数startFields和构造方法参数indexFields的长度之间，包含边界值。
     * @param func
     *            实现Func接口的对象
     * @param startFields
     *            以此值开始的条件字段，参数个数小于等于初始化SearchIndex时指定的条件参数字段个数。
     * @return List<T>
     */

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<T> findKeys(int keyFieldCount, Func<T, Boolean> func, String... startFields)
    {
        List<T> list = new ArrayList<T>();
        if (indexEntrys.size() == 0)
            return list;

        if (startFields.length > fields.length) {
            LOGGER.error("Too many search fields: Index<" + strClassName + ">");
            return list;
        }

        if (keyFieldCount > fields.length) {
            LOGGER.error("Too many key fields: Index<" + strClassName + "> key:" + keyFieldCount);
            return list;
        }

        if (keyFieldCount < startFields.length) {
            LOGGER.error("KeyFieldCount can not less than startFields: Index<" + strClassName + "> key:"
                    + keyFieldCount);
            return list;
        }

        Comparable[] values = new Comparable[fields.length];
        for (int i = 0; i < startFields.length; i++) {
            values[i] = startFields[i];
        }
        IndexObject startObject = new IndexObject(values, 0);
        IndexObject lastObject = null;

        Object[] arrKeySet = indexEntrys.keySet().toArray();// 得到keySet组成的数组

        for (int i = 0; i < indexEntrys.size(); i++) {
            IndexObject keyObjects = (IndexObject) arrKeySet[i];
            if (keyObjects.startsWith(startObject)) {
                if (lastObject == null || !keyObjects.startsWith(lastObject, keyFieldCount)) {
                    if (func != null && func.exec(indexEntrys.get((IndexObject) arrKeySet[i]))) {
                        list.add(indexEntrys.get((IndexObject) arrKeySet[i]));
                        lastObject = keyObjects;
                    }
                }
            }
        }
        return list;
    }

    @SuppressWarnings("rawtypes")
    class IndexObject implements Comparable
    {
        private Comparable[] objects;
        private int serialNo;

        public IndexObject(Comparable[] objects, int serialNo)
        {
            this.objects = objects;
            this.serialNo = serialNo;
        }

        /**
         * 对象比较的业务逻辑
         *
         * @param obj
         * @return
         */
        @SuppressWarnings("unchecked")
        public int compareTo(Object obj)
        {
            IndexObject val = (IndexObject) obj;
            if (val == null)
                try {
                    throw new InvalidObjectException("Can't Compare IndexObject With: " + obj.getClass());
                } catch (InvalidObjectException e) {
                    throw new InvalidParameterException("Can't Compare IndexObject");
                }
            for (int i = 0; i < objects.length; i++) {
                if (this.objects[i] == null) {
                    if (val.objects[i] == null)
                        continue;
                    else
                        return -1;
                } else {
                    if (val.objects[i] == null)
                        return 1;
                    else {
                        int c = this.objects[i].compareTo(val.objects[i]);
                        if (c == 0)
                            continue;
                        else
                            return c;
                    }
                }
            }
            return this.serialNo - val.serialNo;
        }

        public boolean startsWith(IndexObject prefix)
        {
            return startsWith(prefix, objects.length);
        }

        @SuppressWarnings("unchecked")
        public boolean startsWith(IndexObject prefix, int length)
        {
            for (int i = 0; i < length; i++) {
                if (prefix.objects[i] != null) {
                    if (objects[i].compareTo(prefix.objects[i]) == 0)
                        continue;
                    else
                        return false;
                } else {
                    break;
                }
            }
            return true;
        }

        public String toString()
        {
            StringBuilder str = new StringBuilder();
            for (Object obj : objects) {
                str.append(obj.toString());
            }
            return str.toString();
        }
    }
}
package org.helium.perfmon;

import org.helium.perfmon.annotation.PerformanceCounter;
import org.helium.perfmon.observation.ObserverReportColumn;
import org.helium.util.Tuple2;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 计数器分类的模板类
 *
 * Created by Coral
 */
public class CounterCategorySchema {
    private Class<?> clazz;
    private List<ObserverReportColumn> columns;
    private Map<String, Tuple2<Field, CounterBuilder>> counters;

    Field[] getPerformanceCounterFields() {
        List<Field> result = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(PerformanceCounter.class)) {
                result.add(field);
            }
        }
        Class<?> superclass = clazz.getSuperclass();
        while (superclass != null) {
            for (Field field : superclass.getDeclaredFields()) {
                if (field.isAnnotationPresent(PerformanceCounter.class)) {
                    result.add(field);
                }
            }
            superclass = superclass.getSuperclass();
        }
        return result.toArray(new Field[result.size()]);
    }

    public CounterCategorySchema(String name, Class<?> clazz) {
        this.clazz = clazz;
        counters = new HashMap<>();
        columns = new ArrayList<>();
        Field[] fs = getPerformanceCounterFields();
        AccessibleObject.setAccessible(fs, true);

        for (Field f : fs) {
            PerformanceCounter anno = f.getAnnotation(PerformanceCounter.class);

            if (counters.get(anno.name()) != null) {
                throw new IllegalArgumentException("duplicate counter name =" + anno.name());
            }

            CounterBuilder builder = PerformanceCounterFactory.getBuilder(anno.type());
            counters.put(anno.name(), new Tuple2<Field, CounterBuilder>(f, builder));

            for (ObserverReportColumn column : builder.getColumns(anno.name())) {
                columns.add(column);
            }
        }
    }

    public CounterCategoryInstance createCounterInstance(String instance) {
        Object cc;
        try {
            cc = clazz.newInstance();
            AbstractCounterEntity[] entities = new AbstractCounterEntity[counters.keySet().size()];
            int i = 0;
            Field[] fs = getPerformanceCounterFields();
            for (Field fc : fs) {
                PerformanceCounter anno = fc.getAnnotation(PerformanceCounter.class);

                for (String key : counters.keySet()) {
                    if (key != null && key.equals(anno.name())) {
                        Tuple2<Field, CounterBuilder> item = counters.get(key);
                        Field f = item.getV1();
                        CounterBuilder builder = item.getV2();
                        AbstractCounterEntity e = builder.createCounter();
                        entities[i++] = e;
                        f.set(cc, e);
                        break;
                    }

                }
            }

            return new CounterCategoryInstance(entities, cc, instance);
        } catch (Exception e) {
            throw new IllegalArgumentException("unable to create instance " + clazz.getName(), e);
        }
    }

    public List<ObserverReportColumn> getObserverColumns() {
        return columns;
    }
}

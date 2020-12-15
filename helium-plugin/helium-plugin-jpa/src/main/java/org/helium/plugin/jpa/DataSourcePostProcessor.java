//package org.helium.plugin.jpa;
//
//import com.allstar.cintracer.CinTracer;
//import org.helium.plugin.jpa.DatabaseSelector;
//import com.allstart.common.jpa.dao.GenericDAO;
//import com.allstart.common.jpa.holder.DaoHolder;
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.config.BeanPostProcessor;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import java.lang.reflect.Field;
//import java.util.Arrays;
//import java.util.Objects;
//
//@Component
//public class DataSourcePostProcessor implements BeanPostProcessor {
//
//    @Resource
//    private DaoHolder daoHolder;
//
//    private final static CinTracer LOGGER = CinTracer.getInstance(DataSourcePostProcessor.class);
//
//
//    @Override
//    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
//        Field[] fields = bean.getClass().getDeclaredFields();
//        Arrays.stream(fields).filter(field -> Objects.nonNull(field.getAnnotation(DatabaseSelector.class))).forEach(field -> {
//            if (!field.isAccessible()){
//                field.setAccessible(true);
//            }
//            DatabaseSelector databaseSelector = field.getAnnotation(DatabaseSelector.class);
//            GenericDAO genericDAO = daoHolder.getGenericDAO(databaseSelector);
//
//            try {
//                field.set(bean,genericDAO);
//            }catch (Exception e){
//                LOGGER.error("DataSourcePostProcessor ", e);
//            }
//        });
//        return bean;
//    }
//
//}

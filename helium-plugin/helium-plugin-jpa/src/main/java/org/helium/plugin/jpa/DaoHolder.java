//package org.helium.plugin.jpa;
//
//import org.helium.plugin.jpa.DatabaseSelector;
//import com.allstart.common.jpa.dao.GenericDAO;
//import com.allstart.common.jpa.processor.DataSourceScanPostProcessor;
//import org.helium.cloud.configcenter.ConfigCenterClient;
//import org.helium.database.ConnectionString;
//import org.helium.database.Database;
//import org.helium.database.spi.DatabaseManager;
//import org.hibernate.FlushMode;
//import org.hibernate.SessionFactory;
//import org.hibernate.cfg.Configuration;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.config.BeanDefinition;
//import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
//import org.springframework.orm.hibernate5.SessionFactoryUtils;
//import org.springframework.stereotype.Component;
//
//import javax.persistence.Table;
//import javax.sql.DataSource;
//import java.io.IOException;
//import java.util.Iterator;
//import java.util.Map;
//import java.util.Objects;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Component
//public class DaoHolder {
//
//    @Autowired
//    private ConfigCenterClient configCenterClient;
//
//
//	private static final Logger LOGGER = LoggerFactory.getLogger(DaoHolder.class);
//
//    private static final Map<String, GenericDAO> DAO_CACHE = new ConcurrentHashMap<>(16);
//
//    public GenericDAO getGenericDAO(DatabaseSelector databaseSelector) {
//        GenericDAO genericDAO = DAO_CACHE.get(getKey(databaseSelector));
//        if (Objects.isNull(genericDAO)) {
//            genericDAO = createGenericDAO(databaseSelector);
//            DAO_CACHE.put(getKey(databaseSelector), genericDAO);
//        }
//        return genericDAO;
//    }
//
//    private GenericDAO createGenericDAO(DatabaseSelector databaseSelector) {
//        GenericDAO genericDAO = new GenericDAO();
//
//        try {
//            LocalSessionFactoryBean factory = new LocalSessionFactoryBean();
//            DataSource dataSource = getDataSource(databaseSelector);
//            factory.setDataSource(dataSource);
//            factory.afterPropertiesSet();
//            Configuration configuration = factory.getConfiguration();
//            if (DataSourceScanPostProcessor.classHashSet != null){
//                Iterator<BeanDefinition> iterator = DataSourceScanPostProcessor.classHashSet.iterator();
//                while(iterator.hasNext()){
//                    BeanDefinition beanDefinition = iterator.next();
//                    try {
//                        Class classEntity = Class.forName(beanDefinition.getBeanClassName());
//                        Table table = (Table) classEntity.getAnnotation(Table.class);
//                        if (table == null){
//                            continue;
//                        }
//                        configuration.addAnnotatedClass(classEntity);
//                    } catch (ClassNotFoundException e) {
//                        LOGGER.error("add class Exception:{}", beanDefinition.getBeanClassName(), e);
//                    }
//                }
//            }
//
//
//            SessionFactory sessionFactory = configuration.buildSessionFactory();
//            genericDAO.setSessionFactory(sessionFactory);
//        } catch (IOException e) {
//            LOGGER.error("createGenericDAO Exception", e);
//        }
//
//        return genericDAO;
//    }
//
//    private DataSource getDataSource(DatabaseSelector databaseSelector) throws IOException {
//        String value = configCenterClient.get(databaseSelector.dataSourceName(), databaseSelector.group());
//        ConnectionString connectionString = ConnectionString.fromText(value);
//        Database database = DatabaseManager.INSTANCE.getDatabase(databaseSelector.dataSourceName(), connectionString);
//
//        if (database == null){
//            return null;
//        }
//        return database.getDataSource();
//    }
//
//    public String getKey(DatabaseSelector databaseSelector){
//        return databaseSelector.group() + "-" + databaseSelector.dataSourceName();
//    }
//
//
//}

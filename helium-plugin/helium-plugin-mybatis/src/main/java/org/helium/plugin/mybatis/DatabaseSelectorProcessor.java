package org.helium.plugin.mybatis;


import org.helium.plugin.mybatis.loader.ConfigLoader;
import org.helium.plugin.mybatis.model.ConfigModel;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

@Component
@DependsOn("springContextHelper")
public class DatabaseSelectorProcessor implements BeanPostProcessor {

    private static final String CLOUD = "cloud";

    @Resource
    private ConfigLoader configLoader;

    /**
     * BeanPostProcessor为spring的容器级接口，所有bean初始化之前，都要调用该方法
     *
     * @param bean     Bean
     * @param beanName beanName
     * @return Bean
     * @throws BeansException 异常
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof MapperFactoryBean) {
            MapperFactoryBean mapperFactoryBean = (MapperFactoryBean) bean;
            Class<?> mapperInterface = mapperFactoryBean.getMapperInterface();
            MapperSelector selector = mapperInterface.getAnnotation(MapperSelector.class);
            if (Objects.nonNull(selector)) {
                ConfigModel config = configLoader.load(selector.dataSourceName(), CLOUD);
                // 设置数据源
                MultipleDataSource.setDataSource(selector.dataSourceName(), config);
                // 设置事务管理器，为了简化开发，事务管理器名称与application.yml里面配置的数据源名称保持一致
                DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(MultipleDataSource.instance());
                SpringContextHelper.putBean(selector.dataSourceName(), transactionManager);
            }
        }
        return bean;
    }


}

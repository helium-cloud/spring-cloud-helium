package org.helium.plugin.mybatis;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.Properties;

/**
 * mybatis插件，用来实现数据源的动态切换，拦截mybatis的Executor的query与update方法
 */

@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
})
public class DataSourceSelectorInterceptor implements Interceptor {

    /**
     * 拦截后执行的方法
     *
     * @param invocation Invocation
     * @return 继续执行
     * @throws Throwable 异常，在这不处理
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 获取参数,在这主要是要获取MappedStatement对象，这个对象里面封装了mapper执行的具体方法
        MappedStatement statement = (MappedStatement) invocation.getArgs()[0];
        // id为方法全名称
        String id = statement.getId();
        // 进行切割，拿到mapper接口的完整名称
        int lastIndex = id.lastIndexOf(".");
        // 拿到mapper接口的完整名称
        String interfaceName = id.substring(0, lastIndex);
        try {
            // 加载mapper接口
            Class<?> mapperClass = Class.forName(interfaceName);
            // 获取当前接口的MapperSelector注解，该注解属性本质上与当前dataSource保持一致
            MapperSelector selector = mapperClass.getAnnotation(MapperSelector.class);
            // 设置数据源
            MultipleDataSource.updateCurrentDataSource(selector.dataSourceName());
            return invocation.proceed();
        } finally {
            // 移除数据源，再这移除的原因的底层是用ThreadLocal做了缓存，如果不移除会造成内存泄露
            MultipleDataSource.removeCurrentDataSource();
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }


}

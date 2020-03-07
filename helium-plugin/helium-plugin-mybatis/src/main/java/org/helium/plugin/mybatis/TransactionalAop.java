package org.helium.plugin.mybatis;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 事务拦截AOP
 */
@Component
@Aspect
@Order(Integer.MIN_VALUE)
public class TransactionalAop {

    /**
     * 拦截点标有Transactional注解的所有方法，在这拦截的原因是：
     * 此时mapper方法未执行，所以DatabaseSelectorProcessor还未生效
     * 事务管理方式采用的spring的DataSourceTransactionManager
     * 但是事务管理器DataSourceTransactionManager此时需要打开连接，设置autoCommit为false，后续也需要commit与rollback
     */
    @Pointcut("@annotation(org.springframework.transaction.annotation.Transactional)")
    public void transactionalPointCut() {
    }

    @Around("transactionalPointCut()")
    public Object transactional(ProceedingJoinPoint point) throws Throwable {
        Method method = ((MethodSignature) point.getSignature()).getMethod();
        Transactional transactional = method.getAnnotation(Transactional.class);
        if (Objects.isNull(transactional)) {
            return point.proceed();
        }
        try {
            String transactionalManagerName;
            if (!StringUtils.isEmpty(transactional.value())) {
                transactionalManagerName = transactional.value();
            } else {
                transactionalManagerName = transactional.transactionManager();
            }
            if (StringUtils.isEmpty(transactionalManagerName)) {
                throw new BusinessException("未配置事务管理器");
            }
            // 设置数据源
            MultipleDataSource.updateCurrentDataSource(transactional.value());
            return point.proceed();
        } finally {
            // 移除当前线程的datasource，不移除会造成内存泄露
            MultipleDataSource.removeCurrentDataSource();
        }
    }
}

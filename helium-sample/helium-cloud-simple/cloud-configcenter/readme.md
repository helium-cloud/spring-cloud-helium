### 1.动态配置支持如下数据类型
    kafka
    redis
    db
    普通配置
    
    支持自定义扩展
    
### 2.支持多种配置中心
    zk[已测试]
    nacos[已测试]
    apollo[已测试]
    
### 3.切换方式
    gradle依赖变更
    配置中心url自定义替换
    
### 4.
    gradle
    
    //    compile group: 'org.apache.dubbo', name: 'dubbo-configcenter-apollo', version: dubbo_version
        compile group: 'org.apache.dubbo', name: 'dubbo-configcenter-nacos', version: dubbo_version
    //    compile group: 'org.apache.dubbo', name: 'dubbo-configcenter-zookeeper', version: dubbo_version
    
    config
    #    #compile group: 'org.apache.dubbo', name: 'dubbo-configcenter-zookeeper', version: dubbo_version使用zk为配置中心
    #    url: zookeeper://127.0.0.1:7998
    
        #compile group: 'org.apache.dubbo', name: 'dubbo-configcenter-nacos', version: dubbo_version使用nacos为注册中心
        url: nacos://127.0.0.1:8848?config.namespace=cloud&config.group=cloud
    
    #    #compile group: 'org.apache.dubbo', name: 'dubbo-configcenter-apollo', version: dubbo_version使用apollo为注册中心
    #    url: apollo://10.10.208.194:7080?config.namespace=cloud&config.group=cloud&app.id=cincloud
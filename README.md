### 1.helium结构图

![](resources/helium-cloud.png)

系统结构图

### 2.helium使用

#### 2.1 数据组件
1.依赖

```

dependencies {
    compile group: 'org.helium', name: 'helium-data-base',version: 3.0-dev-1909161114
    compile group: 'org.helium', name: 'helium-data-redis',version: 3.0-dev-1909161114
    compile group: 'org.helium', name: 'helium-cloud-starter',version: 3.0-dev-1909161114
}

```

2.使用

```java
    @FieldSetter(value = "testdb", group = "test")
    private Database database;
    
    application.yml:添加
    test:
      testdb: local:db/testdb.properties

    不填写支持迁移配置中心：nacos、zk等
```

参考：helium/helium-sample/helium-cloud-simple/cloud-configcenter

#### 2.1 task组件

1.依赖

```

dependencies {
    compile group: 'org.helium', name: 'helium-cloud-task',version: 3.0-dev-1909161114
    compile group: 'org.helium', name: 'helium-cloud-starter',version: 3.0-dev-1909161114
}

```

2.使用

```java
    
    @TaskEvent(SimpleCloudTask.TAG)
    private TaskProducer<SimpleArgs> simpleCloudTask;
    
    
    @TaskImplementation(event = SimpleCloudTask.TAG)
    public class SimpleCloudTask implements Task<SimpleArgs> {
        public static final String TAG  ="Task:SimpleCloudTask";
        private static final Logger LOGGER = LoggerFactory.getLogger(SimpleCloudTask.class);
    
    	@Override
    	public void processTask(SimpleArgs args) {
    		LOGGER.info("SimpleCloudTask task exec:{}", JSONObject.toJSONString(args, true));
    	}
    }

```

参考：helium/helium-sample/helium-cloud-simple/cloud-task

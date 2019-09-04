# Helium

# helium-framework-old

# helium-dashboard

# helium-server

# helium-http

# foundation


### 使用Task实现调用解耦

在很多业务场景，我们都需要一种先返回应答，然后再处理后续业务流程操作的需求。Task可以视为一种提供将业务前段的操作进行解耦的机制，用于实现通知、日志等异步处理的场合。

**首先实现消费者**，下面是一个实现日志记录的Task的实现代码，实现Task消费者的一些注意事项如下:

- @TaskImplementation注解用于标注一个Task实现，event是事件名，beanId默认与event名字相同，但也可设置不同的名字
- Task靠event的名字来进行生产与消费的处理，允许多个生产者对应一个消费者，但对于同一个event名称，只允许存在一个Task
- Task实现类需要实现Task<E>的泛型接口，E为Args的类型，实现类需要实现processTask(Args args)方法，在processTask方法完成Task的实际处理
- Task的Args类型如果需要跨进程投递，需要从SuperPojo派生，进程内操作没有此限制

```java
@TaskImplementation(event = SampleLogTask.EVENT_NAME)
public class SampleLogTask implements Task<SampleLogTaskArgs> {
	public static final String EVENT_NAME = "quickstart:SampleLogTask";
	public static final Logger LOGGER = LoggerFactory.getLogger(SampleLogTask.class);
 	
	@FieldSetter("SAMPLEDB")
	private Database db;
	
	@Override
	public void processTask(SampleLogTaskArgs args) {
		try {
			String sql = "insert into UserLogs (time, clientIp, action, user) values (?, ?, ?, ?)";
			db.executeInsert(sql, new Date(), args.getClientIp(), args.getAction(), args.getUser().toJsonObject().toString());
		} catch (Exception ex) {
			LOGGER.error("processLogTaskFailed {}", ex);
		}
	}
}
```
*代码参考helium-sample/org.helium.sample.quickstart.SampleTaskLog*

**完成生产者的注入**，TaskProducer<E>接口用于声明并注入TaskProducer，参照下面的代码，

- @TaskEvent(eventName)注解用于标注在TaskProducer<E>的字段上，用于完成setter注入
- 使用TaskProducer<E>.produce(E args);方法完成event的生产，这个过程会快速返回，真正的event处理在另外的线程运行，不会影响到主业务流程。

```
@ServletImplementation(id = "quickstart:SampleServlet3")
@HttpMappings(contextPath = "/quickstart", urlPattern = "/sample3")
public class SampleHttpServlet3 extends HttpServlet {
	...
	
	@TaskEvent(SampleLogTask.EVENT_NAME)
	private TaskProducer<SampleLogTaskArgs> logTask;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			...
			
			SampleLogTaskArgs logArgs = new SampleLogTaskArgs();
			logArgs.setClientIp(req.getRemoteAddr());
			logArgs.setAction("GET");
			logArgs.setUser(user);
			
			logTask.produce(logArgs);
			...
	}
}
```
**代码参考helium-sample/org.helium.sample.quickstart.SampleHttpServlet3**

bootstrap.xml，只需要将SampleLogTask放入beans加载过程即可，使用Bootstrap3启动
```
<bootstrap id="bootstrap-3">
	...
    <!-- 需要添加的beans -->
    <beans>
        <!-- 加载bean的实现类以及想要嵌入的ServletStack -->
        <bean class="org.helium.sample.quickstart.SampleHttpServlet3" stacks="http"/>
        <!-- 加载service的实现类 -->
        <bean class="org.helium.sample.quickstart.SampleServiceImpl"/>
        <!-- 加载SampleLogTask -->
        <bean class="org.helium.sample.quickstart.SampleLogTask"/>
    </beans>
</bootstrap>
```

### 访问Redis



```
	@FieldSetter("SAMPLERD")
	private RedisClient redis;
```

```
```

### 实现一个Module

``` 
```

### 编写bundle.xml，并将模块打包

当发布到环境上时，我们需要将放在src/main/resources目录的META-INF子目录下面

bundle.xml

```
```

### 完善bootstrap.xml

```
```

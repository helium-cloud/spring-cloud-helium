# Helium² Quick Start

## 1. 快速创建一个平台应用

### 1.1. 开发工具的选择

- 使用SUN JDK 8
- 建议使用Intellij Idea作为开发工具
- 建议使用Gradle作为集成工具

*以上工具请到官网下载最新版本*

### 1.2. 工程的创建

- 如果你比较熟悉 `Helium²`， 可以选择手工创建工程，并引用适当的版本，当前推荐的版本为 `2.1.9`
- 如果打算创建一个新工程，强烈推荐使用 `项目原型脚手架`，它可以助你快速的创建一个标准的 Helium 项目：

	1. 使用SSH客户端连接 `10.0.2.74` 用户名和密码都是 `pcreator`
	2. 成功登录后执行创建命令 `create helium-service`
	3. 根据提示填写项目的关键信息（如项目名，命名空间等）
	4. 等待生成操作完成，访问生成器给出的 `url` 下载生成好的项目包
	5. 解压工程包并保存到合适的位置，使用你喜欢的IDE导入项目

	（想知道更多关于快速创建项目的信息，请参考 [项目原型脚手架](http://git.feinno.com/pub/gradle-archetype)）

### 1.3. 预备工作

- `helium-framework-old` 大量使用了 `helium-superpojo` 工程提供的序列化能力，再继续阅读此指南之前，请先阅读 [SuperPojo手册](http://git.feinno.com/pub/helium-superpojo)，学习编写`SuperPojo 实体类`，了解 `protobuf2`, `json`, `xml` 等序列化格式的区别
- 了解 `Restful` 接口的设计和访问方式
- 对 `Gradle` 的使用有基本理解 *[Gradle使用指南](http://git.feinno.com/pub/guidelines/wikis/how-to-use-gradle)*

在Gradle配置中加入以下依赖

```groovy
dependencies {
    compile group: 'org.slf4j', name: 'slf4j-api', version: slf4j_version
    compile group: 'org.helium', name: 'helium-superpojo', version: super_pojo_version
    compile group: 'org.helium', name: "foundation", version: foundation_version
    compile group: 'org.helium', name: 'helium-framework-old', version: helium_version
    compile group: 'org.helium', name: 'helium-http', version: helium_version
    compile group: 'org.helium', name: 'helium-data-services', version: helium_version
    compile group: 'org.helium', name: 'helium-dashboard', version: helium_version
    testCompile group: 'junit', name: 'junit', version: junit_version
}
```

`gradle` 的 `versions` 定义在工程根目录下的 `gradle.properties` 文件中, 参考如下

```properties
slf4j_version = 1.7.12
junit_version = 4.11
super_pojo_version = 2.0.6
helium_version = 2.3.0-SNAPSHOT
foundation_version = 2.3.0-SNAPSHOT
```

*本指南中的代码请下载helium源代码，参考其中的`helium-sample`模块*

### 1.4. 实现一个HttpServlet

- 我们从实现一个简单的`HttpServlet`开始我们的构建过程,
- 参考代码位置在`helium-sample`工程下的`org.helium.sample.quickstart.SampleHttpServelt1`
- 首先创建`SampleHttpServlet1`派生自标准的`javax.servlet.http.HttpServlet`类，也可以派生自`org.helium.http.servlet.HttpServlet`，后者提供了更便于实现业务的接口
- 在类上添加`@ServletImplementation(id = "quickstart:SampleServlet1")`，这个 annotation 会将这个类标记为一个 Servlet 类型的 bean，以便于注入器完成加载
- 在类上添加`@HttpMappings(contextPath = "/quickstart", urlPattern = "/sample1")`，表明这个 servlet 的访问路径
- 添加一个字段`name`，并在字段上使用`@FieldSetter("${USER_NAME}")`标注，在 bean 的注入时，将会自动完成 setter 注入
- 覆盖`HttpServlet的doGet()`方法

```java
/**
 * HttpServlet例子
 * Created by Coral on 5/11/15.
 */
@ServletImplementation(id = "quickstart:SampleServlet1")
@HttpMappings(contextPath = "/quickstart", urlPattern = "/sample1")
public class SampleHttpServlet1 extends HttpServlet {
	/**
	 * 注入
	 */
	@FieldSetter("${USER_NAME}")
	private String name;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.getOutputStream().println("Hello: " + name);
		resp.getOutputStream().close();
	}
}
```

### 1.5. 配置bootstrap.xml并启动Bootstrap

完成这个 Servlet 实现后，就可以尝试启动这个bean了，在helium框架中，启动一个bean需要使用 `Bootstrap类` 以及 `bootstrap.xml` 配置，下面来讲解一下 `bootstrap.xml` 的构成。

- `enviorments` 节用于配置环境变量，以 `key-value` 的方式配置，可以用`${VAR}`的方式引用其他的变量
- `Bootstrap` 启动时会先加载本机的环境变量，
- `${LOCAL_IP}` 这个字段是自动变量，如果本机可以连上公网的话，会自动检测本机的公开IP，一般用于开发环境调试
- `stacks` 配置服务启动的 `ServletStack` ，目前系统中实现的stacks有 `http`, `rpc`, `sip` 等协议，同样类型的stack可以启动多个(端口不能冲突)，需要配置唯一的`id`
- `beans` 节，用于配置需要加载的 `bean`，需要设置实现类，如果启动的是 `Servlet` 类型，需要在 `stacks` 上表明在哪个 `stack` 上加载

```xml
<bootstrap id="bootstrap-1">
    <!-- 环境变量配置 -->
    <environments imports="">
        <variable key="PRIVATE_IP" value="${LOCAL_IP}"/>
        <variable key="RPC_URL" value="${PRIVATE_IP}:${RPC_PORT}"/>
        <variable key="RPC_PORT" value="7023"/>
        <variable key="HTTP_PORT" value="8301"/>
        <variable key="HTTP_DASH_STACK" value="http"/>
        <variable key="USER_NAME" value="Leon"/>
    </environments>
    <!-- 需要启动的ServletStack -->
    <stacks>
        <stack id="http" class="org.helium.http.servlet.HttpServletStack">
            <setters>
                <setter field="host">${PRIVATE_IP}</setter>
                <setter field="port">${HTTP_PORT}</setter>
            </setters>
        </stack>
    </stacks>
    <!-- 需要添加的beans -->
    <beans>
	    <!-- 配置bean的实现类以及想要嵌入的ServletStack -->
        <bean class="org.helium.sample.quickstart.SampleHttpServlet1" stacks="http"/>
    </beans>
</bootstrap>
```
*参考`bootstrap-1.xml`*

- `Bootstrap` 启动器的 `Bootstrap.INSTANCE.addPath()` 方法，用于添加配置的识别目录，在开发环境启动的时候参考示例进行配置
- `initialize()` 方法会开始注入器加载

```java
/**
 * Quickstart教程启动器
 * Created by Coral on 6/15/17.
 */
public class Bootstrap1 {
	public static void main(String[] args) throws Exception {
		Bootstrap.INSTANCE.addPath("helium-sample");
		Bootstrap.INSTANCE.initialize("bootstrap-1.xml", true, false);
	    Bootstrap.INSTANCE.run();	
	}
}
```
*参考`Bootstrap1`*

当在Console上看到以下输出的时候，就可以判断启动成功了，访问 `http://10.10.9.71:8301/quickstart/sample1`，可以看到期望的结果

```
[WARN] 15:45:43.889 org.helium.framework.spi.Bootstrap/(main-1): >>> ================= BOOTSTRAP Start Finished ================= <<<
[WARN] 15:45:43.889 org.helium.framework.spi.Bootstrap/(main-1): >>> listening: http://10.10.9.71:8301
```

### 1.6. 定义一个Service

在服务开发时，除了 `Servlet` 外，我们还需要设计分层的业务或DAO组件，或设计对外提供RPC服务，helium-framework-old类似于Spring，是一个使用依赖注入完成装配的框架，在使用helium框架进行应用开发的时候，我们应当遵循以下设计规则

- 接口与实现分离，不要使用 `Singleton`
- 使用 `@Initializor` 完成Bean的初始化，避免使用 `static initializor`

实现一个`service`，首先定义一个`interface`，需要在interface上添加`@ServiceInterface`，关注以下几个注意事项

- 与servlet一样，`beanId`需要唯一
- 如果service需要以RPC的方式暴露给其他服务访问，则service的调用返回参数必须为基础类型或SuperPojo类型
- 通过`AsyncResult<E>`可以实现异步调用，参考`helium-sample`中的`SampleAsyncService`方法 (2.3.0版本)

```java
@ServiceInterface(id = "quickstart:SampleService")
public interface SampleService {
	SampleUser getUser(int userId) throws Exception;
}
```
*代码参考`org.helium.sample.quickstart.SampleService`*

### 1.7. 实现Service，并在其中访问数据库

当定义好接口后，实现需要实现定义好的service interface, 参照后面的代码，这个例子通过读取数据库完成接口中定义的 `getUser()` 功能，关于`@ServiceImplementation` 和 `@ServiceInterfac`批注，有如下注意事项

- `@ServiceImplementation` 是接口的实现标注，不能省略，以下是一些细节的注意事项
	- 实现class实现的interface接口上没有`@ServiceInterface`标注，需要添加`@ServiceImplementation(interfaceType=...)`
	- 目前不支持一个实现类实现多个`interface`
	- `beanId`会取实现的interface上的`@ServiceInterface`批注中的`id()`，如果id属性为空，实现时必须在`@ServiceImplementation`上提供`id()`
	- 针对同一个`interface`可以在一个工程中完成多个`id相同的实现`，比如一个正常实现和一个挡板实现，可以在`bootstrap.xml`或`bundle.xml`中决定在运行时使用哪个实现
	- 通过`@ServiceImplementation`批注的`id()`，可以指定bean的id，如: `@ServiceImplementation(id = "quickstart:SampleService2")`，这个方法可以针对一个service添加两个不同id的实现
- `@Initializer` 可以标注在一个`public void xxx()`类型的方法上，在bean加载后运行，完成bean内部的初始化功能
	- TODO: @Initializer现在是没有调用优先级的，需要各个bean独立完成初始化
- `@Initializer` 在任何bean中都可以使用（Servlet，Service, Task...)

```java
@ServiceImplementation
public class SampleServiceImpl implements SampleService {
	// 通过Setter注入，添加一个数据库访问类
	@FieldSetter("SAMPLEDB")
	private Database db;
	
	@Initializer
	public void initialize() {
		// DO INITIALIZE
	}
	
	@Override
	public SampleUser getUser(int userId) throws Exception {
		DataTable table = db.executeTable("select * from Users where id = ?", userId);
		
		if (table.getRowCount() == 0) {
			return null;
		}

		DataRow row = table.getRow(0);
		SampleUser user = new SampleUser();
		user.setId(row.getInt("id"));
		user.setName(row.getString("name"));
		user.setRole(row.getString("row"));
		return user;
	}
}
```
*代码参考`org.helium.sample.quickstart.SampleServiceImpl`*

添加数据库访问，通过`setter注入`方式添加数据库访问，代码如下

- `Database` 是一个含连接池并且线程安全的 `JDBC包装类`
- helium采用约定的方式进行配置路径的管理，例子中的`SAMPLEDB`表示使用`config/db/SAMPLEDB.properties`连接串中制定的数据库

```java
	// 通过Setter注入，添加一个数据库访问类
	@FieldSetter("SAMPLEDB")
	private Database db;
```

*本例子中连接串位置在`helium-sample/src/main/resources/config/db/SAMPLEDB.properties`*

### 1.8. 完成Service的注入，通过Bootstrap完成启动

Service注入通过`@ServiceSetter`批注完成，代码如下，几个注意事项

- 可以使用`id()`参数制定beanId，例如：`@ServiceSetter(id = "quickstart:SampleService2")`
- 如果`interface`类型上存在含有`id()`的`@ServiceInterface`标注，则不需要在`@ServiceSetter`上声明id，否则需要指定id

```java
	@ServiceSetter
	private SampleService sampleService;
```
*代码参照`org.helium.sample.quickstart.SampleHttpServlet2`*

参考`bootstrap-1.xml`修改`bootstrap-2.xml`

```xml
<bootstrap id="bootstrap-2">
	...
    <!-- 需要添加的beans -->
    <beans>
        <!-- 配置bean的实现类以及想要嵌入的ServletStack -->
        <bean class="org.helium.sample.quickstart.SampleHttpServlet2" stacks="http"/>
        <!-- 配置service的实现类 -->
        <bean class="org.helium.sample.quickstart.SampleServiceImpl" stacks="http"/>
    </beans>
</bootstrap>
```

编写启动器类`Bootstrap2`

```java
public class Bootstrap2 {
	public static void main(String[] args) throws Exception {
		Bootstrap.INSTANCE.addPath("helium-sample");
		Bootstrap.INSTANCE.addPath("helium-sample/build/resources/main/config"); // 将config目录加载到配置路径中
		Bootstrap.INSTANCE.initialize("bootstrap-2.xml", true, false);
	    Bootstrap.INSTANCE.run();
	}
}
```

启动成功后访问: `http://10.10.9.71:8301/quickstart/sample2?id=1` 查看结果

### 1.9. 实现一个RestfulService[TODO]

**helium从2.3.0版本开始支持RestfulService**

在这个例子中，我们首先实现一个简单的RestfulService

- 1. 定义接口, 接口的返回值类型必须为基础类型或SuperPojo类型

```

```

*关于Restful的Annotation定义，参考JSR-311标准*

- 2. 完成实现

```
```

- 关于beanId的解释：在helium框架中，任何一个提供功能的Class均视为一个bean，beanId的格式为group:identity，同一个环境中，不允许存在两个相同id的bean。helium组被用于框架内部建议不使用

### 1.10. 使用Task实现调用解耦

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
*代码参考org.helium.sample.quickstart.SampleLogTask*

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
*代码参考org.helium.sample.quickstart.SampleHttpServlet3*

配置参考`bootstrap-3.xml`，只需要将SampleLogTask放入beans加载过程即可，使用`Bootstrap3`启动

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

### 1.11. 访问Redis

访问Redis和访问数据库类似，通过`setter注入`方式添加数据库访问，代码如下

```java
	@FieldSetter("SAMPLERD")
	private RedisClient redis;
```

** 完整实例代码参考helium-sample/org.helium.sample.quickstart.SampleHttpServlet3**

- `RedisClient ` 是一个含连接池并且线程安全的 `Redis包装类`
- `RedisSentinelClient` 也可以按照类似方法使用，但连接串有差别，请联系李宏博   
- helium采用约定的方式进行配置路径的管理，例子中的`SAMPLERD`表示使用`config/redis/SAMPLERD.properties`连接串中制定的Redis

一个可用的Redis连接串如下，

```
testOnBorrow=false
testOnReturn=false
testWhileIdle=true
maxTotal=5
maxIdle=5
minIdle=5
maxWait=-1
whenExhaustedAction=1
timeBetweenEvictionRunsMillis=30000
numTestsPerEvictionRun=-1
minEvictableIdleTimeMillis=-1
softMinEvictableIdleTimeMillis=540000
host=192.168.156.16
port=6379
database=0
```

*TODO: 关于RedisClient和RedisSentinelClient的连接串是有区别的，有些混乱，等待文档和规范补全*

### 1.12. 编写bundle.xml，并将模块打包

当需要发布时，更好的方式是将工程打包为独立的bundle，bundle.xml放在src/main/resources目录的META-INF子目录下面

按照上面的例子bundle.xml如下

```xml
<!-- name是bundle的名字，尽量唯一 -->
<bundle name="quickstart" version="2.1.9">
    <!-- configImports节用于表示在bundle内需要引用的配置 -->
    <configImports>
        <configImport key="HTTP_STACK"/>
    </configImports>
    <beans>
    <!-- 加载bean的实现类以及想要嵌入的ServletStack -->
        <bean class="org.helium.sample.quickstart.SampleHttpServlet3" stacks="${HTTP_STACK}"/>
        <!-- 加载service的实现类 -->
        <bean class="org.helium.sample.quickstart.SampleServiceImpl"/>
        <!-- 加载SampleLogTask -->
        <bean class="org.helium.sample.quickstart.SampleLogTask"/>
    </beans>
</bundle>
```

### 1.13. 完善bootstrap.xml，并添加dashboard

修改bootstrap.xml如下，只用jar包的方式直接加载bundle，并且启动了dashboard，启动成功后，可以访问以下链接来使用dashboard的功能

- http://127.0.0.1:8301/perfmon/ - 应用性能计数器
- http://127.0.0.1:8301/dashboard/ - Beans的启动情况

```xml
<bootstrap id="bootstrap-4">
    <!-- 环境变量配置 -->
    <environments imports="">
        <variable key="PRIVATE_IP" value="${LOCAL_IP}"/>
        <variable key="RPC_URL" value="${PRIVATE_IP}:${RPC_PORT}"/>
        <variable key="RPC_PORT" value="7023"/>
        <variable key="HTTP_PORT" value="8301"/>
        <!-- helium-dashboard~.jar 需要的配置变量 -->
        <variable key="HTTP_DASH_STACK" value="http"/>
        <!-- helium-sample~.jar 需要的配置变量 -->
        <variable key="HTTP_STACK" value="http" />
    </environments>
    <!-- 需要启动的ServletStack -->
    <stacks>
        <stack id="http" class="org.helium.http.servlet.HttpServletStack">
            <setters>
                <setter field="host">${PRIVATE_IP}</setter>
                <setter field="port">${HTTP_PORT}</setter>
            </setters>
        </stack>
    </stacks>
    <bundles>
        <!-- 从helium-sample-2.1.9-SNAPSHOT.jar中加载，"~"符号用于版本号通配 -->
        <bundle path="helium-sample~.jar"/>
        <!-- 从helium-dashboard-2.1.9-SNAPSHOT.jar中加载，"~"符号用于版本号通配 -->
        <bundle path="helium-dashboard~.jar"/>
    </bundles>
</bootstrap>
```

- *启动代码参考`Bootstrap4`,配置代码为`bootstrap-4.xml`*
- *注: 你可能需要先执行helium-sample及helium-dashboard工程的`gradle build`来生成在{module}/build/libs目录下生成jar包*
- *启动时可能会报缺失perfmon.xml的错误，不影响正常启动，请暂时忽略*

### 1.14. 发布到环境

为了简化发布流程，我们提供了基于 `Gradle` 的工程打包发布插件 `org.helium.tsp.release`

*此处提供的范例比较简略，更多详尽的内容请参考 [Gradle使用指南](http://git.feinno.com/pub/guidelines/wikis/how-to-use-gradle)*

简要步骤如下：

1. 配置部署用帐号（该操作只需要进行一次，全局长期有效）

```properties
# vim ~/.gradle/gradle.properties
# 配置你的Feinno邮箱帐号
tspDeployUsername=xxxx@feinno.com
tspDeployPassword=xxxxxxx
```

2. 编写部署用sls文件到环境指定的部署配置仓库，sls文件是yaml格式，请务必注意缩进

```yaml
my-service:                   # 部署的服务实例名
  tsp.installed:
    - namespace: ott          # 匹配部署全局配置用的命名空间（例如部署目录、配置库位置、启动文件名等等）
    - version: latest         # 部署的服务版本
    - config_revision: master # 部署的服务配置版本（配置库基于git，可以使用分支、tag、commit-id）
    - java_args:
      - -Xmx1024m
      - -Xms1024m
    - port:
        rpc: 7002
        sip: 6060
        http_dash: 9002
    - log_level: INFO
```

3. 编辑 `build.gradle` 配置插件

```groovy
buildscript {
	// 配置下载插件的仓库集(这个仓库集只会在下载插件时生效)
	repositories {
		maven { url "http://repo.feinno.com/nexus/content/groups/public/" }
	}
	// 配置插件包的定位信息(一个插件包中可能会包含若干个插件)
	dependencies {
		classpath "org.helium.gradle:tsp-gradle-plugin:1.0-SNAPSHOT"
	}
}
// 启用 TspRelease 插件
apply plugin: 'org.helium.tsp.release'

// ... 项目配置 ...

// 测试环境部署位置
tspDeploy {
	location = 'fetion-functional-test' // 部署目标环境ID
	args target : '10.10.0.100,10.10.0.101' // 部署目标服务器 minionId，多个目标可用逗号分割
	args sls_repo: 'ott' // sls 文件源 （作用类似之前的 namespace 参数）
}
```

4. 在项目根目录执行如下命令进行部署

```
gradle clean publish deployBySalt
```

### 1.15. 启动过程中的常见排错处理

根目录的logging.xml配置了记录日志的方式, 请先查看logging.xml的配置级别和输出是否正确。

```xml
<!-- 总体日志级别 -->
<logging level="INFO">
    <!-- 设置子logger级别 -->
    <subLevels>
        <subLevel name="com.feinno.superpojo" level="INFO"/>
        <subLevel name="org.test.logger" level="INFO"/>
        <subLevel name="org.helium.framework" level="INFO" output="HELIUM"/>
    </subLevels>
    <!-- 设置日志输出-->
    <outputs>
        <output>
            <appender class="org.helium.logging.spi.ConsoleAppender"/>
            <appender class="org.helium.logging.spi.TextAppender">
                <setters>
                    <setter field="path">/tmp</setter>
                    <setter field="fileFormat">log_${DATE}.log</setter>
                </setters>
            </appender>
        </output>
        <output name="HELIUM">
            <appender class="org.helium.logging.spi.ConsoleAppender"/>
            <appender class="org.helium.logging.spi.TextAppender">
                <setters>
                    <setter field="path">/tmp</setter>
                    <setter field="dateFormat">yyyyMMdd</setter>
                    <setter field="fileFormat">LOG_helium_${DATE}.log</setter>
                </setters>
            </appender>
        </output>
    </outputs>
    <!-- 过滤器 -->
    <filters>
        <filter loggerName="org.test.logger." class="org.helium.logging.spi.SimpleMarkerFilter" params="foo"/>
    </filters>
</logging>

```

常见启动排错过程

**TODO**

## 2. 实现不同服务间的协作 

### 2.1 将Service接口通过Rpc方式暴露给其他服务

我们基于1.8章节的内容，进行改动，参考`Bootstrap2_1`及`bootstrap-2-1.xml`与`bootstrap-2.xml`对比，有以下改动

- 增加Rpc协议栈一节
- -

```xml
<bootstrap id="bootstrap-2-1">
    <!-- 环境变量配置 -->
    <environments imports="">
        <variable key="PRIVATE_IP" value="${LOCAL_IP}"/>
        <variable key="RPC_URL" value="${PRIVATE_IP}:${RPC_PORT}"/>
        <variable key="RPC_PORT" value="7023"/>
        <variable key="HTTP_PORT" value="8301"/>
        <variable key="HTTP_DASH_STACK" value="http"/>
        <variable key="USER_NAME" value="Leon"/>
    </environments>
    <!-- 需要启动的ServletStack -->
    <stacks>
        <stack id="http" class="org.helium.http.servlet.HttpServletStack">
            <setters>
                <setter field="host">${PRIVATE_IP}</setter>
                <setter field="port">${HTTP_PORT}</setter>
            </setters>
        </stack>
        <!-- 开启对外监听的RPC协议栈 -->
        <stack id="rpc" class="org.helium.framework.rpc.RpcServerStack">
            <setters>
                <setter field="host">${PRIVATE_IP}</setter>
                <setter field="port">${RPC_PORT}</setter>
            </setters>
        </stack>
    </stacks>
    <!-- 需要添加的beans -->
    <beans>
        <!-- 加载bean的实现类以及想要嵌入的ServletStack -->
        <bean class="org.helium.sample.quickstart.SampleHttpServlet2" stacks="http"/>
        <!-- 加载service的实现类，并对外输出 -->
        <bean class="org.helium.sample.quickstart.SampleServiceImpl" export="true"/>
    </beans>
</bootstrap>
```

启动后rpc的地址为`tcp://127.0.0.1:7023/quickstart.SampleService`，如果在其他服务中使用透明Rpc方式调用SampleService接口，代码参考如下，其中服务名为beanId将":"改为"."，比如beanId为"quickstart:SampleService"，暴露的rpcServiceName为"quickstart.SampleService"

```
		SampleService service = RpcProxyFactory.getTransparentProxy("tcp://127.0.0.1:7023/quickstart.SampleService", SampleService.class);
		SampleUser user = service.getUser(1);
		System.out.println(user.toJsonObject().toString());
```

*代码参考`Bootstrap2_1`*


### 2.2. 通过Rpc引用其他Service

除了上一章中提供的使用RpcProxyFactory.getTransparentProxy方式得到透明代理方式外，还可以使用注入的方式完成加载，参考`bootstrap-3-1.xml`配置文件

- 每个被引用的Service都必须显示在<references/>中声明
- 如果配置了<reference/>节点中endpoints字段，则服务会注入到固定位置如果不配置，则使用ZK完成配置

```
<bootstrap id="bootstrap-3-1">
    <!-- 环境变量配置 -->
    <environments imports="">
        <variable key="PRIVATE_IP" value="${LOCAL_IP}"/>
        <variable key="RPC_URL" value="${PRIVATE_IP}:${RPC_PORT}"/>
        <variable key="RPC_PORT" value="7024"/>
        <variable key="HTTP_PORT" value="8302"/>
        <variable key="HTTP_DASH_STACK" value="http"/>
        <variable key="USER_NAME" value="Leon"/>
    </environments>
    <!-- 需要启动的ServletStack -->
    <stacks>
        <stack id="http" class="org.helium.http.servlet.HttpServletStack">
            <setters>
                <setter field="host">${PRIVATE_IP}</setter>
                <setter field="port">${HTTP_PORT}</setter>
            </setters>
        </stack>
    </stacks>
    <!-- 加载需要注入的引用 -->
    <references>
        <!-- id: 引用的beanId; interface: 引用的接口; endpoints: 如果指明了，则使用静态地址，如果未指明，使用zk中同步的地址 -->
        <reference id="quickstart:SampleService" interface="org.helium.sample.quickstart.SampleService" endpoints="tcp://127.0.0.1:7023;protocol=rpc"/>
    </references>
    <!-- 需要添加的beans -->
    <beans>
        <!-- 加载bean的实现类以及想要嵌入的ServletStack -->
        <bean class="org.helium.sample.quickstart.SampleHttpServlet3" stacks="http"/>
        <!-- 加载SampleLogTask -->
        <bean class="org.helium.sample.quickstart.SampleLogTask"/>
    </beans>
</bootstrap>
```

*使用`Bootstrap3_1`来完成启动*


### 2.3. 配置CentralizedService, 实现基于Zookeeper的服务发现与集成

服务发现和服务访问可以通过Zookeeper完成，通过在bootstrap.xml中添加<centralizedService/>配置节，参考`bootstrap-2-2.xml`及`bootstrap-3-2/xml` 

```xml
<bootstrap>
	...
    <!-- 添加基于Zk的中心化服务 -->
    <centralizedService class="org.helium.framework.route.center.ZkCentralizedService" enabled="$true">
        <setters>
            <setter field="zkHosts">192.168.156.16:7998</setter>
        </setters>
    </centralizedService>   
    ...
</bootstrap>
```

*使用`Bootstrap2_2`和`Bootstrap3_2`启动*

## 3. 进阶话题与最佳实践

进阶话题的的示例代码路径在`helium-sample/org.helium.sample.advanced`包中，从进阶话题开始，不再为每个章节提供单独的`Bootstrap`及`bootstrap-xxx.xml`，统一使用`BootstrapAdvanced`及`bootstrap-advanced.xml`进行加载，示例的访问方式请参照各个章节内部的说明。

### 3.1. 理解环境变量的配置方式

环境变量指的是可以在`bootstrap.xml`，`bundle.xml`中进行配置，并可上述配置及`FieldSetter`中使用的形如`${Variable}`格式的变量，变量类型都是字符串格式，环境变量可以使用以下方式进行配置

- 1. 继承操作系统环境变量，参考linux环境变量的常见配置方法，一般来讲需要在环境变量中提供`${PRIVATE_IP}`及`${PUBLIC_IP}`作为本服务器的私有地址和共有地址
- 2. 配置在`bootstrap.xml`的`<enviorments/>`节点中，以`<variable key="RPC_PORT" value="7023"/>`的方式进行配置，此配置可以引用环境中已有的变量
- 3. 使用形如`<enviorments imports="env.xml,biz/sims/env/xml" `的imports方式进行导入，导入的文件使用","进行分割，从左到右进行导入，最后导入`<enviornments>`自身节点中的变量，注意后倒入的变量会覆盖先倒入的变量，也就是`<enviornments>`节点中变量最后覆盖

```xml
<!-- 能够import的env.xml的格式 -->
<environments>
    <variable key="ENABLE_ZK" value="true"/>
    <variable key="ZK_HOSTS" value="192.168.156.16:7998"/>
    <variable key="HA_CENTER_RPC" value="tcp://192.168.143.144:8888"/>
</environments>
```

- 4. 在`bundle.xml`的`<configImports>`节中可以指定此bundle依赖的环境变量有哪些，可以指定默认值，使用`<configImport key="USER_NAME" default="DefaultName" />`这种方式进行配置。
- 5. 除以上显式配置的变量外，`helium-framework-old`还支持一些自动变量，如下:
	* `${LOCAL_IP}`: 在可以访问公网的机器上运行，可以自动探测本机的公开IP
	* `${AUTO_PORT}`: 检测本机尚未监听的端口号
	* `${NEW_GUID}`: 生成一个随机的GUID


**关于配置变量使用的最佳实践**

- 一般使用GIT来管理配置目录，参考这个工程http://git.feinno.com/ngcc-v2/config_test
- 一般在配置根目录放一个env.xml，将各个服务可能都会用到的配置放在其中，如: ZK_HOSTS, DOMAIN ...
- 各个服务自己使用的变量配置一般在配置根目录下的biz/{SERVICE_NAME}/env.xml下面

### 3.2. <传统飞信环境> 加载HACenter中的配置表及相关数据源

在很多场景，我们还需要继续使用传统飞信环境的HACenter的配置项及配置表，在Helium框架框架中访问飞信传统HACenter配置，参考以下步骤，

- 1. 在环境变量中配置`HA_CENTER_RPC`的地址，`HA_CENTER_RPC`是个约定好的名字，只要配置了这个地址，helium-framework-old就会自动初始化HACenter配置读取相关的代码

```xml
    <variable key="HA_CENTER_RPC" value="tcp://192.168.143.144:8888"/>

```

- 2. 通过注入方式获取到HAConfigService接口，并完成配置读取

```
	@ServiceSetter
	private HAConfigService configService;
	
	private String configText;
	private ConfigTable<Integer, CFG_LogicalPool> configTable; 
	
	@Initializer
	public void initialize() throws Exception {
		configText = configService.loadConfigText("addressbook.properties", new ConfigParams());
		
		configTable = configService.loadConfigTable(Integer.class, CFG_LogicalPool.class, "CFG_LogicalPool");
	}
```

- 3. 已经内置的一些可以直接使用的，基于HA的数据源配置

飞信HA分Pool数据库

```	
	@FieldSetter("IICUPDB")             // 从FAE_Resource表读取
	private HAPooledDatabase updb;
```

飞信HA带Sharding功能的Redis

```	
	@FieldSetter("PRS-Online")         // 从FAE_RedisCluster表读取
	private HARedisCluster prsOnline;
```

飞信HA带Sharding功能的RedisSentinel

```
	@FieldSetter("SIMS-Contents")      // 从FAE_RedisSentinels表读取
	private HARedisSentinelCluster simsContents;
```

*本节代码参考`HAConfigSampleServlet`*

### 3.3. <传统飞信环境> 新老版本Rpc之间的兼容

传统飞信的老版本RPC与Helium框架中使用的透明Rpc不同，老版本Rpc的Server端使用以下方式进使用

1. 在$CONFIG/rpc路径下添加`fetion-cats.xml`

```
<rpcService service="IUserIndexService">
	<endpoints>
		<endpoint value="tcp://192.168.110.194:8802"/>
	</endpoints>
</rpcService>

```

2. 得到注入的LegacyRpcClient 

```
	@FieldSetter("rpc/fetion-cats.xml")
	private LegacyRpcClient catsRpcClient; 
```

*`@FieldSetter`路径上的rpc在2.3.1版本中是可以省略掉的, 2.1.9以前的版本是历史问题必须保留*

3. 调用Rpc得到结果, 传统Rpc需要自己判断调用与返回的类型

```
		RpcMethodStub methodStub = catsRpcClient.getMethodStub("GetUserIndexByMobileNo");
		FxMobileNo mobileNo = FxMobileNo.parse(ctx.getRequest().getParameter("mobile"));
		RpcFuture future = methodStub.invoke(mobileNo);
		FxCatalogIndex fxCatalogIndex = future.syncGet(FxCatalogIndex.class);
```

如果在传统服务中访问Helium服务，需要使用`RpcProxyFactory.getTransparency系列方法`，注意传统Rpc和透明Rpc在参数序列化方式上是不同的，互不兼容

### 3.4. 实现数据库的Partition&Sharding

Partition&Sharding就是数据库的分库分表，在大规模系统设计中会经常用到，helium-framework-old提供一个简单且比较容易扩展的分库分表解决方案。 

- 1. 首先配置`SAMPLEDB_sharding.xml`, 在$CONFIG/db目录下

```xml
<ShardedDataSource name="SAMPLEDB">
    <!-- 库号，从1开始: 注意不是从0开始 -->
    <dataSources>
        <dataSource id="1" name="SAMPLEDB.1"/>
        <dataSource id="2" name="SAMPLEDB.2"/>
    </dataSources>
    <!-- 用于Sharding与Partition的函数, ModDiv表示先按modBy取余再按divBy -->
    <shardingFunction class="org.helium.data.sharding.functions.ModDivFunction">
        <setters>
            <setter field="modBy">8</setter><!-- id % 8，得到表名后缀-->
            <setter field="divBy">4</setter><!-- (id % 8) / 4，得到库序号-->
            <setter field="shardingFormat">%d</setter><!-- 表后缀格式，如果希望得到补零后的格式，_00, _01，设置为"%02d"-->
        </setters>
    </shardingFunction>
</ShardedDataSource>
```

- 2. 在$CONFIG/db目录下，添加连接串，本例子下为`SAMPLEDB.1.properties`和`SAMPLEDB.2.properties`

- 3. 建库建表，表名的后缀应当与`XXX_sharding.xml`中的保持一致，注意，如果使用`ModDivFunction`表名从0开始，库名从1开始

```sql
create database SAMPLEDB_1;
use SAMPLEDB_1;

create table Users_0 (id int primary key, name varchar(100), role varchar(100));
create table Users_1 (id int primary key, name varchar(100), role varchar(100));
create table Users_2 (id int primary key, name varchar(100), role varchar(100));
create table Users_3 (id int primary key, name varchar(100), role varchar(100));

insert into Users_1 value (1, "Zhangsan", "admin");
insert into Users_2 value (2, "Lisi", "user");

create database SAMPLEDB_2;
use SAMPLEDB_2;

create table Users_4 (id int primary key, name varchar(100), role varchar(100));
create table Users_5 (id int primary key, name varchar(100), role varchar(100));
create table Users_6 (id int primary key, name varchar(100), role varchar(100));
create table Users_7 (id int primary key, name varchar(100), role varchar(100));

insert into Users_6 value (6, "6666666", "admin");
insert into Users_7 value (7, "SEVEN", "user");
```

- 4. 通过注入方式得到数据源，参考`ShardedSampleServiceImpl`这个类是`SampleService`的另一个实现

```
	@FieldSetter("SAMPLEDB_Sharding.xml")
	private ShardedDatabase<Long> shardedDb;
```

*因为ModDivFunction的传入参数为Long，所以这里要用Long，不能用Integer*


- 5. 编写访问代码，参考`ShardedSampleServiceImpl`这个类是`SampleService`的另一个实现

```
		// 首先根据userId得到sharding, 然后再按照原有方式进行访问
		Database sharding = shardedDb.getSharding((long)userId);
		// SQL涉及到的表名后面必须添加${SHARDING}后缀，这个是约定
		DataTable table = sharding.executeTable("select * from Users_${SHARDING} where id = ?", userId);
```

### 3.5. 实现Redis的Sharding

### 3.6. 使用基于MyBatis的MYSQL访问 [2.3.1]

### 3.7. 使用ScheduledTask定时任务

定时任务的实现方式如下，需要实现ScheduledTask接口，并添加`@ScheduleTaskImplementation`标注

```java
@ScheduledTaskImplementation(id = "quickstart:ScheduledTaskSample", cronExpression = "*/5 * * * * ?")
public class ScheduledTaskSample implements ScheduledTask {
	@Override
	public void processTask(Object lock) {
		System.err.println("ScheduledTask Running...");
	}
}
```

定时任务使用Cron表达式完成定时功能，Corn表达式的详细定义可自行Google，或参考下面文章

```
 Cron Expressions

 cron的表达式被用来配置CronTrigger实例。 cron的表达式是字符串，实际上是由七子表达式，描述个别细节的时间表。这些子表达式是分开的空白，代表：

		1.        Seconds
		2.        Minutes
		3.        Hours
		4.        Day-of-Month
		5.        Month
		6.        Day-of-Week
		7.        Year (可选字段)
		例  "0 0 12 ? * WED" 在每星期三下午12:00 执行,

		个别子表达式可以包含范围, 例如，在前面的例子里("WED")可以替换成 "MON-FRI", "MON, WED, FRI"甚至"MON-WED,SAT".

		“*” 代表整个时间段.

		每一个字段都有一套可以指定有效值，如

		Seconds (秒)         ：可以用数字0－59 表示，

		Minutes(分)          ：可以用数字0－59 表示，

		Hours(时)             ：可以用数字0-23表示,

		Day-of-Month(天) ：可以用数字1-31 中的任一一个值，但要注意一些特别的月份

		Month(月)            ：可以用0-11 或用字符串  “JAN, FEB, MAR, APR, MAY, JUN, JUL, AUG, SEP, OCT, NOV and DEC” 表示

		Day-of-Week(每周)：可以用数字1-7表示（1 ＝ 星期日）或用字符口串“SUN, MON, TUE, WED, THU, FRI and SAT”表示

		“/”：为特别单位，表示为“每”如“0/15”表示每隔15分钟执行一次,“0”表示为从“0”分开始, “3/20”表示表示每隔20分钟执行一次，“3”表示从第3分钟开始执行

		“?”：表示每月的某一天，或第周的某一天

		“L”：用于每月，或每周，表示为每月的最后一天，或每个月的最后星期几如“6L”表示“每月的最后一个星期五”

		“W”：表示为最近工作日，如“15W”放在每月（day-of-month）字段上表示为“到本月15日最近的工作日”

		““#”：是用来指定“的”每月第n个工作日,例 在每周（day-of-week）这个字段中内容为"6#3" or "FRI#3" 则表示“每月第三个星期五”



		1）Cron表达式的格式：秒 分 时 日 月 周 年(可选)。

		字段名                 允许的值                        允许的特殊字符
		秒                    0-59                               , - * /
		分                    0-59                               , - * /
		小时                  0-23                               , - * /
		日                    1-31                               , - * ? / L W C
		月                    1-12 or JAN-DEC         , - * /
		周几                  1-7 or SUN-SAT           , - * ? / L C #
		年 (可选字段)          empty, 1970-2099      , - * /



		“?”字符：表示不确定的值

		“,”字符：指定数个值

		“-”字符：指定一个值的范围

		“/”字符：指定一个值的增加幅度。n/m表示从n开始，每次增加m

		“L”字符：用在日表示一个月中的最后一天，用在周表示该月最后一个星期X

		“W”字符：指定离给定日期最近的工作日(周一到周五)

		“#”字符：表示该月第几个周X。6#3表示该月第3个周五

 2）Cron表达式范例：

		每隔5秒执行一次：*/5 * * * * ?

		每隔1分钟执行一次：0 */1 * * * ?

		每天23点执行一次：0 0 23 * * ?

		每天凌晨1点执行一次：0 0 1 * * ?

		每月1号凌晨1点执行一次：0 0 1 1 * ?

		每月最后一天23点执行一次：0 0 23 L * ?

		每周星期天凌晨1点实行一次 0 0 1 ? * L

		在26分、29分、33分执行一次：0 26,29,33 * * * ?

		每天的0点、13点、18点、21点都执行一次：0 0 0,13,18,21 * * ?
```

### 3.7. 为不同应用划分线程池

在很多场景，我们需要隔离线程池，为不同的Service，Task，Servlet分配不同的线程池，一般我们仅使用`type="fixed"`方式的线程池，size表示线程池的大小，limit指的是如果线程满，允许排队的数量。

```xml
<bootstrap...
    <!-- 特殊化线程池配置executors-->
    <executors>
        <executor type="fixed" name="sample-task" size="16" limit="20480"/>
        <executor type="fixed" name="servlet" size="8" limit="4096"/>
    </executors>
    ...
</bootstrap>
```

当在`<bootstrap/>`中添加了`<executors>`设置后，可以在`<beans>`节点中增加`executor={executorName}`的方式制定Servlet，对外的Service，Task使用的线程池，对内的Service是无法制定线程池的

```
   <beans>
        <bean class="org.helium.sample.advanced.ConfigSampleServlet" stacks="http" executor="servlet"/>
    	...
   </beans>
```

*参考`boostrap-advanced.xml`

### 3.8. 使用perfmon.xml定时记录业务性能数据

在$CONFIG的根目录防止`perfmon.xml`用于定时记录，`perfmon.xml`的格式如下，请先访问通过dashboard的perfmon界面，确认存在哪些计数器，再添加访问

```
<!-- 提供给helium-dashboard用于记录perfmon数据的配置 -->
<perfmon>
    <database>
        <jdbcUrl>jdbc:mysql://10.10.202.132:3306/PERFMONDB?autoReconnect=true</jdbcUrl>
        <user>admin</user>
        <password>Admin@!2016</password>
    </database>
    <!-- 需要记录的计数器，name请对照perfmon, interval为间隔的时间 -->
    <counters>
        <counter name="database" interval="60"/>
        <counter name="thread-pool" interval="60"/>
        <counter name="tasks" interval="60"/>
    </counters>
</perfmon>
```

当配置并启动成功后，会在perfmon.xml提供的库上创建名为`PERFMON_{name}_{YYYYMMDD}`的表，并自动记录相关的性能数据。

### 3.9. 使用带有强一致性队列的DedicatedTask

在消息递送，涉及到需要强一致性的用户状态变更时，可能会需要一种具有针对特定用户实现顺序排队功能的消费能力，DedicatedTask提供了这样一种机制，

- TaskArgs需要从DedicatedTaskArgs接口派生，实现getTag()方法
- 在processTask()方法中，每次执行完成后必须执行ctx.setTaskRunnable()才能继续消费针对此tag的下一个Args

参考代码如下`SampleDedicatedTask`

```
@TaskImplementation(event = "quickstart:SampleDedicatedTask")
public class SampleDedicatedTask implements DedicatedTask<SampleUser> {
	@Override
	public void processTask(DedicatedTaskContext ctx, SampleUser args) {
		// DO SOMETHING
	 	...	
		
		//
		// DedicatedTask完成运行后必须运行setTaskRunnable()方法设置为可用
		ctx.setTaskRunnable();
	}

	@Override
	public void processTaskRemoved(DedicatedTaskContext ctx) {
		// 当Tag被移除后执行此方法, 但一般没有实现
	}
}
```

### 3.10. 实现异步Service与RestfulService [2.3.1]

[TODO]

### 3.11. 使用其他数据源

其他可用数据源

- FastDFS: FastDFSClient: 
- Kafka: KafkaProducer, 
- HBase: HTableClient:  

### 3.13 ServletModule及ServiceModule [2.3.1]

## 4. Helium的可扩展开发接口[2.3.1]

### 4.1. 通过实现FieldLoader接口实现更多的注入器功能

### 4.2. 通过扩展BeanContextLoader实现更多的Bean类型

### 4.3. 扩展TaskEngine

### 4.4. 扩展Perfmon计数器



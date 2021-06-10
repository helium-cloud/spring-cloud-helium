### 1.helium-spi概要
* 重要功能
    * 1.SPI
    * 2.config
    
### TODO
* 

### 3.helium-spi依赖分析

代码依赖
``` gradle
description = 'helium-spi'
dependencies {
    //log4j
    compile "org.slf4j:slf4j-api:${slf4j_version}"

    //common-io通用依赖库
    compile group: 'commons-io', name: 'commons-io', version: '2.8.0'
    compile "org.javassist:javassist:${javassist_version}"



    //高效的二进制对象图形序列化框架[序列化]
    //TODO 移除一部分
    testCompile "com.esotericsoftware:kryo:${kryo_version}"
    testCompile 'com.alibaba:hessian-lite:3.2.3'

    testCompile 'de.javakaffee:kryo-serializers:0.42'
    testCompile 'de.ruedigermoeller:fst:2.48-jdk-6'

    //测试库
    testCompile group: 'org.hamcrest', name: 'hamcrest', version: '2.2'
    testCompile group: 'org.slf4j', name: 'slf4j-simple', version: '1.7.25'
    //动态代理
    testCompile "org.javassist:javassist:${javassist_version}"
    testCompile "cglib:cglib:${cglib_version}"
    testCompile 'com.alibaba:fastjson:1.2.46'

}
```
### 4.包目录及资源文件
```      
beanutil: java-bean操作工具类:JavaBeanSerializeUtil
bytecode: ClassGenerator和Proxy byte操作
config: 配置模块,系统,环境变量等操作
compiler: support:JavassistCompiler,JdkCompiler等
extension
     factory: spi工厂，AdaptiveExtensionFactory，SpiExtensionFactory
     support: 
io: io操作
json: 以基本不使用
logger: 日志操作模块
     jcl
     jdk
     log4j2
     log4j
     slf4j
     support
status: 状态监测模块
     support: LoadStatusChecker，MemoryStatusChecker
store
     support: ConcurrentMap存储
threadlocal: NamedInternalThreadFactory工厂
    
timer
threadpool: 线程池组件类
     manager: DefaultExecutorRepository
     support
          cached: CachedThreadPool
          eager: TaskQueue，EagerThreadPoolExecutor
          fixed: FixedThreadPool
          limited: LimitedThreadPool
utils:各种各样的工具类,StringUtils,JVMUtil
    
```


### 5.组件使用
#### 5.1.SPI
* 功能项
改进了JDK标准的SPI的以下问题:JDK标准的SPI会一次性实例化扩展点所有实现，如果有扩展实现初始化很耗时，没用上也会加载，会很浪费资源。 
如果扩展点加载失败，扩展点的名称无法拿到。

* 约定： 
在扩展类的jar包内，放置扩展点配置文件：META-INF/helium/接口全限定名，内容为：配置名=扩展实现类全限定名，多个实现类用换行符分隔。

* 配置位置
helium-spi/src/test/resources/META-INF/helium/internal/Demo
内容
```
demo=DemoImpl
wrapper=DemoWrapper
```

* 测试
```
Demo demoWrapper = ExtensionLoader.getExtensionLoader(Demo.class).getExtension("demo");
System.out.println(demoWrapper);
assertTrue(demoWrapper instanceof DemoWrapper);
Demo demoWrapper2 = ExtensionLoader.getExtensionLoader(Demo.class).getExtension("demo2");
System.out.println(demoWrapper2);
assertTrue(demoWrapper2 instanceof DemoWrapper2);
```


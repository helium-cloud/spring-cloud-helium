# helium-logging日志处理模块

## 设计目标

### 基本目标

- 它是slf4j-api的一个标准实现，只要加载了就会生效
- 启动时会寻找启动目录的logging.xml，如果找到就按照logging.xml的方式加载
- 在未加载logging.xml的时候，启动默认配置，level=info，仅启动ConsoleAppender
- 支持以下appender: ConsoleAppender, TextAppender, DatabaseAppender

### 扩展目标

因为helium-logging针对helium容器设计，所以支持以下特性

- 在helium容器中注册helium:LoggerManager的对象，LoggerManager支持以下功能，以更好支持Dashboard的使用
	* 动态调整级别, 或重新加载配置
	* 在Servlet或Task实现中，允许通过ModuleContext的过滤功能[M9,Marker实现]
	* 允许单独设置某个Bean的Logger等级[M9,]
	* 允许单独过滤设置的单独输出[M9]
-


## 配置文件

```xml
<logging level="INFO">
    <appenders>
        <appender class="org.helium.logging.spi.appenders.ConsoleAppender"/>
        <appender class="org.helium.logging.spi.appenders.TextAppender">
            <setters>
                <setter field="path"></setter>
                <setter field="file">LOG_yyMMdd_HH.log</setter>
                <setter field="days"></setter>
                <setter field="size"></setter>
            </setters>
        </appender>
    </appenders>
    <loggers>
        <logger name="" level="WARN"/>
    </loggers>
</logging>
```

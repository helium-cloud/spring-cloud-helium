## 1.提供服务监控能力
* 提供可视化QPS监控能力
* TODO
    * 数据自动清理
    * 数据写入数据库采用spi扩展
## 2.使用
### 2.1编写计数器
```
@PerformanceCounterCategory("monitor")
public class PerfmonCounters {
	@PerformanceCounter(name = "tx", type = PerformanceCounterType.TRANSACTION)
	private SmartCounter tx;

	@PerformanceCounter(name = "qps", type = PerformanceCounterType.QPS)
	private SmartCounter qps;

	public SmartCounter getQps() {
		return qps;
	}

	public SmartCounter getTx() {
		return tx;
	}

	public static PerfmonCounters getInstance(String name) {
		PerfmonCounters perfmonCounters =
				PerformanceCounterFactory.getCounters(PerfmonCounters.class, name);
		return perfmonCounters;
	}

}

```
### 2.2 计数

```

PerfmonCounters perfmonCounters = PerfmonCounters.getInstance("test");
Stopwatch stopwatch = perfmonCounters.getTx().begin();
stopwatch.fail("xxx");
stopwatch.end();

```

### 2.3  服务启动
```
public class ServerTest {
	public static void main(String[] args) {
		MonitorServer.run(8081);
	}
}
```

### 2.4  访问服务器

```
http://127.0.0.1:8081/

```

### 2.5  查看历史记录

```
1.

https://github.com/dbeaver/dbeaver
db客户端连接数据库 可查看历史记录

2.连接串

jdbc:h2:tcp://localhost:9093/~/h2db

user: sa
password: 123456
```

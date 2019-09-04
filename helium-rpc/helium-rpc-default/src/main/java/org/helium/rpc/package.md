org.helium.rpc - 实现跨平台多协议的rpc框架的类库
=================================================================

包列表
-----------------------------------------------------------------
org.helium.rpc.common			公共定义类
org.helium.rpc.channel			负责rpc底层通信，包含对上与对下的代码
org.helium.rpc.client			包含客户端代码
org.helium.rpc.server			包含服务器端代码

org.helium.rpc.channel.uds		UnixDomainSocket通信层
org.helium.rpc.channel.tcp		Tcp通信层
org.helium.rpc.channel.http		Http通信层
org.helium.rpc.channel.inproc		远程调用转换为内部调用的通信层

1.5.0 版本改动
-----------------------------------------------------------------
* 调整包间的依赖结构，避免循环依赖
* 将以下机制调整到org.helium.rpc.channel包中
	+ fromId, toId的协商与连接复用机制
	+ RpcConnection代表一个物理连接，将SimplexConnection连接的循环与回收机制内置在代码中
	+ RpcTransactionManager
	+ 在Transaction层面使用Future模式代替原有的回调方式
	+ 异步回调时判断是否启用新线程处理,或是采用老线程处理
	+ 使用默认模式的RpcRequest, RpcResponse, 在此层面上不再继续抽象
* 简化透明代理的方法参数设置，不再对透明代理进行异步调用实现的兼容, 删除RpcNull类
* 增加包org.helium.rpc.server, 并做如下调整
	+ 增加服务器端线程池的设置
	+ 增加Rpc内建服务
		- ping方法
		- reflect方法
* 增加包org.helium.rpc.client, 并作如下调整
	+ 新增RpcMethodStub代理类, 允许fromId, toId的复用
	+ 更换使用RpcFuture模式
	+ 增加软负载机制
	+ 废弃RpcProxy, 但提供原始代码的向下兼容
	+ 增加大量事务并发时的保护机制
	

复杂性体现在
-----------------------------------------------------------------
1. 连接状态的特性->tcp连接上的复杂性, cacheable, endpoint本身很复杂
2. duplex
3. 同时支持各种协议(inproc, tcp, http)
4. 性能测试统计及日志
5. 能够负担的起稳定业务的一个完整的应用服务器
6. 

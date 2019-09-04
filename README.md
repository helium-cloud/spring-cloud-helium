# Helium² Framework

## 设计目标

**Helium²是一个基于依赖注入思想实现的Java微服务框架，Helium²致力于解决以下问题**

- 快速开发, 迅速搭建一个可用的Java平台程序
- 支持接口/实现分离的Service与透明Rpc支持，支持rpc路由动态分配
- 支持HttpServlet、RestfulService、WebService以便于实现基于Http的应用
- 内置mysql、redis、fastDFS、kafka、hbase的数据访问组的Setter注入式引用，并提供扩展能力
- 数据库访问可集成MyBatis，以及透明的Partition&Sharding能力
- 灵活的配置能力，以及约定大于配置的定制化能力
- 支持异步处理业务的Task模式，松散耦合的Task模式，以及特定Tag进行有序消费的DedicatedTask
- 支持基于Zookeeper的服务发现与服务负载服务
- 具备友好的的Dashboard，能监控业务启动情况与服务性能
- 内置perfmon记录, 无需考虑业务计数器问题
- 兼容spring-boot、spring-cloud组件
- 支持docker部署

## 参考手册

- [快速入门 - QUICKSTART.md](QUICKSTART.md)
- [开发与运维人员参考 - README-DEVOPS.md](README-DEVOPS.md)

## 当前维护分支与开发情况

- 当前稳定版本为: `2.1.9-SNAPSHOT`
    原有helium版本
- 当前测试版本为: `3.0.1-SNAPSHOT`
    增加docker部署支持，spring、task优化等
- 当前开发版本为: `3.0.2-BETA`
    待开发动态配置、zk组件部分优化、rpc解耦

参考gradle引用

```
dependencies {
    compile group: 'org.helium', name: 'helium-http', version: helium_version
    compile group: 'org.helium', name: 'helium-data-base', version: helium_version
    testCompile group: 'junit', name: 'junit', version: junit_version
}
```

```
dependencies {
    compile group: 'org.helium', name: 'helium-boot', version: helium_version
    compile group: 'org.helium', name: 'helium-data-base', version: helium_version
    testCompile group: 'junit', name: 'junit', version: junit_version
}
```

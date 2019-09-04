# RedisSentinel

## 配置文件示例
```
testOnBorrow=false
testOnReturn=false
testWhileIdle=true
maxTotal=50
maxIdle=50
minIdle=50
maxWait=-1
whenExhaustedAction=1
timeBetweenEvictionRunsMillis=30000
numTestsPerEvictionRun=-1
minEvictableIdleTimeMillis=-1
softMinEvictableIdleTimeMillis=540000

#server distribution

# 这个配置决定了走sentinel 的方式. 地址中的信息为sentinel 的链接地址.如果为sentinel,目前无法配置多个server 节点. 建议使用数据库方式.
sentinelModel=true
# sentinel 通过masterName获取实际地址.如果配置了masterNamePrefix,并且没有配置server.n.masterName 则推算为 masterName 为 masterNamePrefix[n] ,比如 Test.0, 若masterNamePrefix 为空, 也没有配置server.n.masterName 则抛出异常中断这个加载过程.
masterNamePrefix=Test
#默认的数据库为0
database=0
serverNumber=1
# 这个实际是Sentinel 的地址, 多个用分号分割.
server.0=192.168.247.228:26380;172.21.35.196:26380;172.21.35.197:26380


```

## 包结构



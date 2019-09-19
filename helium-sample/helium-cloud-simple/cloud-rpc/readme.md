#### 1.dubbo使用说明
    参考：
    https://github.com/apache/incubator-dubbo-spring-boot-project/blob/master/README_CN.md
#### 2.使用示例

示例见[dubbo使用示例](https://github.com/coral-learning/spring-boot-example/tree/master/spring-dubbo)

#### 3.示例说明
##### 3.1.依赖
        注意引入jar时注意jar冲突，造成服务启动失败，采用 gradle dependencies查看冲突依赖jar
        //连接zk时使用
        compile (group: 'org.apache.zookeeper', name: 'zookeeper', version: '3.5.0-alpha'){
            exclude group: 'org.slf4j', module: 'slf4j-log4j12'
            exclude group: 'log4j', module: 'log4j'
        }
        compile group: 'com.101tec', name: 'zkclient', version: '0.10'
        //dubbo启动
        compile group: 'com.alibaba.spring.boot', name: 'dubbo-spring-boot-starter', version: '2.0.0'
        
        
##### 3.2.定义api接口
        public interface MessageService {
            MessageResponse send(MessageRequest messageRequest);
        }

    
##### 3.3.实现服务提供者
    
        业务实现
            @Service(interfaceClass = MessageService.class)
            @Component
            public class MessageServiceDefault implements MessageService {
            
                @Override
                public MessageResponse send(MessageRequest messageRequest) {
                    MessageResponse response = new MessageResponse();
                    System.out.println("sendMessage" + messageRequest.getTid());
                    return response;
                }
            }
        
        启动配置
            # Spring boot application
            server.port: 8231
            spring.application.name=spring-dubbo-provider
            spring.dubbo.server=true
            #不使用注册中心
            #spring.dubbo.registry=N/A
            
            #使用zk作为注册中心
            spring.dubbo.registry.address=zookeeper://127.0.0.1:7998
##### 3.4.实现服务调用者
    
        业务实现：
            //定向路由
            //@Reference(url = "dubbo://127.0.0.1:20880")
            //private MessageService messageService;
        
            //注册中心负载
            @Reference
            private MessageService messageService;
        
            @GetMapping("/dubbo/test")
            @ResponseBody
            public MessageResponse doTest(){
                MessageRequest messageRequest = new MessageRequest();
                messageRequest.setTid(UUID.randomUUID().toString());
                System.out.println("call send");
                MessageResponse messageResponse = messageService.send(messageRequest);
                return messageResponse;
            }
            
        启动配置
           # Spring boot application
           server.port: 8232
           spring.application.name=spring-dubbo-consumer
           spring.dubbo.registry.address=zookeeper://127.0.0.1:7998
           #spring.dubbo.registry=N/A
##### 3.5.测试
        访问：
            http://127.0.0.1:8232/dubbo/test
        
        控制台输出：
            sendMessage Rev Tid:7fce344d-d11c-474d-8604-5ed77b7c823a
    

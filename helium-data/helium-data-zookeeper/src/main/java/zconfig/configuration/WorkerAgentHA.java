package zconfig.configuration;

import com.feinno.superpojo.type.DateTime;
import org.helium.framework.configuration.Environments;
import org.helium.rpc.RpcEndpointFactory;
import org.helium.rpc.channel.RpcEndpoint;
import org.helium.rpc.duplex.RpcDuplexClient;
import org.helium.threading.ExecutorFactory;
import org.helium.util.ServiceEnviornment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zconfig.configuration.args.HAWorkerHeartbeatArgs;
import zconfig.configuration.args.HAWorkerRegisterArgs;

/**
 * Created by liufeng on 2016/1/5.
 */
public class WorkerAgentHA {
    private static Logger LOGGER = LoggerFactory.getLogger(WorkerAgentHA.class);

    private HAWorkerAgentService workerAgentService;

    /** Worker运行标志 */
    protected boolean running;

    /** 最后一次心跳时间 */
    protected DateTime lastHeartbeat;

    protected Thread heartbeatThread;

    /** 心跳延迟的最小时间，最终的心跳时间是与服务端监控配置进行协商后的结果 */
    private static final int HEARTBEAT_MIN_DELAY = 60 * 1000;// TODO 60 * 1000

    /** 心跳延迟的最终生效时间，该时间是于服务器端监控配置协商后的结果，取所有时间的最小值 */
    private static int HEARTBEAT_EFFECTIVE_DELAY = Integer.MAX_VALUE;

    /** 客户端连接 */
    private RpcDuplexClient client;

    private static WorkerAgentHA instance;
    private static Object syncObject = new Object();

    private static void initialize(){
        if (instance == null) {
            synchronized (syncObject) {
                if (instance == null) {
                    instance = new WorkerAgentHA();
                }
            }
        }
    }

    public static void init() {
        if (instance == null) {
            initialize();
        }
    }

    /**
     * 初始化
     */
    public WorkerAgentHA() {
        String centerUrl = Environments.getVar(HAConfigurator.CENTER_URL_KEY);
        RpcEndpoint centerEp = RpcEndpointFactory.parse(centerUrl);
        client = new RpcDuplexClient(centerEp);
        client.setExecutor(ExecutorFactory.newFixedExecutor("Client", 10, 10240));
        workerAgentService = client.getService(HAWorkerAgentService.class);
        HAConfigurator configurator = new HAConfigurator(client);
        ConfigurationManager.getInstance().setConfigurator(configurator);

        //连接HACenter服务并注册
        register();

        //启动心动线程
        startHeartbeatThread();
    }

    private void register() {
        try {
            client.connectSync();
            HAWorkerRegisterArgs args = new HAWorkerRegisterArgs();
            args.setServerName(ServiceEnviornment.getComputerName());
            args.setServiceName(ServiceEnviornment.getServiceName());
            args.setWorkerPid(ServiceEnviornment.getPid());
            args.setServicePorts("");
            workerAgentService.connect(args);
            workerAgentService.register(args);
        } catch (Exception ex) {
        }
    }

    private void startHeartbeatThread()
    {
        LOGGER.info("Start Heartbeat Thread.");
        // 5. 如果不存在心跳线程，则创建Worker心跳线程，向远端的服务定时发送心跳包
        if (heartbeatThread == null) {
            heartbeatThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    heartbeatProc();
                }
            });
            // 启动心跳线程
            running = true;
            heartbeatThread.start();
        } else if (heartbeatThread != null && running == false) {
            // 如果存在心跳线程，但是线程不在运行，则重新start
            running = true;
            heartbeatThread.start();
        }
    }

    private void close() {
        try {
            if (client != null) {
                client.close();
            }
        } catch (Exception e) {
            LOGGER.error(String.format("Close %s error"), e);
        }
    }

    public void heartbeat(Status status) {
        try {
            lastHeartbeat = DateTime.now();
            HAWorkerHeartbeatArgs args = new HAWorkerHeartbeatArgs();
            args.setStatus(status.toString());
            args.setStatusEx("");
            workerAgentService.heartbeat(args);
        } catch (Exception e) {
            LOGGER.error("Send heartbeat failed. ", e);
        }
    }

    protected void heartbeatProc() {
        HEARTBEAT_EFFECTIVE_DELAY = HEARTBEAT_MIN_DELAY;

        while (running) {
            try {
                // 1. 如果连接断了，需要进行失败重连
                if (!client.isConnected()) {
                    LOGGER.warn("HA-Center Connection is broken,begin Reconnection.");
                    // init();
                    close();
                    register();
                    LOGGER.warn("HA-Center Reconnection is done.");
                }
                // 2. 发送心跳数据
                heartbeat(Status.STARTED);
                Thread.sleep(HEARTBEAT_EFFECTIVE_DELAY);
            } catch (Exception ex) {
                int waitTime = HEARTBEAT_EFFECTIVE_DELAY >> 2;
                LOGGER.error(String.format("WorkerAgentHA.heartbeatProc failed. Sleep %s ms. Reasons: ", waitTime), ex);
                // 如果心跳发送失败，那么尝试休眠心跳频率的1/4时间再进行重新发送，
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException e) {
                    LOGGER.error(String.format(
                                    " WorkerAgentHA.heartbeatProc failed,try sleep %s s ,but interrupted Reasons: ", waitTime),
                            e);
                }
            }
        }
    }

    private static enum Status {
        STARTED, STOPPING, STOPPED,
    }
}
package org.helium.http.obsoleted;//package org.helium.http.servlet.extension;
//
//import org.helium.util.StringUtils;
//import org.glassfish.grizzly.http.server.HttpHandlerRegistration;
//import org.glassfish.grizzly.http.server.HttpServer;
//import org.glassfish.grizzly.http.server.NetworkListener;
//import org.glassfish.grizzly.http.server.ServerConfiguration;
//import org.helium.framework.BeanContext;
//import org.helium.framework.route.ServerUrl;
//import org.helium.framework.servlet.ServletDescriptor;
//import org.helium.framework.servlet.ServletMappings;
//import org.helium.framework.servlet.ServletStack;
//import org.helium.http.servlet.extension.spi.CommonHttpHandler;
//import org.helium.http.servlet.extension.spi.RootHttpHandler;
//import org.helium.http.servlet.extension.spi.ServletHandler;
//import org.helium.http.servlet.HttpModule;
//import org.helium.http.servlet.HttpServletDescriptor;
//import org.helium.http.servlet.HttpServletMappings;
//import org.helium.http.servlet.spi.HttpsKeyStore;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.servlet.http.HttpServlet;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//
///**
// * Auther: jingmiao, 2014; gaolei, 2015-05-13
// * <p>
// * TODO:
// * - SSL support
// * - perform
// * - single stack with multi listener
// * - logging
// */
//public class HttpServletStack implements ServletStack {
//    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServletStack.class);
//    private static final String PROTOCOL = "http";
//    private static final String NAME = "helium-http-server";
//    private static final String DEFAULT_SSL_KEY_STORE = "keystore.jks";
//    private static final String DEFAULT_SSL_KEY_STORE_PASS = "123456";
//    private static final String DEFAULT_SSL_KEY_PASS = "123456";
//
//    private String host;
//    private Integer port = 0;
//    private Integer sslPort = 0;
//    private String sslProtocol = "TLSv1.2";
//    private String keyStoreFilePath;
//    private String keyStorePassword;
//    private String keyPassword;
//    private String keyStoreType = HttpsKeyStore.KEKSTORE_JCEKS;
//
//    public String getHost() {
//        return host;
//    }
//
//    public HttpServletStack setHost(String host) {
//        this.host = host;
//        return this;
//    }
//
//    public Integer getPort() {
//        return port;
//    }
//
//    public HttpServletStack setPort(Integer port) {
//        this.port = port;
//        return this;
//    }
//
//    public Integer getSslPort() {
//        return sslPort;
//    }
//
//    public HttpServletStack setSslPort(Integer sslPort) {
//        this.sslPort = sslPort;
//        return this;
//    }
//
//    public String getSslProtocol() {
//        return sslProtocol;
//    }
//
//    public HttpServletStack setSslProtocol(String sslProtocol) {
//        this.sslProtocol = sslProtocol;
//        return this;
//    }
//
//    public String getKeyStoreFilePath() {
//        return keyStoreFilePath;
//    }
//
//    public HttpServletStack setKeyStoreFilePath(String keyStoreFilePath) {
//        this.keyStoreFilePath = keyStoreFilePath;
//        return this;
//    }
//
//    public String getKeyStorePassword() {
//        return keyStorePassword;
//    }
//
//    public HttpServletStack setKeyStorePassword(String keyStorePassword) {
//        this.keyStorePassword = keyStorePassword;
//        return this;
//    }
//
//    public String getKeyPassword() {
//        return keyPassword;
//    }
//
//    public HttpServletStack setKeyPassword(String keyPassword) {
//        this.keyPassword = keyPassword;
//        return this;
//    }
//
//    public String getKeyStoreType() {
//        return keyStoreType;
//    }
//
//    public HttpServletStack setKeyStoreType(String keyStoreType) {
//        this.keyStoreType = keyStoreType;
//        return this;
//    }
//
//    private HttpServer server;
//    private ServerConfiguration configuration;
//    //    private NetworkListener listener;
//    private Map<String, CommonHttpHandler> handlers;
//
//    public HttpServletStack() {
//        server = new HttpServer();
//        configuration = server.getServerConfiguration();
//        handlers = new HashMap<>();
//        configuration.addHttpHandler(new RootHttpHandler(), HttpHandlerRegistration.ROOT);
//    }
//
//    @Override
//    public boolean isSupportModule(Object module) {
//        return (module instanceof HttpModule);
//    }
//
//    @Override
//    public List<ServerUrl> getServerUrls() {
//        List<ServerUrl> eps = new ArrayList<>();
//        if (port != null) {
//            eps.add(ServerUrl.parse("http://" + host + ":" + port + ";protocol=http"));
//        }
//        if (sslPort != null) {
//            eps.add(ServerUrl.parse("https://" + host + ":" + sslPort + ";protocol=http"));
//        }
//        return eps;
//    }
//
//    @Override
//    public boolean isSupportServlet(Object servlet) {
//        return (servlet instanceof HttpServlet);
//    }
//
//    @Override
//    public ServletDescriptor getServletDescriptor() {
//        return HttpServletDescriptor.INSTANCE;
//    }
//
//    @Override
//    public void registerModule(BeanContext context) {
//        if (!(context.getBean() instanceof HttpModule)) {
//            throw new IllegalArgumentException("unsupported ModuleImplementation:" + context.getId());
//        }
//        HttpModule module = (HttpModule) context.getBean();
//        String contextPath = module.getContextPath();
//        CommonHttpHandler handler = getContextPathHandler(contextPath);
//        synchronized (this) {
//            handler.addModule(module);
//        }
//        LOGGER.info("registerModule context={} module={}", handler.getContextPath(), module.getClass().getName());
//    }
//
//    @Override
//    public void registerServlet(BeanContext ctx) {
//        LOGGER.info("register HttpServlet: {}", ctx.getId());
//        if (!(ctx.getBean() instanceof HttpServlet)) {
//            throw new IllegalArgumentException("Unexpected bean type:" + ctx.getBean().toString());
//        }
//        HttpServletMappings mappings = (HttpServletMappings) ctx.getAttachment(ServletMappings.class);
//        CommonHttpHandler cph = getContextPathHandler(mappings.getContextPath());
//
//        ServletHandler handler = new ServletHandler(ctx, cph);
//
//        //
//        // 如果Servlet支持的是/*, 则跟cph的处理范围重复, 此时无法再进入到servletHandler
//        if ("/*".equals(mappings.getUrlPattern())) {
//            cph.setServlet(handler);
//        } else {
//            configuration.addHttpHandler(handler, mappings.getHandlerRegistration());
//        }
//
//        LOGGER.info("registerServlet servlet={} mapping={}", ctx.getBean().getClass(), mappings);
//    }
//
//    @Override
//    public void unregisterModule(BeanContext context) {
//
//    }
//
//    @Override
//    public void unregisterServlet(BeanContext context) {
//
//    }
//
//    @Override
//    public void start() throws Exception {
//        if (StringUtils.isNullOrEmpty(host)) {
//            host = NetworkListener.DEFAULT_NETWORK_HOST;
//        }
//
//        LOGGER.info("Start {} service", this.getClass().getSimpleName());
//        server.start();
//
//        if (port != null) {
//            NetworkListener listener = new NetworkListener(NAME, host, port);
//            LOGGER.info("Add http Listener {}:{}", host, port);
//            server.addListener(listener);
//            listener.start();
//        }
//
//        if (sslPort != null) {
//
//        }
//    }
//
//    @Override
//    public void stop() throws Exception {
//        server.shutdownNow();
//    }
//
//    private CommonHttpHandler getContextPathHandler(String contextPath) {
//        synchronized (this) {
//            CommonHttpHandler handler = handlers.get(contextPath);
//            if (handler == null) {
//                handler = new CommonHttpHandler(contextPath);
//                handlers.put(contextPath, handler);
//                HttpServletMappings mappings = new HttpServletMappings(contextPath, "/*");
//                configuration.addHttpHandler(handler, mappings.getHandlerRegistration());
//            }
//            return handler;
//        }
//    }
//}

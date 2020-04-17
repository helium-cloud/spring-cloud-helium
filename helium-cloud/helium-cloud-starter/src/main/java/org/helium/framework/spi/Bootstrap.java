package org.helium.framework.spi;


import org.helium.framework.BeanContext;
import org.helium.framework.bundle.AppBundleHandler;
import org.helium.framework.bundle.BundleManager;
import org.helium.framework.bundle.CentralizedAppBundleHandler;
import org.helium.framework.configuration.BeanContextProvider;
import org.helium.framework.configuration.EnvironmentLoader;
import org.helium.framework.configuration.Environments;
import org.helium.framework.entitys.*;
import org.helium.framework.route.ServerEndpoint;
import org.helium.framework.route.ServerRouter;
import org.helium.framework.route.ServerUrl;
import org.helium.framework.route.center.CentralizedService;
import org.helium.framework.servlet.StackManagerImpl;
import org.helium.framework.spi.bundle.*;

import org.helium.framework.utils.ConfigUtils;
import org.helium.framework.utils.EnvUtils;
import org.helium.rpc.client.RpcProtocolResolver;
import org.helium.rpc.client.RpcProxyFactory;
import org.helium.threading.ExecutorFactory;
import org.helium.util.ErrorList;
import org.helium.util.ErrorListException;
import org.helium.util.StringUtils;
import org.helium.util.TypeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * 启动器
 * Created by Coral on 5/5/15.
 */
public class Bootstrap extends AbstractBeanContextService {
	private static final Logger LOGGER = LoggerFactory.getLogger(Bootstrap.class);
	private static final Marker MARKER = MarkerFactory.getMarker("HELIUM");

	public static final Bootstrap INSTANCE = new Bootstrap();
	// inner services
	private String serverId;
	private ServerEndpoint serverEndpoint;
	private BeanContextProvider contextProvider;
	private ConfigProviderImpl configProvider;
	private CentralizedService centerService;
	private StackManagerImpl stackManager;
	private BundleManager bundleManager;

	//是否加载完成
	private volatile boolean loaded = false;
	private String loadXml = "";

	// configuration
	private BootstrapConfiguration configuration;

	private Bootstrap() {
		BeanContext.initializeService(this);

		configProvider = loadBean(ConfigProviderImpl.class);
		LOGGER.info(MARKER, "ROOT_PATH={}", configProvider.getRoot());

		contextProvider = DefaultAppBundle.createContextProvider(configProvider);
		stackManager = loadBean(StackManagerImpl.class);
	}

	public ServerUrl getServerUrl(String protocol) {
		return serverEndpoint.getServerUrl(protocol);
	}

	public String getServerId() {
		return serverId;
	}

	public String getServiceName() {
		return serverId;
	}

	public boolean isLoaded() {
		return loaded;
	}

	public ServerEndpoint getServerEndpoint() {
		return serverEndpoint;
	}


	/**
	 * 增加配置路径
	 *
	 * @param path
	 */
	public void addPath(String path) {
		configProvider.addPath(path);
	}

	/**
	 * 初始化
	 *
	 * @param xmlPath
	 * @throws Exception
	 */
	public void initialize(String xmlPath) throws Exception {
		initialize(xmlPath, false, true);
	}

	/**
	 * 初始化环境变量
	 *
	 * @param envNode
	 */
	private void initialize(EnvironmentsNode envNode) {

		// Step 1. load root imports xml
		String imports = envNode.getImports();
		if (!StringUtils.isNullOrEmpty(imports)) {
			for (String i : StringUtils.split(imports, ",")) {
				EnvironmentsNode en = configProvider.loadXml(i, EnvironmentsNode.class);
				initialize(en);
			}
		}

		// Step 2. load variables
		Environments.loadVariables(envNode.getVariables());

		// Step 3. load root loader node
		String loader = envNode.getLoader();
		if (!StringUtils.isNullOrEmpty(loader)) {
			EnvironmentLoader envLoader = (EnvironmentLoader) ObjectCreator.createObject(loader, ObjectCreator.DEFAULT_LOADER);
			Environments.loadVariables(envLoader.loadEnv(envNode));
		}

		// Step 4. load node import xml
		List<KeyValueNode> nodes = envNode.getVariables();
		if (nodes != null && nodes.size() > 0) {
			for (KeyValueNode node : nodes) {
				if (!StringUtils.isNullOrEmpty(node.getImports())) {
					EnvironmentsNode en = configProvider.loadXml(node.getImports(), EnvironmentsNode.class);
					initialize(en);
				}
			}
		}
	}

	/**
	 * 初始化
	 *
	 * @param
	 * @throws Exception
	 */
	public void initialize(String xmlPath, boolean exitOnError, boolean runConsole) throws Exception {
		//
		// - 加载配置
		loadXml = xmlPath;
		BootstrapConfiguration bootstrapConfiguration = configProvider.loadXml(xmlPath, BootstrapConfiguration.class);
		initialize(bootstrapConfiguration, exitOnError, runConsole, true);
	}
	/**
	 * 初始化
	 *
	 * @param bootstrapConfiguration
	 * @throws Exception
	 */
	public void initialize(BootstrapConfiguration bootstrapConfiguration, boolean exitOnError, boolean runConsole, boolean reloadXml) throws Exception {
		//
		// - 开始启动Bootstrap
		int pid = Environments.getPid();
		LOGGER.warn(MARKER, ">>> Bootstrap initialize on server <{}> pid={}", ConfigUtils.getHostName(), pid);

		writePidFile(configProvider.getPidFile(), pid);

		configuration = bootstrapConfiguration;

		//
		// 加载系统变量
		Environments.loadSystemVariables();

		//
		// 加载环境变量
		if (configuration.getEnvironmentsNode() != null) {
			initialize(configuration.getEnvironmentsNode());
		}

		//
		// 初始化线程池
		for (ExecutorNode en : configuration.getExecutors()) {
			LOGGER.info(MARKER, ">>> create {}Executor name={} size={} limit={}",
					en.getType(), en.getName(), en.getSize(), en.getLimit());
			switch (en.getType().toLowerCase()) {
				case "fixed":
					ExecutorFactory.newFixedExecutor(en.getName(), en.getSize(), en.getLimit());
					break;
				case "cached":
					ExecutorFactory.newCachedExecutor(en.getName());
					break;
				case "scalable":
					if (en.getSize() == 0) {
						ExecutorFactory.newScalableExecutor(en.getName());
					} else {
						if (en.getMaxSize() == 0 || en.getLimit() == 0) {
							throw new IllegalArgumentException("need maxSize and limit attr" + en.getMaxSize());
						}
						ExecutorFactory.newScalableExecutor(en.getName(), en.getSize(), en.getMaxSize(), en.getLimit());
					}
					break;
				default:
					throw new IllegalArgumentException("ExecutorType supports fixed/cached/scalable unexpected:" + en.getType());
			}
		}

//		//
		//
		if (reloadXml){
			configuration = configProvider.loadXml(loadXml, BootstrapConfiguration.class);
			LOGGER.info(MARKER, ">>> Configuration reloaded.");
		}


		//
		// 创建BundleManager
		bundleManager = loadBean(AppBundleManagerImpl.class);
		LOGGER.info(MARKER, ">>> load BundleManager {}.", bundleManager.getClass());

		//
		// - 使用默认的ServerId, 如果存在<bootstrap id="xxx"/>，以此为ServerId
		serverId = ConfigUtils.getHostName();
		if (!StringUtils.isNullOrEmpty(configuration.getId())) {
			serverId = configuration.getId();
		}

		//
		// - 启动Stacks:
		for (ObjectWithSettersNode node : configuration.getStacks()) {
			if (TypeUtils.isFalse(node.getIsEnabled())) {
				continue;
			}
			LOGGER.info(">>> loading stack: {}", node.getClass());
			stackManager.loadStack(node, ObjectCreator.DEFAULT_LOADER);
		}

		//
		// - register CentralizedService
		if (configuration.getCentralizedService() != null) {
			LOGGER.info(">>> loading CentralizedService: {}", configuration.getCentralizedService().getClass());
			centerService = (CentralizedService) ObjectCreator.createObject(
					configuration.getCentralizedService(),
					this, ObjectCreator.DEFAULT_LOADER);
			centerService.register(serverId, stackManager.getCenterServerUrls(configuration), this);
			LOGGER.info(">>> CentralizedService registered.");
		}

		//
		// 增加使用helium://bean-id方式调用
//		RpcProtocolResolver resolver = new HeliumRpcProtocolResolver();
//		RpcProxyFactory.addProtocolResolver(resolver.getProtocol(), resolver);


		//
		// - 加载<references/>静态节点中的数据作为静态结点
		if (configuration.getReferences().size() > 0) {
			LOGGER.info(MARKER, "loading <references/> count=", configuration.getReferences().size());
			ReferenceAppBundle references = ReferenceAppBundle.createDefault(configuration.getReferences(), configProvider);
			bundleManager.addBundle(references);
		}

		//
		// - 加载<beans/>静态节点中的数据作为静态结点
		if (configuration.getBeans().size() > 0) {
			LOGGER.info(MARKER, "loading <beans/> count=", configuration.getBeans().size());
			AppBundleHandler defaultBundle = DefaultAppBundle.createAppBundle(configuration.getBeans(), configProvider);
			if (centerService != null) {
				defaultBundle = new CentralizedAppBundleHandler(defaultBundle, centerService);
			}
			bundleManager.addBundle(defaultBundle);
		}

		//
		// - 加载<bundles/>节点中的配置数据
		for (BundleNode node : configuration.getBundles()) {
			String path = node.getPath();
			LOGGER.info(MARKER, "loading bundle path={}", path);
			AppBundleHandler bundle = null;
			if (path.toLowerCase().endsWith(".xml")) {
				bundle = XmlAppBundle.createAppBundle(node, configProvider);
			} else if (path.toLowerCase().endsWith(".jar")) {
				node.setPath(configProvider.findJarFile(path));
				bundle = JarAppBundle.createAppBundle(node, configProvider);
			} else {
				throw new IllegalArgumentException("unknown bundle path = " + path);
			}
			if (centerService != null) {
				bundle = new CentralizedAppBundleHandler(bundle, centerService);
			}
			bundleManager.addBundle(bundle);
		}

		//
		// -- 注册Bundles中的Beans到BeanContextService当中
		LOGGER.info(MARKER, ">>> ========================= resolveBundles ============================ <<<");
		checkErrors(exitOnError, bundleManager.resolveBundles(), "resolveBundles");

		//
		// -- 注册Bundles中的Beans到BeanContextService当中
		LOGGER.info(MARKER, ">>> ========================= registerBundles ============================ <<<");
		checkErrors(exitOnError, bundleManager.registerBundles(this), "registerBundles");

		//
		// - 如果存在CentralizedService，先将reference sync到表中
		if (centerService != null) {
			LOGGER.info(">>> centralizedService.syncReferences ...");
			centerService.syncReferences(this);
			LOGGER.info(">>> centralizedService first sync done.");
		}

		//
		// 组装
		LOGGER.info(MARKER, ">>> ========================= assembleBundles ============================ <<<");
		checkErrors(exitOnError, bundleManager.assembleBundles(this), "assembleBundles");

		//
		// 启动，如果存在CentralizedService, 则自动注册到Center
		LOGGER.info(MARKER, ">>> ========================= startBundles ============================ <<<");
		checkErrors(exitOnError, bundleManager.startBundles(), "startBundles");

		LOGGER.warn(MARKER, ">>> ================= BOOTSTRAP Start Finished ================= <<<");
		for (ServerUrl url : stackManager.getServerUrls()) {
			LOGGER.warn(MARKER, ">>> listening: {}", url.getUrl());
		}
		loaded = true;

		if (runConsole) {
			run();
		}
	}

	private void writePidFile(String pidFile, int pid) {
		try {
			FileWriter fw = new FileWriter(pidFile);
			fw.write(Integer.toString(pid));
			fw.close();
		} catch (IOException ex) {
			LOGGER.error(MARKER, ">>> write pid file failed", ex);
		}
	}

	public void run() {
		while (true) {
			System.out.print("HELIUM$>");
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			try {
				String line = reader.readLine();
				if (line == null) {
					Thread.sleep(60000);
				} else if ("exit".equals(line)) {
					System.exit(0);
				} else {
					System.out.println("Invalided command:" + line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	protected void checkErrors(boolean exitOnError, ErrorList errorList, String action) {
		if (errorList != null && errorList.hasError()) {
			LOGGER.error(action + " failed with Errors");
			if (errorList != null) {
				errorList.printToLogger(LOGGER);
			}
			if (exitOnError) {
				throw new ErrorListException(action + " Failed!", errorList);
			}
		}
	}

	/**
	 * 手工加载一个Bean, 手工加载必须依赖顺序, 用于测试及加载Servlet, 需手工完成注册
	 *
	 * @param
	 */
	public <E> E loadBean(Class<E> beanClazz) {
		try {
			BeanInstance ctx = BeanContextFactory.createInstance(beanClazz, contextProvider);
			ctx.register(this);
			ctx.assemble(this);
			ctx.start();
			return (E) ctx.getBean();
		} catch (Exception ex) {
			throw new RuntimeException("loadInstance failed:" + beanClazz.getName(), ex);
		}
	}


	@Override
	public ServerRouter subscribeServerRouter(BeanContext bc, String bundleName, String protocol) {
		return centerService.subscribeServerRouter(bc, bundleName, protocol);
	}

	@Override
	public CentralizedService getCentralizedService() {
		return centerService;
	}

	@Override
	public String getEnv(String name) {
		if (configuration != null) {
			return EnvUtils.getEnv(configuration, name);
		}

		return null;
	}

	public String getId() {
		return configuration.getId();
	}

	@Override
	public BundleManager getBundleManager() {
		return bundleManager;
	}

	public StackManagerImpl getStackManager() {
		return stackManager;
	}
}

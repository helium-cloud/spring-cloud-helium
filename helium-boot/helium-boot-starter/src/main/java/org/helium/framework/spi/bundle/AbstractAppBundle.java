package org.helium.framework.spi.bundle;

import org.helium.framework.BeanContext;
import org.helium.framework.BeanContextService;
import org.helium.framework.bundle.AppBundleHandler;
import org.helium.framework.bundle.BundleState;
import org.helium.framework.configuration.BeanContextProvider;
import org.helium.framework.configuration.Environments;
import org.helium.framework.entitys.*;
import org.helium.framework.spi.AnnotationResolver;
import org.helium.framework.spi.BeanContextFactory;
import org.helium.framework.spi.BeanInstance;
import org.helium.framework.utils.StateController;
import org.helium.util.CollectionUtils;
import org.helium.util.ErrorList;
import org.helium.util.StringUtils;
import org.helium.util.TypeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by Coral on 10/7/15.
 */
public abstract class AbstractAppBundle implements AppBundleHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAppBundle.class);

	private String location;
	private Map<String, String> defaultVars;
	private BundleConfiguration configuration;
	private BeanContextProvider contextProvider;
	private StateController<BundleState> stateController;
	private List<BeanInstance> instances;
	private ErrorList lastErrors;

	private Logger logger;

	@Override
	public boolean isExport() {
		boolean export = configuration.getParentNode().isExport();
		if (export) {
			return export;
		}
		for (BeanContext bc: getBeans()) {
			if (TypeUtils.isTrue(bc.getConfiguration().getParentNode().getExport())) {
				return true;
			}
		}
		return false;
	}

	public AbstractAppBundle(String location, BundleConfiguration configuration) {
		this.location = location;
		this.configuration = configuration;
		this.stateController = new StateController<>("bundle:" + location, BundleState.INSTALLED);
		this.instances = new ArrayList<>();
		logger = LoggerFactory.getLogger("BUNDLE." + configuration.getName());
	}

	protected void setConfiguration(BundleConfiguration configuration) {
		this.configuration = configuration;
	}

	protected void setContextProvider(BeanContextProvider provider) {
		this.contextProvider = provider;
	}

	@Override
	public StateController<BundleState> getStateController() {
		return stateController;
	}

	@Override
	public String getLocation() {
		return location;
	}

	@Override
	public String getName() {
		return configuration.getName();
	}

	@Override
	public String getVersion() {
		return Environments.RUNTIME_VERSION;
	}

	@Override
	public BundleConfiguration getConfiguration() {
		return configuration;
	}

	@Override
	public List<BeanContext> getBeans() {
		return CollectionUtils.filter(instances, v -> (BeanContext) v);
	}

	@Override
	public void checkErrors(ErrorList errors, String message) {
		lastErrors = errors;
	}

	@Override
	public ErrorList getLastErrors() {
		return lastErrors;
	}

	@Override
	public ErrorList doResolve() {
		ErrorList errors = new ErrorList();
		BeansNode beansNode = configuration.getBeansNode();
		String stacks = null;
		if (beansNode != null && !StringUtils.isNullOrEmpty(beansNode.getStacks())) {
			stacks = contextProvider.applyConfigVar(beansNode.getStacks());
		}

		//
		// 处理读取<beans/>节点
		for (BeanNode node : configuration.getBeans()) {
			node.mergeStacks(stacks);
			if (TypeUtils.isFalse(node.getEnabled())) {
				continue;
			}

			String label = null;
			try {
				BeanConfiguration config;
				if (!StringUtils.isNullOrEmpty(node.getClazz())) {
					label = "clazz:" + node.getClazz();
					Class<?> clazz = contextProvider.loadClass(node.getClazz());
					config = AnnotationResolver.resolveInstance(clazz, contextProvider);
				} else if (!StringUtils.isNullOrEmpty(node.getPath())) {
					label = "path:" + node.getPath();
					config = contextProvider.loadContentXml(node.getPath(), BeanConfiguration.class);
				} else {
					throw new IllegalArgumentException("Illegal bean node:" + node.toString());
				}
				if (!StringUtils.isNullOrEmpty(node.getId())) {
					config.setId(node.getId());
				}

				label = "id:" + config.getId();

				//
				// process: <bean ... setters="field1=value1;field2=value2"/>
				config.getObject().mergeSetters(node.getSetters(), true);

				//
				// loadInstance base modified config
				config.setParentNode(node);
				config.setExport(node.getExport());

				//
				// create instance
				BeanInstance instance = BeanContextFactory.createInstance(config, contextProvider);
				instance.putAttachment("bundle", this.getName() + "@LOCAL");
				instances.add(instance);
			} catch (Exception ex) {
				errors.addError(label, ex);
			}
		}

		return errors.hasError() ? errors : null;
	}

	@Override
	public ErrorList doRegister(BeanContextService contextService) {
		ErrorList errors = new ErrorList();
		for (BeanInstance bean : instances) {
			if (!bean.register(contextService)) {
				errors.addError(bean.getId().toString(), bean.getLastError());
			}
		}
		return errors.hasError() ? errors : null;
	}

	@Override
	public ErrorList doAssemble(BeanContextService contextService) {
		ErrorList errors = new ErrorList();
		for (BeanInstance bean : instances) {
			if (!bean.assemble(contextService)) {
				errors.addError(bean.getId().toString(), bean.getLastError());
			}
		}
		return errors.hasError() ? errors : null;
	}

	@Override
	public ErrorList doUpdate(BeanContextService contextService) {
		ErrorList errors = new ErrorList();
		for (BeanInstance bean : instances) {
			if (!bean.update(contextService)) {
				errors.addError(bean.getId().toString(), bean.getLastError());
			}
		}
		return errors.hasError() ? errors : null;
	}

	@Override
	public ErrorList doStart() {
		ErrorList errors = new ErrorList();
		for (BeanInstance bean : instances) {
			if (!bean.start()) {
				errors.addError(bean.getId().toString(), bean.getLastError());
			}
		}
		return errors.hasError() ? errors : null;
	}

	@Override
	public ErrorList doStop() {
		ErrorList errors = new ErrorList();
		for (BeanInstance bean : instances) {
			if (!bean.stop()) {
				errors.addError(bean.getId().toString(), bean.getLastError());
			}
		}
		return errors.hasError() ? errors : null;
	}

	/**
	 * 提取默认的变量
	 *
	 * @return
	 */
	protected void importDefaultVars() {
		defaultVars = new HashMap<>();
		for (ConfigImportNode node : configuration.getConfigImports()) {
			String key = node.getKey();
			String value = Environments.getVar(key);
			if (value == null) {
				// 只有这种情况会引用
				if (!StringUtils.isNullOrEmpty(node.getDefaultValue())) {
					value = Environments.applyConfigVariable(node.getDefaultValue());    // 替换变量
					defaultVars.put(key, value);
				} else {
					//
					// TODO: 需要处理一下当ConfigImport不存在时的问题
					LOGGER.error("bundle<{}> missing configImport key = {}", location, key);
				}
			}
		}
	}

	protected String applyDefaultVars(String text) {
		for (Entry<String, String> e : defaultVars.entrySet()) {
			text = text.replace("${" + e.getKey() + "}", e.getValue());
		}
		return text;
	}
}

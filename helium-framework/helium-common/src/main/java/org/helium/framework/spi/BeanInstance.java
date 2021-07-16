package org.helium.framework.spi;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.helium.framework.*;
import org.helium.framework.configuration.BeanContextProvider;
import org.helium.framework.configuration.FieldDependency;
import org.helium.framework.entitys.BeanConfiguration;
import org.helium.framework.entitys.ObjectWithSettersNode;
import org.helium.framework.entitys.SetterNode;
import org.helium.framework.entitys.TagNode;
import org.helium.framework.module.Module;
import org.helium.framework.module.ModuleChain;
import org.helium.framework.tag.Tag;
import org.helium.framework.tag.TagMode;
import org.helium.framework.utils.StateController;
import org.helium.threading.ExecutorFactory;
import org.helium.util.CollectionUtils;
import org.helium.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Created by Coral on 7/20/15.
 */
public abstract class BeanInstance extends AbstractBeanContext implements BeanContextLocal {
	private static final Logger LOGGER = LoggerFactory.getLogger(BeanInstance.class);

	private Object bean;
	private Executor executor;
	private StateController<BeanContextState> stateController;
	private Map<String, FieldDependency> references;
	private ModuleChain moduleChain;
	private List<Module> interModules;
	private List<Tag> tags;

	/**
	 * 构造函数
	 * @param configuration
	 */
	public BeanInstance(BeanConfiguration configuration, BeanContextProvider contextProvider) {
		super(configuration, contextProvider);
		this.references = new HashMap<>();
		this.moduleChain = new ModuleChain();
		this.tags = new ArrayList<>();
		this.interModules = new ArrayList<>();
		this.stateController = new StateController<>(getId().toString(), BeanContextState.INITIAL);

		stateController.doAction(BeanContextAction.RESOLVE, () -> {
			resolve();
		});
	}

	@Override
	public Object getBean() {
		return bean;
	}

	@Override
	public Executor getExecutor() {
		return executor;
	}

	public void setExecutor(Executor executor) {
		this.executor = executor;
	}

	@Override
	public BeanContextState getState() {
		return stateController.getState();
	}

	@Override
	public Throwable getLastError() {
		return stateController.getLastError();
	}

	public ModuleChain getModuleChain() {
		return moduleChain;
	}

	@Override
	public boolean register(BeanContextService contextService) {
		return stateController.doAction(BeanContextAction.REGISTER, () -> {
			contextService.addBean(this);
			doRegister(contextService);
		});
	}

	@Override
	public boolean assemble(BeanContextService contextService) {
		return stateController.doAction(BeanContextAction.ASSEMBLE, () -> {
			assembleSetters(contextService);
			assembleTags();
			assembleModules(contextService);
		});
	}

	public boolean update(BeanContextService contextService) {
		assembleSetters(contextService);
		return true;
	}

	@Override
	public boolean start() {
		if (this.getConfiguration().getParentNode() != null) {
			String executorName = this.getConfiguration().getParentNode().getExecutor();
			if (!StringUtils.isNullOrEmpty(executorName)) {
				this.setExecutor(ExecutorFactory.getExecutor(executorName));
			}
		}

		return stateController.doAction(BeanContextAction.START, () -> {
			applyTags(TagMode.ON_START);
			doStart();
		});
	}

	@Override
	public boolean stop() {
		return stateController.doAction(BeanContextAction.STOP, () -> {
			applyTags(TagMode.ON_STOP);
			doStop();
		});
	}

	@Override
	public synchronized List<FieldDependency> getReferences() {
		return CollectionUtils.cloneValues(references);
	}

	protected abstract void doResolve();
	protected abstract void doStart();
	protected abstract void doStop();

	protected void doRegister(BeanContextService contextService) { }

	private void resolve() throws JsonProcessingException {
		BeanConfiguration configuration = getConfiguration();
		//
		// 创建Bean实体对象
		boolean fromClazz = false; // 配置信息是否从类型创建
		ObjectWithSettersNode objNode = configuration.getObject();
		if (objNode.getClazz() != null) {
			fromClazz = true;
			bean = ObjectCreator.createObject(objNode.getClazz());
		} else {
			bean = ObjectCreator.createObject(objNode.getClassName(), className -> getContextProvider().loadClass(className));
		}

		//
		// 如果不是从类型创建的, 通过反射补齐配置
		Class<?> beanClazz = bean.getClass();
		if (!fromClazz) {
			// analyze setters
			List<SetterNode> s2 = AnnotationResolver.resolveSetters(beanClazz, objNode.getSetters(), getContextProvider());
			objNode.setSetters(s2);

			// analyze tags
			List<TagNode> t2 = AnnotationResolver.resolveTags(beanClazz, configuration.getTags(), getContextProvider());
			configuration.setTags(t2);

			for (TagNode tagNode: configuration.getTags()) {
				if (tagNode.getClazz() == null) {
					tagNode.setClazz(getContextProvider().loadClass(tagNode.getClassName()));
				}
			}
		}

		//
		// 设置所有非Reference的设置, /*并完成依赖关系的分析*/
		for (SetterNode node: configuration.getObject().getSetters()) {

			boolean success = SetterInjector.injectFieldSetter(bean, node);
			node.setIsSet(success);
		}

		doResolve();
	}

	private void assembleSetters(BeanContextService contextService) {
		for (SetterNode node : getConfiguration().getObject().getSetters()) {
			if (!node.isSet()) {
				BeanContext refBean = contextService.getBean(node.getValue());
				if (refBean != null) {
					SetterInjector.setField(bean, node.getField(), refBean.getBean());
					node.setIsSet(true);
				} else {
					LOGGER.error("{}.{} missing reference:{}",getId(), node.getField(), node.getValue());
				}

			}
		}
	}

	private void assembleTags() {
		for (TagNode node : getConfiguration().getTags()) {
			Tag tag = (Tag) ObjectCreator.createObject(node.getClazz());
			tag.initWithConfig(this.getBean(), node);
			tags.add(tag);
		}
	}

	private void assembleModules(BeanContextService contextService) throws JsonProcessingException {
		if (getConfiguration().getModules().size() == 0) {
			return;
		}
		for (ObjectWithSettersNode node: getConfiguration().getModules()) {
			Module module;
			if (!StringUtils.isNullOrEmpty(node.getId())) {
				BeanContext bc = contextService.getBean(node.getId());
				if (bc == null) {
					throw new IllegalArgumentException("missing module:" + node.getId());
				}
				module = (Module)bc.getBean();
			} else {
				//TODO: create in resolve phrase, and assemble in this mode
				module = (Module)ObjectCreator.createObject(node, contextService,
						className -> getContextProvider().loadClass(className));
				interModules.add(module); // 将内置tag加入初始化序列
			}
			moduleChain.addModule(module);
		}
	}

	private void applyTags(TagMode mode) throws Exception {
		int i = 0;
		for (Tag tag : tags) {
			if (tag.getModes().contains(mode)) {
				LOGGER.info("ApplyTag {} for {}", tag.getClass().getName() + "-" + (i++), getId());
				tag.applyTag(mode);
			}
		}
		//
		// 执行自带Module上的初始化Tag
		for (Module module: interModules) {
			TagInitializer.applyTags(mode, module);
		}
	}

	public List<Module> getInterModules() {
		return interModules;
	}
}

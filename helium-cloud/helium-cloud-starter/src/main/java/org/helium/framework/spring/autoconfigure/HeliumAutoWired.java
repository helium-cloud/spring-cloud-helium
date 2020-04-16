package org.helium.framework.spring.autoconfigure;

import org.helium.cloud.task.autoconfigure.HeliumTaskConfig;
import org.helium.cloud.task.autoconfigure.TaskEventBeanHandler;
import org.helium.framework.BeanContext;
import org.helium.framework.annotations.ServiceInterface;
import org.helium.framework.module.Module;
import org.helium.framework.spi.BeanInstance;
import org.helium.framework.spi.ModuleInstance;
import org.helium.framework.spi.ServiceInstance;
import org.helium.framework.spi.ServletInstance;
import org.helium.framework.spring.assembly.HeliumAssembly;
import org.helium.framework.task.TaskProducerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * helium支持spring autowire
 */
public class HeliumAutoWired implements ApplicationContextAware {
	/**
	 * 上下文对象实例
	 */
	private static ApplicationContext applicationContext;

	private static final Logger LOGGER = LoggerFactory.getLogger(HeliumAutoWired.class);

	@Resource(name = HeliumTaskConfig.TASK_PRODUCER_FACTORY)
	private TaskProducerFactory taskProducerFactory;

	@Autowired
	private TaskEventBeanHandler taskEventBeanHandler;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		HeliumAutoWired.applicationContext = applicationContext;

		resolveAnnotation();
	}

	private void resolveAnnotation() {
		resolve(this::resolveAutoWired);
		resolve(this::resolvePostConstruct);
	}

	private void resolve(Consumer<BeanContext> consumer) {
		if (HeliumAutoWired.applicationContext == null) {
			throw new RuntimeException("resolveAutoWired And applicationContext Is Null");
		}
		//获取helium所有bean组件
		List<BeanContext> beanContexts = HeliumAssembly.INSTANCE.getBeans();
		for (BeanContext beanContext : beanContexts) {
			consumer.accept(beanContext);
		}
	}

	private void resolveAutoWired(BeanContext beanContext) {
		if (beanContext instanceof ServletInstance) {
			setFieldClass(beanContext.getBean(), beanContext.getBean().getClass());
			resolveModule((BeanInstance) beanContext);
		} else if (beanContext instanceof ModuleInstance ||
				beanContext instanceof ServiceInstance) {
			setFieldClass(beanContext.getBean(), beanContext.getBean().getClass());
		}
	}


	private void resolvePostConstruct(BeanContext beanContext) {
		resolvePostConstruct(beanContext.getBean(), beanContext.getBean().getClass());
	}

	private void resolvePostConstruct(Object object, Class<?> objClz) {
		Class<?> superclass = objClz.getSuperclass();
		if (superclass != null && superclass != Object.class) {
			resolvePostConstruct(object, superclass);
		}

		Method[] declaredMethods = objClz.getDeclaredMethods();
		if (declaredMethods == null || declaredMethods.length == 0) {
			return;
		}

		for (Method method : declaredMethods) {
			PostConstruct post = method.getAnnotation(PostConstruct.class);
			if (post != null) {
				try {
					method.invoke(object);
				} catch (Exception e) {
					LOGGER.error("resolve PostConstruct Error continue:{}", objClz.getName(), e);
				}
			}
		}
	}

	private void resolveModule(BeanInstance beanInstance) {
		List<Module> modules = beanInstance.getInterModules();
		if (modules != null && modules.size() > 0) {
			modules.forEach(module -> {
				setField(module, module.getClass());
				taskEventBeanHandler.setFieldClass(module, module.getClass().getSimpleName());
			});
		}
	}

	private void setFieldClass(Object object, Class<?> objClz) {
		setField(object, objClz);
		Class<?> superclass = objClz.getSuperclass();
		if (superclass != null && superclass != Object.class) {
			setFieldClass(object, superclass);
		}
	}

	private void setField(Object object, Class<?> objClz) {
		for (Field field : objClz.getDeclaredFields()) {
			Autowired autowired = field.getAnnotation(Autowired.class);
			if (autowired != null) {
				try {
					field.setAccessible(true);
					field.set(object, applicationContext.getBean(field.getType()));
				} catch (Exception e) {
					ServiceInterface id = field.getType().getAnnotation(ServiceInterface.class);
					BeanContext context = HeliumAssembly.INSTANCE.getBean(id.id());
					if (context == null) {
						LOGGER.error("resolveAutoWired Error continue:{}", field, e);
					} else {
						try {
							field.set(object, context.getBean());
						} catch (Exception ex) {
							LOGGER.error("resolveAutoWired Error continue:{}", field, ex);
						}
					}
				}

			}

			Resource resource = field.getAnnotation(Resource.class);
			if (resource != null) {
				try {
					field.setAccessible(true);
					field.set(object, applicationContext.getBean(resource.name()));
				} catch (Exception e) {
					BeanContext context = HeliumAssembly.INSTANCE.getBean(resource.name());
					if (context == null) {
						LOGGER.error("resolveAutoWired Error continue:{}", field, e);
					} else {
						try {
							field.set(object, context.getBean());
						} catch (Exception ex) {
							LOGGER.error("resolveAutoWired Error continue:{}", field, ex);
						}
					}
				}

			}
		}
	}

}


package org.helium.framework.spi;

import com.feinno.superpojo.type.Guid;
import org.helium.framework.BeanIdentity;
import org.helium.framework.BeanType;
import org.helium.framework.annotations.*;
import org.helium.framework.configuration.BeanContextProvider;
import org.helium.framework.configuration.FieldLoader;
import org.helium.framework.entitys.*;
import org.helium.framework.servlet.ServletMappings;
import org.helium.framework.tag.Tag;
import org.helium.framework.tag.TagImplementationClass;
import org.helium.framework.task.ScheduledTask;
import org.helium.framework.task.TaskProducerLoader;
import org.helium.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.lang.model.type.NullType;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 用于从注解中获取BeanConfiguration
 * <p>
 * Created by Coral on 7/20/15.
 */
public final class AnnotationResolver {
	private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationResolver.class);

	private BeanContextProvider contextProvider;

	public AnnotationResolver(BeanContextProvider contextProvider) {
		this.contextProvider = contextProvider;
	}

	/**
	 * 通过反射读取Annotation, 创建BeanConfiguration
	 *
	 * @param clazz
	 * @return
	 */
	public static BeanConfiguration resolveInstance(Class<?> clazz, BeanContextProvider contextProvider) {
		BeanConfiguration config = new BeanConfiguration();
		//
		// 读取@ServiceInterface, @ServletImplementation, @TaskImplementation Annotation,获取Bean类型
		Annotation anno;
		Class<?> intfClazz = null;

		//
		// BeanType.Service
		anno = clazz.getAnnotation(ServiceImplementation.class);
		if (anno != null) {
			resolveService(clazz, config, (ServiceImplementation) anno);
		}
		//
		// BeanType.Servlet
		anno = clazz.getAnnotation(ServletImplementation.class);
		if (anno != null) {
			resolveServlet(clazz, config, (ServletImplementation) anno);
		}
		//
		// BeanType.Task
		anno = clazz.getAnnotation(TaskImplementation.class);
		if (anno != null) {
			resolveTask(clazz, config, (TaskImplementation) anno);
		}

		//
		// BeanType.Task, ScheduledTask
		anno = clazz.getAnnotation(ScheduledTaskImplementation.class);
		if (anno != null) {
			resolveScheduledTask(clazz, config, (ScheduledTaskImplementation) anno);
		}

		//
		// BeanType.Module
		anno = clazz.getAnnotation(ModuleImplementation.class);
		if (anno != null) {
			resolveModule(config, (ModuleImplementation) anno);
		}
		//
		// BeanType.Configurator
		anno = clazz.getAnnotation(Configuration.class);
		if (anno != null) {
			resolveConfigurator(clazz, config, (Configuration) anno);
		}

		if (StringUtils.isNullOrEmpty(config.getId())) {
			throw new IllegalArgumentException("AnnotationResolver failed, missing annotation? :" + clazz.getName());
		}

		ObjectWithSettersNode objectNode = new ObjectWithSettersNode();
		objectNode.setClazz(clazz); //NONE XML INFO
		objectNode.setClassName(clazz.getName());
		objectNode.setSetters(resolveSetters(clazz, null, contextProvider));
		config.setObject(objectNode);

		config.setTags(resolveTags(clazz, null, contextProvider));
		config.setModules(resolveModules(clazz, null));

		//
		// 如果ContextProvider不为空， 使用ContextProvider中的信息替换
		BeanConfiguration config2 = config;
		if (contextProvider != null) {
			String xml = config.toXmlString();
			xml = contextProvider.applyConfigText("bean-" + config.getId(), xml);
			config2 = new BeanConfiguration();
			config2.parseXmlFrom(xml);
			config2.getObject().setClazz(clazz);
			config2.getTags().forEach(n -> {
				config.getTags().forEach(n2 -> {
					if (n.getClassName().equals(n2.getClassName())) {
						n.setClazz(n2.getClazz());
					}
				});
			});
			config2.getModules().forEach(n -> {
				config.getModules().forEach(n2 -> {
					if (n.getClassName().equals(n2.getClassName())) {
						n.setClazz(n2.getClazz());
					}
				});
			});
		}

		return config2;
	}

	public static BeanConfiguration resolveReference(Class<?> interfaceClazz) {
		return resolveReference(interfaceClazz, true);
	}

	public static BeanConfiguration resolveReference(Class<?> interfaceClazz, boolean raiseError) {
		ServiceInterface si = interfaceClazz.getAnnotation(ServiceInterface.class);
		if (si == null) {
			if (raiseError) {
				throw new IllegalArgumentException("missing @ServiceInterface: " + interfaceClazz.toString());
			} else {
				BeanConfiguration configuration = new BeanConfiguration();
				configuration.setId("");
				configuration.setType(BeanType.SERVICE.strValue());
				configuration.setInterfaceType(interfaceClazz.getName());
				configuration.setInterfaceClazz(interfaceClazz);
				return configuration;
			}
		}
		BeanConfiguration configuration = new BeanConfiguration();
		configuration.setId(si.id());
		configuration.setType(BeanType.SERVICE.strValue());
		configuration.setInterfaceType(interfaceClazz.getName());
		configuration.setInterfaceClazz(interfaceClazz);
		return configuration;
	}

	/**
	 * 分析Fields
	 *
	 * @param clazz
	 * @param list
	 * @return
	 */
	public static List<SetterNode> resolveSetters(Class<?> clazz, List<SetterNode> list, BeanContextProvider cp) {
		if (list == null) {
			list = new ArrayList<>();
		}
		for (Class<?> c2 = clazz; !c2.equals(Object.class); c2 = c2.getSuperclass()) {
			for (Field field : c2.getDeclaredFields()) {
				FieldSetter fieldSetter = field.getAnnotation(FieldSetter.class);
				ServiceSetter serviceSetter = field.getAnnotation(ServiceSetter.class);
				TaskEvent taskEvent = field.getAnnotation(TaskEvent.class);

				String value;
				int timeout = 0;
				Class<? extends FieldLoader> loaderClazz = null;

				//
				// 允许@ServiceSetter不加参数注入, 但不允许@FieldSetter不加参数注入
				if (serviceSetter != null) {
					if (StringUtils.isNullOrEmpty(serviceSetter.id())) {
						ServiceInterface si = field.getType().getAnnotation(ServiceInterface.class);
						if (si == null) {
							throw new IllegalArgumentException("@ServiceSetter class has't @ServiceInterface :" + field.getName());
						} else {
							value = si.id();
						}
					} else {
						value = serviceSetter.id();
					}
					//timeout = serviceSetter.timeout();

				}
//				else if (fieldSetter != null) {
//					value = fieldSetter.value();
//					loaderClazz = FieldLoader.Null.class.equals(fieldSetter.loader()) ? null : fieldSetter.loader();
//				}
				else {
					continue;
				}

				//
				// 如果xml中已经存在配置<setter>节点了, 则以xml为准, 忽略Annotation
				boolean hasSetter = false;
				for (SetterNode item : list) {
					if (item.getField().equals(field.getName())) {
						hasSetter = true;
						break;
					}
				}
				if (hasSetter) {
					continue;
				}

				if (cp != null) {
					value = cp.applyConfigVar(value);
				}

				SetterNode node = SetterNode.create(field.getName(), loaderClazz, value);
				node.setLoaderClazz(loaderClazz);
				//node.setTimeout(timeout);
				list.add(node);
			}
		}
		return list;
	}

	public static List<TagNode> resolveTags(Class<?> clazz, List<TagNode> list, BeanContextProvider cp) {
		if (list == null) {
			list = new ArrayList<>();
		}
		//
		// extract class, only this class
		extractTags(clazz, list, cp);

		//
		// extract methods, include parent class
		for (Class<?> c2 = clazz; !c2.equals(Object.class); c2 = c2.getSuperclass()) {
			for (Method method : c2.getMethods()) {
				extractTags(method, list, cp);
			}
		}
		return list;
	}

	//TODO: support @Module annotations
	public static List<ObjectWithSettersNode> resolveModules(Class<?> clazz, List<ObjectWithSettersNode> list) {
		if (list == null) {
			list = new ArrayList<>();
		}
		return list;
	}

	/**
	 * 从继承关系中获取一个唯一的标记了@ServiceInterface的对象
	 *
	 * @param clazz
	 * @return
	 */
	public static Class<?> getServiceInterface(Class<?> clazz) {
		Class<?> r = null;
		for (Class<?> c2 = clazz; !Object.class.equals(c2); c2 = c2.getSuperclass()) {
			for (Class<?> intf : c2.getInterfaces()) {
				ServiceInterface si = intf.getAnnotation(ServiceInterface.class);
				if (si != null) {
					if (r != null) {
						throw new IllegalArgumentException("Class has ambiguous ServiceInterface:" + clazz.getName());
					}
					r = intf;
				}
			}
		}
		if (r == null) {
			throw new IllegalArgumentException("@ServiceInterface not found in:" + clazz.getName());
		}
		return r;
	}

	private static void extractTags(AnnotatedElement element, List<TagNode> list, BeanContextProvider cp) {
		for (Annotation a : element.getAnnotations()) {
			TagImplementationClass implClass = a.annotationType().getAnnotation(TagImplementationClass.class);
			if (implClass != null) {
				Class<? extends Tag> tagClazz = implClass.value();
				Tag tag;
				try {
					tag = tagClazz.newInstance();
				} catch (Exception e) {
					throw new RuntimeException("can't create Tag:" + tagClazz.getName(), e);
				}
				tag.initWithAnnotation(null, a, element);
				TagNode node = tag.getTagNode();
				list.add(node);
			}
		}
	}

	private static void resolveModule(BeanConfiguration config, ModuleImplementation a) {
		config.setType(BeanType.MODULE.strValue());
		ModuleImplementation a2 = a;
		BeanIdentity id;
		if (!StringUtils.isNullOrEmpty(a2.id())) {
			id = BeanIdentity.parseFrom(a2.id());
		} else {
			id = new BeanIdentity("module", UUID.randomUUID().toString());
		}
		config.setId(id.toString());
	}

	private static void resolveTask(Class<?> clazz, BeanConfiguration config, TaskImplementation a) {
		config.setType(BeanType.TASK.strValue());
		TaskImplementation a2 = a;

		BeanIdentity eventId = BeanIdentity.parseFrom(a2.event());
		BeanIdentity id;
		if (StringUtils.isNullOrEmpty(a2.id())) {
			String name = clazz.getName();
			id = new BeanIdentity(eventId.getGroup(), name);
		} else {
			id = BeanIdentity.parseFrom(a2.id());
		}
		config.setId(id.toString());
		config.setEvent(a2.event());
		config.setStorageType(a2.storage());
	}

	private static void resolveScheduledTask(Class<?> clazz, BeanConfiguration config, ScheduledTaskImplementation a) {
		config.setType(BeanType.TASK.strValue());
		BeanIdentity id = BeanIdentity.parseFrom(a.id());
		config.setId(id.toString());
		config.getExtensions().add(new KeyValueNode(ScheduledTask.EXTENSION_KEY_CRON, a.cronExpression()));
		config.getExtensions().add(new KeyValueNode(ScheduledTask.EXTENSION_KEY_ENABLE_REENTRY, Boolean.toString(a.enableReentry())));
	}

	private static void resolveServlet(Class<?> clazz, BeanConfiguration config, ServletImplementation anno) {
		config.setType(BeanType.SERVLET.strValue());
		ServletImplementation a2 = anno;
		String id = a2.id();
		if (id.contains("${AUTO_GUID}")) {
			id = id.replace("${AUTO_GUID}", Guid.randomGuid().toStr().replace("-", ""));
		}
		config.setId(id);

		//
		// reflect mappings
		ServletMappings mappings = null;
		for (Annotation a : clazz.getAnnotations()) {
			ServletMappingsClass smc = a.annotationType().getAnnotation(ServletMappingsClass.class);
			if (smc != null) {
				mappings = (ServletMappings) ObjectCreator.createObject(smc.value());
				mappings.initWithAnnotation(a);
				LOGGER.info("got ServletMappings:{}", mappings);
				break;
			}
		}
		if (mappings != null) {
			ServletMappingsNode node = mappings.getMappingsNode();
			config.setServletMappings(node);
		}
	}

	private static void resolveConfigurator(Class<?> clazz, BeanConfiguration config, Configuration anno) {
		config.setType(BeanType.CONFIGURATOR.strValue());
		config.setId("config:" + clazz.getName());
		config.setPath(anno.path());
	}


	private static void resolveService(Class<?> clazz, BeanConfiguration config, ServiceImplementation a) {
		config.setType(BeanType.SERVICE.strValue());
		//
		// 获取@ServiceInterface接口类型
		Class<?> intfClazz;
		if (!NullType.class.equals(a.interfaceType())) {
			intfClazz = a.interfaceType();
		} else {
			intfClazz = getServiceInterface(clazz);
		}
		//
		// 获取Bean-Id
		BeanIdentity id;
		if (StringUtils.isNullOrEmpty(a.id())) {
			//
			// 需要从Interface中反射消息
			ServiceInterface intfAnno = intfClazz.getAnnotation(ServiceInterface.class);
			id = BeanIdentity.parseFrom(intfAnno.id());
		} else {
			id = BeanIdentity.parseFrom(a.id());
		}
		config.setId(id.toString());
		config.setInterfaceType(intfClazz.getTypeName());
	}
}
package org.helium.cloud.task.autoconfigure;

import org.helium.cloud.task.TaskBeanInstance;
import org.helium.cloud.task.TaskCounter;
import org.helium.cloud.task.annotations.TaskImplementation;
import org.helium.cloud.task.scan.HeliumClassPathBeanDefinitionScanner;
import org.helium.cloud.task.utils.TaskBeanUtils;
import org.helium.perfmon.PerformanceCounterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.beans.factory.support.*;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.rootBeanDefinition;
import static org.springframework.context.annotation.AnnotationConfigUtils.CONFIGURATION_BEAN_NAME_GENERATOR;
import static org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation;
import static org.springframework.util.ClassUtils.resolveClassName;

/**
 * {@link TaskImplementation} Annotation
 * {@link BeanDefinitionRegistryPostProcessor Bean Definition Registry Post Processor}
 *
 * @since 2.5.8
 */

public class TaskImplementationAnnotationBeanPostProcessor implements BeanDefinitionRegistryPostProcessor, EnvironmentAware,
        ResourceLoaderAware, BeanClassLoaderAware {


    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Set<String> packagesToScan;

    private Environment environment;

    private ResourceLoader resourceLoader;

    private ClassLoader classLoader;


    public TaskImplementationAnnotationBeanPostProcessor(String... packagesToScan) {
        this(Arrays.asList(packagesToScan));
    }

    public TaskImplementationAnnotationBeanPostProcessor(Collection<String> packagesToScan) {
        this(new LinkedHashSet<>(packagesToScan));
    }

    public TaskImplementationAnnotationBeanPostProcessor(Set<String> packagesToScan) {
        this.packagesToScan = packagesToScan;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {


        Set<String> resolvedPackagesToScan = resolvePackagesToScan(packagesToScan);

        if (!CollectionUtils.isEmpty(resolvedPackagesToScan)) {
            registerServiceBeans(resolvedPackagesToScan, registry);
        } else {
            if (logger.isWarnEnabled()) {
                logger.warn("packagesToScan is empty , ServiceBean registry will be ignored!");
            }
        }

    }


    /**
     * Registers Beans whose classes was annotated {@link TaskImplementation}
     *
     * @param packagesToScan The base packages to scan
     * @param registry       {@link BeanDefinitionRegistry}
     */
    private void registerServiceBeans(Set<String> packagesToScan, BeanDefinitionRegistry registry) {

        HeliumClassPathBeanDefinitionScanner scanner =
				new HeliumClassPathBeanDefinitionScanner(registry, environment, resourceLoader);

        BeanNameGenerator beanNameGenerator = resolveBeanNameGenerator(registry);

        scanner.setBeanNameGenerator(beanNameGenerator);

        scanner.addIncludeFilter(new AnnotationTypeFilter(TaskImplementation.class));

        for (String packageToScan : packagesToScan) {

            // Registers @Service Bean first
            scanner.scan(packageToScan);

            // Finds all BeanDefinitionHolders of @Service whether @ComponentScan scans or not.
            Set<BeanDefinitionHolder> beanDefinitionHolders =
                    findServiceBeanDefinitionHolders(scanner, packageToScan, registry, beanNameGenerator);

            if (!CollectionUtils.isEmpty(beanDefinitionHolders)) {

                for (BeanDefinitionHolder beanDefinitionHolder : beanDefinitionHolders) {
                	try {
						registerServiceBean(beanDefinitionHolder, registry, scanner);
					} catch (Exception e){
                		logger.error("beanDefinitionHolder Exception", e);
					}

                }

                if (logger.isInfoEnabled()) {
                    logger.info(beanDefinitionHolders.size() + " annotated Dubbo's @Service Components { " +
                            beanDefinitionHolders +
                            " } were scanned under package[" + packageToScan + "]");
                }

            } else {

                if (logger.isWarnEnabled()) {
                    logger.warn("No Spring Bean annotating Dubbo's @Service was found under package["
                            + packageToScan + "]");
                }

            }

        }

    }

    /**
     * It'd better to use BeanNameGenerator instance that should reference
     * {@link ConfigurationClassPostProcessor},
     * thus it maybe a potential problem on bean name generation.
     *
     * @param registry {@link BeanDefinitionRegistry}
     * @return {@link BeanNameGenerator} instance
     * @see SingletonBeanRegistry
     * @see AnnotationConfigUtils#CONFIGURATION_BEAN_NAME_GENERATOR
     * @see ConfigurationClassPostProcessor#processConfigBeanDefinitions
     * @since 2.5.8
     */
    private BeanNameGenerator resolveBeanNameGenerator(BeanDefinitionRegistry registry) {

        BeanNameGenerator beanNameGenerator = null;

        if (registry instanceof SingletonBeanRegistry) {
            SingletonBeanRegistry singletonBeanRegistry = SingletonBeanRegistry.class.cast(registry);
            beanNameGenerator = (BeanNameGenerator) singletonBeanRegistry.getSingleton(CONFIGURATION_BEAN_NAME_GENERATOR);
        }

        if (beanNameGenerator == null) {

            if (logger.isInfoEnabled()) {

                logger.info("BeanNameGenerator bean can't be found in BeanFactory with name ["
                        + CONFIGURATION_BEAN_NAME_GENERATOR + "]");
                logger.info("BeanNameGenerator will be a instance of " +
                        AnnotationBeanNameGenerator.class.getName() +
                        " , it maybe a potential problem on bean name generation.");
            }

            beanNameGenerator = new AnnotationBeanNameGenerator();

        }

        return beanNameGenerator;

    }

    /**
     * Finds a {@link Set} of {@link BeanDefinitionHolder BeanDefinitionHolders} whose bean type annotated
     * {@link TaskImplementation} Annotation.
     *
     * @param scanner       {@link ClassPathBeanDefinitionScanner}
     * @param packageToScan pachage to scan
     * @param registry      {@link BeanDefinitionRegistry}
     * @return non-null
     * @since 2.5.8
     */
    private Set<BeanDefinitionHolder> findServiceBeanDefinitionHolders(
            ClassPathBeanDefinitionScanner scanner, String packageToScan, BeanDefinitionRegistry registry,
            BeanNameGenerator beanNameGenerator) {

        Set<BeanDefinition> beanDefinitions = scanner.findCandidateComponents(packageToScan);

        Set<BeanDefinitionHolder> beanDefinitionHolders = new LinkedHashSet<>(beanDefinitions.size());

        for (BeanDefinition beanDefinition : beanDefinitions) {

            String beanName = beanNameGenerator.generateBeanName(beanDefinition, registry);
            BeanDefinitionHolder beanDefinitionHolder = new BeanDefinitionHolder(beanDefinition, beanName);
            beanDefinitionHolders.add(beanDefinitionHolder);

        }

        return beanDefinitionHolders;

    }

    /**
     * Registers {@link } from new annotated {@link TaskImplementation} {@link BeanDefinition}
     *
     * @param beanDefinitionHolder
     * @param registry
     * @param scanner
     * @see BeanDefinition
     */
    private void registerServiceBean(BeanDefinitionHolder beanDefinitionHolder, BeanDefinitionRegistry registry,
                                     HeliumClassPathBeanDefinitionScanner scanner) {

        Class<?> beanClass = resolveClass(beanDefinitionHolder);

        TaskImplementation taskImplementation = findMergedAnnotation(beanClass, TaskImplementation.class);

        Class<?> interfaceClass = resolveServiceInterfaceClass(beanClass, taskImplementation);

		//
		BeanDefinition taskBeanDefinition =
                buildTaskBeanDefinition(taskImplementation, interfaceClass);
		BeanDefinition taskImplementationDefinition =
				buildTaskImplementationDefinition(taskImplementation, beanClass);

		// ServiceBean Bean name
		String beanName = taskImplementation.event();

        if (scanner.checkCandidate(beanName, taskBeanDefinition)) { // check duplicated candidate bean
            registry.registerBeanDefinition(TaskBeanUtils.getBeanInstance(beanName), taskBeanDefinition);
			registry.registerBeanDefinition(TaskBeanUtils.getBeanImpl(beanName), taskImplementationDefinition);
            if (logger.isInfoEnabled()) {
                logger.info("The BeanDefinition[" + taskBeanDefinition +
                        "] of ServiceBean has been registered with name : " + beanName);
            }

        } else {

            if (logger.isWarnEnabled()) {
                logger.warn("The Duplicated BeanDefinition[" + taskBeanDefinition +
                        "] of ServiceBean[ bean name : " + beanName +
                        "] was be found , Did @DubboComponentScan scan to same package in many times?");
            }

        }

    }

    private Class<?> resolveServiceInterfaceClass(Class<?> annotatedServiceBeanClass, TaskImplementation taskImplementation) {

        Class<?> interfaceClass = null;

		Class<?>[] allInterfaces = ClassUtils.getAllInterfacesForClass(annotatedServiceBeanClass);

		if (allInterfaces.length > 0) {
			interfaceClass = allInterfaces[0];
		}

        Assert.notNull(interfaceClass,
                "@Service interfaceClass() or interfaceName() or interface class must be present!");

        Assert.isTrue(interfaceClass.isInterface(),
                "The type that was annotated @Service is not an interface!");

        return interfaceClass;
    }

    private Class<?> resolveClass(BeanDefinitionHolder beanDefinitionHolder) {

        BeanDefinition beanDefinition = beanDefinitionHolder.getBeanDefinition();

        return resolveClass(beanDefinition);

    }

    private Class<?> resolveClass(BeanDefinition beanDefinition) {

        String beanClassName = beanDefinition.getBeanClassName();

        return resolveClassName(beanClassName, classLoader);

    }

    private Set<String> resolvePackagesToScan(Set<String> packagesToScan) {
        Set<String> resolvedPackagesToScan = new LinkedHashSet<String>(packagesToScan.size());
        for (String packageToScan : packagesToScan) {
            if (StringUtils.hasText(packageToScan)) {
                String resolvedPackageToScan = environment.resolvePlaceholders(packageToScan.trim());
                resolvedPackagesToScan.add(resolvedPackageToScan);
            }
        }
        return resolvedPackagesToScan;
    }

    private AbstractBeanDefinition buildTaskBeanDefinition(TaskImplementation taskImplementation, Class<?> interfaceClass) {
		BeanDefinitionBuilder builder = rootBeanDefinition(TaskBeanInstance.class);

		try {
			//
			builder.addPropertyValue("id", taskImplementation.id());
			builder.addPropertyValue("event", taskImplementation.event());
			builder.addPropertyValue("storage", taskImplementation.storage());
			builder.addPropertyValue("counter",
					PerformanceCounterFactory.getCounters(TaskCounter.class, taskImplementation.event()));
		} catch (Exception e){
			logger.error("buildTaskBeanDefinition Error.", e);
		}

        return builder.getBeanDefinition();

    }

	private AbstractBeanDefinition buildTaskImplementationDefinition(TaskImplementation taskImplementation,
																	 Class<?> interfaceClass) {
		BeanDefinitionBuilder builder = rootBeanDefinition(interfaceClass);
		return builder.getBeanDefinition();
	}



    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

}

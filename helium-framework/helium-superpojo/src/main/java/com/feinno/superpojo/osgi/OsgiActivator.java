package com.feinno.superpojo.osgi;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;

/**
 * OSGi启动器
 * <p/>
 * Created by Panying on 2015/4/28.
 */
public class OsgiActivator implements BundleActivator {

    static final Logger logger = LoggerFactory.getLogger(OsgiActivator.class);

    public BundleContext getContext() {
        return context;
    }

    private BundleContext context;

    private Set<Bundle> requireBundles;

    private static OsgiActivator instance;

    private static ClassLoader rootClassLoader;

    public static OsgiActivator getInstance() {
        return instance;
    }

    @Override
    public void start(BundleContext context) throws Exception {

        this.context = context;

        OsgiActivator.instance = this;
        OsgiActivator.rootClassLoader = getInstance().getContext().getBundle(0).adapt(BundleWiring.class).getClassLoader();

        // 初始化本bundle的所有依赖bundle集合
        requireBundles = new HashSet<>();
        initRequireBundles(context.getBundle(), requireBundles);

        OsgiEnv.IS_ACTIVE = true;
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        OsgiEnv.IS_ACTIVE = false;
    }


    /**
     * 将给出的bundle及其依赖的所有bundle填充到一个Set中去
     *
     * @param bundle
     * @param fillList
     */
    public static void initRequireBundles(Bundle bundle, Set<Bundle> fillList) {
        if (!fillList.contains(bundle)) {
            fillList.add(bundle);
            for (BundleWire requiredWires : bundle.adapt(BundleWiring.class).getRequiredWires(null)) {
                initRequireBundles(requiredWires.getProvider().getBundle(), fillList);
            }
        }
    }

    /**
     * 获取当前Bundle的ClassLoader
     *
     * @return
     */
    public static ClassLoader getClassLoader() {
        return getInstance().getContext().getBundle().adapt(BundleWiring.class).getClassLoader();
    }

    /**
     * 判断给出的ClassLoader是不是根（JVM）Classloader
     *
     * @param classLoader
     * @return
     */
    public static boolean isRootClassLoader(ClassLoader classLoader) {
        return rootClassLoader.equals(classLoader);
    }

    /**
     * 获取bundle中jar包路径
     *
     * @param bundle
     * @return
     */
    private List<String> getBundleClassPath(Bundle bundle) {
        //return getFixedClassPath();
        List<String> ref = new ArrayList<String>();
        try {
            String rootPath = bundle.getLocation();
            String[] splitAt = rootPath.split("@");
            rootPath = splitAt[splitAt.length - 1];
            String[] splitColon = rootPath.split(":");
            rootPath = splitColon[splitColon.length - 1];
            //拼上运行时路径就可以生成其File对象了
            File f = new File(rootPath);

            rootPath = f.getCanonicalPath();
            //System.out.println(rootPath);
            ref.add(rootPath);
            Enumeration<URL> jarFileEntries = bundle.findEntries("/", "*.jar",
                    true);
            if (jarFileEntries == null)
                return ref;
            while (jarFileEntries.hasMoreElements()) {
                try {
                    URL url = jarFileEntries.nextElement();
                    //System.out.println(url);
                    String s = url.getFile();
                    if (s.startsWith("/") || s.startsWith("\\"))
                        s = s.substring(1);

                    Path path2 = f.toPath();
                    Path path3 = path2.resolve(s);
                    // path3.get
                    //url = new URL(FILE_SCHEME + path3.toFile().getCanonicalPath());
                    ref.add(path3.toFile().getCanonicalPath());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ref;
    }

    /**
     * 获取给出classloader所在bundle的所有依赖的ClassPath
     *
     * @param cl
     * @return
     */
    public List<String> getClassPath(ClassLoader cl) {

        Set<Bundle> allReqBundles = new HashSet<>(requireBundles);
        for (Bundle bundle : context.getBundles()) {
            BundleWiring wiring = bundle.adapt(BundleWiring.class);
            if (wiring != null && cl == wiring.getClassLoader()) {
                initRequireBundles(bundle, allReqBundles);
            }
        }

        List<String> result = new ArrayList<>();
        for (Bundle bun : allReqBundles) {
            result.addAll(getBundleClassPath(bun));
        }
        return result;

    }
}

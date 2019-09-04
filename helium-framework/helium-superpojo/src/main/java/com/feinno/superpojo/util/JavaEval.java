package com.feinno.superpojo.util;

import com.feinno.superpojo.osgi.OsgiActivator;
import com.feinno.superpojo.osgi.OsgiEnv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.jvmstat.monitor.HostIdentifier;
import sun.jvmstat.monitor.MonitoredHost;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <b>描述: </b>这是一个JAVA版的Eval实现，可以在运行时将某一个字符串编译成class，以及创建这个class的对象<br>
 * Java是一个静态语言，此类可以稍微的弥补一些，使Java运行时可以动态的将源文件编译并且加载到当前的环境中来，主要用于序列化辅助代码的生成以及加载
 * <p/>
 * <b>功能: </b>运行时编译及加载类到当前环境中
 * <p/>
 * <b>用法: </b>
 * <p/>
 * <pre>
 * 源文件编译并返回对应的实例对象:
 *
 * String java0 = ...
 * Object object = JavaEval.eval(java1);//编译并返回对应类实例对象
 *
 * String java1 = ... //一个继承自IAutoClassTest接口的类的源代码
 * IAutoClassTest autoClassTest = JavaEval.eval(IAutoClassTest.class, java1);//将一个源代码编译后转型成指定接口
 * autoClassTest.sayFeinno("Feinno");//调用接口方法
 *
 *
 * JavaEval.compile(java0, java1);//可以同时编译多个类
 * </pre>
 * <p/>
 *
 * @author Lv.Mingwei
 */
public class JavaEval {

    /**
     * 用于从字符串中提取包名称的正则表达式
     */
    private static Pattern packPattern = Pattern.compile("package\\s+[^\\s]+;{1}");

    /**
     * 用于从字符串中提取类名称的正则表达式
     */
    private static Pattern classNamePattern = Pattern.compile("class\\s+[^\\s]+\\s{1}");

    /**
     * 用于从字符串中提取接口名称的正则表达式
     */
    private static Pattern interfaceNamePattern = Pattern.compile("interface\\s+[^\\s]+\\s{1}");

    /**
     * 用于从字符串中提取枚举名称的正则表达式
     */
    private static Pattern enumNamePattern = Pattern.compile("enum\\s+[^\\s]+\\s{1}");

    protected static String SAVE_CLASS_PATH = null;

    private JavaEval() {
    }

    /**
     * 根据字符串所表示的JAVA源码生成对应的JAVA对象
     *
     * @param source
     * @return
     * @throws Exception
     */
    public static Object eval(String source) throws JavaEvalException, ClassNotFoundException, IllegalAccessException,
            InstantiationException {
        return JavaEval.eval(Object.class, source);
    }

    /**
     * 根据字符串所表示的JAVA源码生成对应的JAVA对象,如果可以确定这个类是某一类型的子类，那么允许传入这个父类型，届时会上转型导出这个父类
     *
     * @param clazz
     * @param source
     * @return
     * @throws Exception
     */
    public static <T> T eval(Class<T> clazz, String source) throws JavaEvalException, ClassNotFoundException,
            IllegalAccessException, InstantiationException {
        // 如果编译成功了，则创建这个类的对象，并强转型为向需要的类型，如果失败了，这里会抛出异常，异常将上抛给调用者
        ClassPathInfo classPathInfo = null;
        if (clazz.getClassLoader() != null) {
            classPathInfo = ClassPathCache.getClassPathInfo(clazz.getClassLoader());
        } else {
            if (OsgiEnv.IS_ACTIVE) {
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                if (OsgiActivator.isRootClassLoader(classLoader)) {
                    classPathInfo = ClassPathCache.getClassPathInfo(OsgiActivator.getClassLoader());
                } else {
                    classPathInfo = ClassPathCache.getClassPathInfo(classLoader);
                }
            } else {
                classPathInfo = ClassPathCache.getClassPathInfo();
            }
        }

        compile(classPathInfo, source);

        String classInfo[] = JavaEval.getPackageAndClassName(source);
        String className = classInfo[0] != null && classInfo[0].length() > 0 ? classInfo[0] + "." + classInfo[1] : classInfo[1];
        Class<?> classTempClass = Class.forName(className, true, classPathInfo.getClassLoader());
        @SuppressWarnings("unchecked")
        T instance = (T) classTempClass.newInstance();
        return instance;
    }

    /**
     * 编译指定的源码，可同时编译多个源码，解决源码之间的相互嵌套递归问题
     *
     * @param sources 可变参数，可以同时编译多个源代码
     * @throws JavaEvalException 如果编译失败，会抛出这个异常，接到异常后请务必处理，否则在接下来想要引用这个类时会引用失败
     */
    public static void compile(String... sources) throws JavaEvalException {
        compile(ClassPathCache.getClassPathInfo(), sources);
    }

    /**
     * 编译指定的源码，可同时编译多个源码，解决源码之间的相互嵌套递归问题
     *
     * @param classLoader 使用指定的ClassLoader来进行编译
     * @param sources     可变参数，可以同时编译多个源代码
     * @throws JavaEvalException 如果编译失败，会抛出这个异常，接到异常后请务必处理，否则在接下来想要引用这个类时会引用失败
     */
    public static <T> void compile(ClassLoader classLoader, String... sources) throws JavaEvalException {
        compile(ClassPathCache.getClassPathInfo(classLoader), sources);
    }

    /**
     * 编译指定的源码，可同时编译多个源码，解决源码之间的相互嵌套递归问题
     *
     * @param sources 可变参数，可以同时编译多个源代码
     * @throws JavaEvalException 如果编译失败，会抛出这个异常，接到异常后请务必处理，否则在接下来想要引用这个类时会引用失败
     */
    public static void compile(ClassPathInfo classPathInfo, String... sources) throws JavaEvalException {
        // 此Write对象用于存储编译时信息，当编译出错时，它记录的信息将会以异常的形式抛出
        StringWriter compileInfo = null;
        try {
            /** Step 1.创建待编译的源码列表 */
            JavaSourceFromString JavaSourceFromStringArray[] = new JavaSourceFromString[sources.length];
            int index = 0;
            for (String source : sources) {
                // 根据源码分析出包名与类名,包名允许为空
                String classInfo[] = JavaEval.getPackageAndClassName(source);
                // 创建对象所需的第一个参数为 包名.类名，第二个参数为源代码
                JavaSourceFromStringArray[index++] = new JavaSourceFromString(classInfo[0] != null
                        && classInfo[0].length() > 0 ? classInfo[0] + "." + classInfo[1] : classInfo[1], source);// 创建源码对象
            }

            /** Step 2.获取平台的编译器 */
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            if (compiler == null) {
                // 必须在当前环境中取到tools这个JAR包才可以进行动态编译，否则无法取到默认编译器
                throw new JavaEvalException(
                        "Can not find tools.jar in the current environment,please check java class path!");
            }
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
            // 指定Class的默认输出路径，如果在Eclipse中没有它，会输出到了工程根路径而非bin下，导致实例化时因为找不到类文件而失败
            Iterable<String> options = null;
            if (classPathInfo.getSavePath() != null) {
                options = Arrays.asList("-d", classPathInfo.getSavePath(), "-classpath", classPathInfo.getClassPath());
            }
            Iterable<? extends JavaFileObject> files = Arrays.asList(JavaSourceFromStringArray);

            /** Step 3.创建编译任务并且执行 */
            compileInfo = new StringWriter();
            JavaCompiler.CompilationTask task = compiler.getTask(compileInfo, fileManager, null, options, null, files);
            // 初次使用task.call时可能有些慢，因为javac需要初始化许多东西，在第二次执行时速度会快很多，目前动态生成的速度慢的瓶颈在这里
            if (!task.call()) {
                throw new JavaEvalException(String.format(
                        "Compiler Java Soruce Error! Please Check Source. Error Reason:\n%s", compileInfo.toString()));
            }
        } catch (JavaEvalException e) {
            throw e;
        } catch (Exception e) {
            throw new JavaEvalException(String.format(
                    "Compiler Java Soruce Error! Please Check Source. Error Reason:\n%s", Formater.formaError(e)));
        } finally {
            try {
                if (compileInfo != null) {
                    compileInfo.close();
                }
            } catch (IOException e) {
                throw new JavaEvalException(String.format("Close StringWriter Error! Error Reason:\n%s",
                        Formater.formaError(e)));
            }
        }
    }

    /**
     * 获取包名和类名
     *
     * @param source 这是一个数组，数组第0位是包名，第1位是类名
     * @return
     */
    private static String[] getPackageAndClassName(String source) {
        String result[] = new String[2];
        // 取字符串所代表的源码中的报名
        Matcher matcher = packPattern.matcher(source);
        if (matcher.find()) {
            result[0] = (matcher.group(0));
            result[0] = result[0] != null ? result[0].replace("package", "").replaceAll(";", "").trim() : result[0];
        }
        // 取字符串所代表的源码中的类名
        matcher = classNamePattern.matcher(source);
        if (matcher.find()) {
            result[1] = (matcher.group(0));
            result[1] = result[1] != null ? result[1].replace("class", "").trim() : result[1];
        }
        // 如果通过类名没有取到，那么通过接口的正则进行判断
        if (result[1] == null || result[1].length() == 0) {
            matcher = interfaceNamePattern.matcher(source);
            if (matcher.find()) {
                result[1] = (matcher.group(0));
                result[1] = result[1] != null ? result[1].replace("interface", "").trim() : result[1];
            }
        }
        // 如果通过还是没有取到，那么通过枚举的正则进行判断
        if (result[1] == null || result[1].length() == 0) {
            matcher = enumNamePattern.matcher(source);
            if (matcher.find()) {
                result[1] = (matcher.group(0));
                result[1] = result[1] != null ? result[1].replace("enum", "").trim() : result[1];
            }
        }
        return result;
    }

    /**
     * 创建一个指定类路径的实例，此类路径为全路径
     *
     * @param clazz
     * @param classPath
     * @return
     */
    public static <T> T newClassInstance(Class<T> clazz, String classPath) {
        return newClassInstance(ClassPathCache.getClassPathInfo().getClassLoader(), clazz, classPath);
    }

    /**
     * 创建一个指定类路径的实例，此类路径为全路径
     *
     * @param classLoader 此对象的类加载器
     * @param clazz
     * @param classPath
     * @return
     */
    public static <T> T newClassInstance(ClassLoader classLoader, Class<T> clazz, String classPath) {
        try {
            Class<?> classTemp = Class.forName(classPath, true, ClassPathCache.getClassPathInfo(classLoader)
                    .getClassLoader());
            @SuppressWarnings("unchecked")
            T instance = (T) classTemp.newInstance();
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("JavaEval.newClassInstance()  found error:", e);
        }
    }

    public static String getRootClassPath() {
        return ClassPathCache.getClassPathInfo().getSavePath();
    }

    public static ClassLoader getSpecialClassLoader() {
        return ClassPathCache.getClassPathInfo().getClassLoader();
    }

    public static void setSaveClassPath(String path) {
        SAVE_CLASS_PATH = path;
        File tempFile = new File(SAVE_CLASS_PATH);
        if (!tempFile.exists()) {
            tempFile.mkdirs();
        }
    }
}

/**
 * 源文件对象，使用他来包装以String字符串存储的源码对象，把这个String源码对象装饰成一个类似的File文件，用白话来说，就是用来欺骗编译器，
 * 把它当做文件系统中读取到的源代码
 */
class JavaSourceFromString extends SimpleJavaFileObject {
    /**
     * 源码(虚拟文件的源代码)
     */
    final String code;

    /**
     * 构造方法
     *
     * @param name 这个编译单元名称
     * @param code 这个编译单元的源代码
     */
    JavaSourceFromString(String name, String code) {
        super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
        this.code = code;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return code;
    }
}

/**
 * <b>描述: </b>这是{@link JavaEval}的辅助类，主要功能是辅助{@link JavaEval}获取当前线程上下文的
 * {@link ClassLoader}的环境变量， {@link JavaEval}
 * 需要用到这些环境变量来编译代码，除此之外该类还提供了针对此环境变量的新的ClassLoader
 * <p/>
 * <b>功能: </b>辅助{@link JavaEval}获取当前线程上下文的 {@link ClassLoader}的环境变量，
 * {@link JavaEval} 需要用到这些环境变量来编译代码，除此之外该类还提供了针对此环境变量的新的ClassLoader
 * <p/>
 * <b>用法: </b>调用{@link ClassPathCache#getClassPathInfo()}即可
 * <p/>
 *
 * @author Lv.Mingwei
 */
class ClassPathCache {

    /**
     * 这个计数器的作用是在创建SavePath时，为每一个ClassLoader创建不同的目录，防止出现同路径同名类的冲突
     */
    private static AtomicInteger counter = new AtomicInteger(0);

    /**
     * 用于缓存已计算出来的ClassLoader的环境变量信息
     */
    private static final Map<ClassLoader, ClassPathInfo> CLASSPATH_MAP = new HashMap<ClassLoader, ClassPathInfo>();

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassPathCache.class);

    static {
        /** 此静态初始化块用于启动时按需清理上一次动态生成的javaeval文件 */
        cleanTmpFolder();
    }

    /**
     * 获得当前线程上下文的环境变量
     *
     * @return
     */
    public static ClassPathInfo getClassPathInfo() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return getClassPathInfo(classLoader);
    }

    /**
     * 获得当前类加载器的环境变量
     *
     * @param classLoader
     * @return
     */
    public static ClassPathInfo getClassPathInfo(ClassLoader classLoader) {
        ClassPathInfo classPathInfo = CLASSPATH_MAP.get(classLoader);
        if (classPathInfo != null) {
            return classPathInfo;
        }
        String savePath = getSavePath();
        String classPath = getClassPathString(classLoader);
        StringBuilder path = new StringBuilder();
        path.append(classPath);
        path.append(System.getProperty("java.class.path")).append(File.pathSeparator);
        // 在全部环境变量后面追加上我们将要自动生成的类路径
        path.append(savePath).append(File.pathSeparator);
        // 追加OSGi容器中其他bundle的path
        if (OsgiEnv.IS_ACTIVE) {
            for (String str : OsgiActivator.getInstance().getClassPath(classLoader)) {
                path.append(str).append(File.pathSeparator);
            }
        }
        // 创建一个针对此currentClassLoader的对应用于序列化的classLoader
        ClassLoader javaEvalClassLoader = null;
        try {
            javaEvalClassLoader = URLClassLoader.newInstance(new URL[]{new File(savePath).toURI().toURL()}, classLoader);
        } catch (MalformedURLException e) {
            throw new RuntimeException("create JavaEvalClassLoader Error,ROOT_CLASS_PATH.toURI failed. ", e);
        }
        classPathInfo = new ClassPathInfo(javaEvalClassLoader, path.toString(), savePath);
        CLASSPATH_MAP.put(classLoader, classPathInfo);
        return classPathInfo;
    }

    /**
     * 释放当前线程上下文的环境变量
     *
     * @return
     */
    public static boolean release() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return release(classLoader);
    }

    /**
     * 释放某一个classLoader的环境变量
     *
     * @param classLoader
     * @return
     */
    public static boolean release(ClassLoader classLoader) {
        CLASSPATH_MAP.remove(classLoader);
        return true;
    }

    /**
     * 获得当前线程上下文的用于JavaEval保存动态生成的class的路径
     *
     * @return
     */
    private static String getSavePath() {
        if (JavaEval.SAVE_CLASS_PATH != null) {
            return JavaEval.SAVE_CLASS_PATH;
        }
        String savePath = null;
        try {
            String folder = "javaeval" + File.separator + "javaeval-" + ServiceEnvironment.getPid() + "-"
                    + counter.incrementAndGet();
            if (OsgiEnv.IS_ACTIVE) {
                savePath = OsgiActivator.getInstance().getContext().getDataFile("").getAbsolutePath();
            } else {
                savePath = URLDecoder.decode(JavaEval.class.getProtectionDomain().getCodeSource().getLocation().getPath(),
                        "UTF-8");
            }
            // 取当前类路径的上一层,并创建目录javaeval,用于在此目录下保存生成的class文件
            File tempFile1 = new File(savePath);
            savePath = tempFile1.getParentFile().getPath();
            savePath += savePath.endsWith("/") || savePath.endsWith("\\") ? folder + File.separator : File.separator
                    + folder + File.separator;
            File tempFile2 = new File(savePath);
            if (!tempFile2.exists()) {
                tempFile2.mkdirs();
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Super Error,get ROOT_CLASS_PATH Error,JavaEval init ROOT_CLASS_PATH failed.", e);
        }
        return savePath;
    }

    /**
     * 用于清理无效的javaeval临时文件
     */
    private static void cleanTmpFolder() {
        try {
            String folder = "javaeval";
            String savePath = URLDecoder.decode(JavaEval.class.getProtectionDomain().getCodeSource().getLocation()
                    .getPath(), "UTF-8");
            // 取当前类路径的上一层,并创建目录javaeval,用于在此目录下保存生成的class文件
            File tempFile1 = new File(savePath);
            File saveFolder = tempFile1.getParentFile();
            savePath = saveFolder.getPath();
            savePath += savePath.endsWith("/") || savePath.endsWith("\\") ? folder + File.separator : File.separator
                    + folder + File.separator;

            File tempFile2 = new File(savePath);
            if (!tempFile2.exists()) {
                return;
            }
            File[] files = tempFile2.listFiles();
            if (files == null || files.length == 0) {
                return;
            }
            Set<Integer> runningJVMPIDSet = getRunningJVMPID();
            for (File file : files) {
                // 遍历缓存路径下的每一个文件，如果该文件为javaeval-pid-xxx的格式，且pid不在系统中存在，则删除
                if (file.isDirectory() && file.getName().startsWith("javaeval-")) {
                    String[] fileNames = file.getName().split("-");
                    if (fileNames != null && fileNames.length == 3
                            && !runningJVMPIDSet.contains(Integer.valueOf(fileNames[1]))) {
                        removeFile(file);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Clean java tmp file failed.", e);
        }
    }

    private static void removeFile(File file) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            // 递归删除目录中的子目录下
            for (File child : children) {
                removeFile(child);
            }
        }
        // 目录此时为空，可以删除
        file.delete();
    }

    /**
     * 用于获取正在运行的JVM的PID
     *
     * @return
     */
    public static Set<Integer> getRunningJVMPID() {
        try {
            // 加入这句没有特殊的含义，首先获取一下平台默认的编译器，目的是以这个渠道加载tools.jar
            ToolProvider.getSystemJavaCompiler();
            String hostname = null;
            HostIdentifier localHostIdentifier = new HostIdentifier(hostname);
            MonitoredHost monitoredHost = MonitoredHost.getMonitoredHost(localHostIdentifier);
            return ((MonitoredHost) monitoredHost).activeVms();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashSet<Integer>();
    }

    /**
     * 获得当前线程上下文的环境变量(字符串标识方式)
     *
     * @param classLoader
     * @return
     */
    private static String getClassPathString(ClassLoader classLoader) {
        StringBuilder path = new StringBuilder();
        if (URLClassLoader.class.isAssignableFrom(classLoader.getClass())) {
            URL[] urls = ((URLClassLoader) classLoader).getURLs();
            if (urls != null) {
                for (URL url : urls) {
                    path.append(url.getPath()).append(File.pathSeparator);
                }
            }
            ClassLoader parentClassLoader = classLoader.getParent();
            if (parentClassLoader != null && URLClassLoader.class.isAssignableFrom(parentClassLoader.getClass())) {
                path.append(getClassPathString(parentClassLoader));
                // 如果父加载器存在了ClassPathInfo对象，那么需要将父的临时class保存路径也纳入到当前的环境变量中
                if (CLASSPATH_MAP.containsKey(parentClassLoader)) {
                    ClassPathInfo parentClassPathInfo = CLASSPATH_MAP.get(parentClassLoader);
                    path.append(parentClassPathInfo.getSavePath()).append(File.pathSeparator);
                }
            } else if (parentClassLoader != null
                    && parentClassLoader.getClass().getName().startsWith("org.apache.tools.ant.loader.AntClassLoader")) {
                try {
                    Method m = parentClassLoader.getClass().getMethod("getClasspath", new Class[]{});
                    path.append(m.invoke(parentClassLoader, new Object[]{})).append(File.pathSeparator);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } else if (classLoader.getClass().getName().startsWith("org.apache.tools.ant.loader.AntClassLoader")) {
            try {
                Method m = classLoader.getClass().getMethod("getClasspath", new Class[]{});
                path.append(m.invoke(classLoader, new Object[]{})).append(File.pathSeparator);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            ClassLoader parentClassLoader = classLoader.getParent();
            if (parentClassLoader != null && URLClassLoader.class.isAssignableFrom(parentClassLoader.getClass())) {
                path.append(getClassPathString(parentClassLoader));
                // 如果父加载器存在了ClassPathInfo对象，那么需要将父的临时class保存路径也纳入到当前的环境变量中
                if (CLASSPATH_MAP.containsKey(parentClassLoader)) {
                    ClassPathInfo parentClassPathInfo = CLASSPATH_MAP.get(parentClassLoader);
                    path.append(parentClassPathInfo.getSavePath()).append(File.pathSeparator);
                }
            } else if (parentClassLoader != null
                    && parentClassLoader.getClass().getName().startsWith("org.apache.tools.ant.loader.AntClassLoader")) {
                try {
                    Method m = parentClassLoader.getClass().getMethod("getClasspath", new Class[]{});
                    path.append(m.invoke(parentClassLoader, new Object[]{})).append(File.pathSeparator);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return path.toString();
    }
}

class ClassPathInfo {

    private ClassLoader classLoader;
    private String classPath;
    private String savePath;

    ClassPathInfo(ClassLoader classLoader, String classPath, String savePath) {
        this.classLoader = classLoader;
        this.classPath = classPath;
        this.savePath = savePath;
    }

    public final ClassLoader getClassLoader() {
        return classLoader;
    }

    public final String getClassPath() {
        return classPath;
    }

    public final String getSavePath() {
        return savePath;
    }

}
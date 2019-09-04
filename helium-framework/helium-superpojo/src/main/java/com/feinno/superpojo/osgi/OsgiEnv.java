package com.feinno.superpojo.osgi;

/**
 * 这个类主要用来跟OSGi环境的接口代码进行隔离，以便于该bundle也可以在非OSGi环境中作为普通Jar包使用
 * Created by Soul on 2015/5/4.
 */
public class OsgiEnv {

    /**
     * 当BundleActivator成功启动时，该标志位会被设置成true，默认或Bundle停止时为false
     */
    public static Boolean IS_ACTIVE = false;

}

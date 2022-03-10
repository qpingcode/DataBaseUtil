package me.qping.utils.dynamicloader;


import sun.misc.Resource;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;

/**
 * 根据properties中配置的路径把jar和配置文件加载到classpath中。<br>
 * 此工具类加载类时使用的是SystemClassLoader，如有需要对加载类进行校验，请另外实现自己的加载器 *
 */
public class DynamicClassLoader extends URLClassLoader{

    private static final String JAR_SUFFIX = ".jar";
    private static final String ZIP_SUFFIX = ".zip";

    public DynamicClassLoader() {
        super(new URL[0]);
    }

    /**
     * 通过filepath加载文件到classpath。
     *
     * @param file 文件路径
     */
    private void addURL(File file) throws Exception {
        this.addURL(file.toURI().toURL());
    }

    public Class<?> findClass(final String name) throws ClassNotFoundException {
       return super.findClass(name);
    }

    /**
     * load Resource by Dir
     *
     * @param file dir
     */
    public void loadResource(File file) throws Exception {
        // 资源文件只加载路径
        if (file.isDirectory()) {
            addURL(file);
            File[] subFiles = file.listFiles();
            if (subFiles != null) {
                for (File tmp : subFiles) {
                    loadResource(tmp);
                }
            }
        }
    }

    /**
     * load Classpath by Dir
     *
     * @param file Dir
     */
    public void loadClasspath(File file) throws Exception {
        if (file.isDirectory()) {
            File[] subFiles = file.listFiles();
            if (subFiles != null) {
                for (File subFile : subFiles) {
                    loadClasspath(subFile);
                }
            }
        } else {
            if (file.getAbsolutePath().endsWith(JAR_SUFFIX) || file.getAbsolutePath().endsWith(ZIP_SUFFIX)) {
                addURL(file);
            }
        }
    }
}
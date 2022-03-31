package me.qping.utils.database.dynamicloader;

import me.qping.utils.database.DataBaseUtilBuilder;
import me.qping.utils.database.connect.DataBaseType;
import me.qping.utils.database.util.MetaDataUtil;
import me.qping.utils.dynamicloader.DynamicClassLoader;
import org.junit.Test;

import java.io.File;

/**
 * @ClassName DynamicClassLoaderTest
 * @Description
 * @Author qping
 * @Date 2022/3/10 09:29
 * @Version 1.0
 **/
public class DynamicClassLoaderTest {

    @Test
    public void testLoad() throws Exception {

        testWithMysql8();
        testWithMysql5();

    }

    public void testWithMysql8() throws Exception {
        String jarPath = "/Users/qping/icode/common-utils-java/DataBaseUtil/lib/mysql/8.0/mysql-connector-java-8.0.13.jar";

        DynamicClassLoader loader = new DynamicClassLoader();
        loader.loadClasspath(new File(jarPath));


        for(int i = 0 ;i < 1; i++){
            Thread.sleep(100);
            DataBaseUtilBuilder builder = DataBaseUtilBuilder.init(
                    DataBaseType.MYSQL,
                    "192.168.1.201",
                    "3306",
                    "11",
                    "root",
                    "11",
                    true,
                    null
            ).setClassLoader(loader);

            MetaDataUtil metaDataUtil = builder.build();
            boolean flag = metaDataUtil.test();
            System.out.println(i + " : " + flag);
        }

        System.out.println("finish");

    }

    public void testWithMysql5() throws Exception {
//        String jarPath = "/Users/qping/icode/common-utils-java/DataBaseUtil/lib/mysql/8.0/mysql-connector-java-8.0.13.jar";
        String jarPath = "/Users/qping/icode/common-utils-java/DataBaseUtil/lib/mysql/5.1/mysql-connector-java-5.1.49.jar";
        DynamicClassLoader loader = new DynamicClassLoader();
        loader.loadClasspath(new File(jarPath));
        DataBaseUtilBuilder builder = DataBaseUtilBuilder.init(
                DataBaseType.MYSQL5,
                "localhost",
                "3309",
                "gjkh",
                "gjkh",
                "gjkh@2022",
                true,
                null
        ).setClassLoader(loader);

        boolean flag2 = builder.build().test();
        System.out.println(flag2);
    }

}

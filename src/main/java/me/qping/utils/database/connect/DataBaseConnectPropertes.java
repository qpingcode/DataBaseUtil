package me.qping.utils.database.connect;

import me.qping.utils.dynamicloader.DynamicClassLoader;

import java.util.Properties;

/**
 * @ClassName DataBaseConnectPropertes
 * @Description 数据库连接串模版
 * @Author qping
 * @Date 2019/6/14 17:15
 * @Version 1.0
 **/
public interface DataBaseConnectPropertes {

    static final String SERVER_TIME_ZONE_GMT8 = "GMT%2B8";              // 使用这个可以防止错误  HOUR_OF_DAY:0->1
    static final String SERVER_TIME_ZONE_SHANGHAI = "Asia/Shanghai";


    String getServerEncoding();
    String getClientEncoding();
    void setEncoding(String serverEncoding, String clientEncoding);


    Properties getConnectionProperties();

    DataBaseType getDataBaseType();
    String getDriver();
    String getUrl();
    String getUsername();
    String getPassword();
    String getValidQuery();
    DynamicClassLoader getClassLoader();

    /**
     * Catalog和Schema都属于抽象概念，主要用来解决命名冲突问题
     * 数据库对象表的全限定名可表示为：Catalog.Schema.表名
     | 供应商        | Catalog支持                       | Schema支持                 |
     | ------------- | --------------------------------- | -------------------------- |
     | Oracle        | 不支持                            | Oracle User ID            |
     | MySQL         | 数据库名                          | 不支持                     |
     | MS SQL Server | 数据库名                          | 对象属主名，2005版开始有变    |
     | DB2           | 指定数据库对象时，Catalog部分省略    | Catalog属主名              |
     * @return
     */
    String getCatalog();
    String getSchema();

    //设置超时
    void setMaxWait(int maxWait);
    //设置时区 mysql 专用
    void setTimezone(String timezone);

    void setClassLoader(DynamicClassLoader classLoader);

    DataBaseDialect getDataBaseDialect();
}

package me.qping.utils.database.connect;

/**
 * @ClassName DataBaseConnectPropertes
 * @Description 数据库连接串模版
 * @Author qping
 * @Date 2019/6/14 17:15
 * @Version 1.0
 **/
public interface DataBaseConnectPropertes {

    public DataBaseType getDataBaseType();
    public String getDriver();
    public String getUrl();
    public String getUsername();
    public String getPassword();
    public String getValidQuery();

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
    public String getCatalog();
    public String getSchema();
}

package me.qping.utils.database.connect.impl;

import lombok.Data;
import me.qping.utils.database.connect.DataBaseConnectPropertes;
import me.qping.utils.database.connect.DataBaseConnectType;

import static me.qping.utils.database.connect.DataBaseConnectType.MSSQL;

/**
 * @ClassName MySQLDataBaseType
 * @Description mysql连接
 * @Author qping
 * @Date 2019/7/4 16:25
 * @Version 1.0
 **/
@Data
public class MSSQLDataBaseType implements DataBaseConnectPropertes {

    public static final String URL = "jdbc:sqlserver://${host}:${port};DatabaseName=${database}";;
    String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    String validQuery = "select 1";

    String host;
    String port;
    String database;
    String username;
    String password;
    String schema;
    String catalog;
    String url;


    /**
     * Catalog和Schema都属于抽象概念，主要用来解决命名冲突问题
     * 数据库对象表的全限定名可表示为：Catalog.Schema.表名
     | 供应商         | Catalog支持                      | Schema支持                             |
     | ------------- | -------------------------------- | ------------------------------------- |
     | MS SQL Server | 数据库名                          | 对象属主名，2005版开始有变，如dbo、sys等   |
     * @return
     */
    public MSSQLDataBaseType(String host, String port, String database, String username, String password) {
        this.host = host;
        this.port = port == null ? "1433" : port;
        this.database = database;
        this.username = username;
        this.password = password;

        this.catalog = database;
    }

    public MSSQLDataBaseType(String host, String port, String database, String username, String password, String schema) {
        this(host, port, database, username, password);
        this.schema = schema;
    }

    public MSSQLDataBaseType(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;

        this.catalog = getCatalogByUrl(url);
    }

    private static String getCatalogByUrl(String url) {
        String markStr = "DatabaseName=";
        int begin = url.indexOf(markStr);
        if( begin > -1){
            return url.substring(begin + markStr.length(), url.length());
        }
        return null;
    }

    @Override
    public DataBaseConnectType getDataBaseType() {
        return MSSQL;
    }

    public String getUrl(){

        if(url != null){
            return url;
        }

        return URL.replaceAll("\\$\\{host\\}", host)
                .replaceAll("\\$\\{port\\}", port)
                .replaceAll("\\$\\{database\\}", database);
    }

    @Override
    public String getCatalog() {
        return this.catalog;
    }

    @Override
    public String getSchema() {
        /**
         * sqlserver 如果当前登录用户为Sue，且不指定scheme，执行 "select * from table_test"
         * 默认的搜索顺序是：
         *      sys.table_test （Sys Schema）
         *      Sue.table_test （Default Schema）
         *      dbo.table_test （Dbo Schema）
         *
         * 在查询数据库表中的数据时，最好指定特定的Schema前缀，
         * 这样数据库就不用去扫描Sys Schema了，就可以提高查询的速度了
         */
        return schema;
    }

}

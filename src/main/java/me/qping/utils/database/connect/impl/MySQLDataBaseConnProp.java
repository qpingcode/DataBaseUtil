package me.qping.utils.database.connect.impl;

import lombok.Data;
import me.qping.utils.database.connect.DataBaseConnectPropertes;
import me.qping.utils.database.connect.DataBaseType;

import static me.qping.utils.database.connect.DataBaseType.MYSQL;

/**
 * @ClassName MySQLDataBaseConnProp
 * @Description mysql连接
 * @Author qping
 * @Date 2019/7/4 16:25
 * @Version 1.0
 **/
@Data
public class MySQLDataBaseConnProp implements DataBaseConnectPropertes {

    public static final String URL = "jdbc:mysql://${host}:${port}/${database}?useUnicode=true&characterEncoding=UTF-8&tinyInt1isBit=false&serverTimezone=Asia/Shanghai&rewriteBatchedStatements=true&autoReconnect=true&failOverReadOnly=false&maxReconnects=2&connectTimeout=${connectTimeout}&socketTimeout=${socketTimeout}";;
    String driver = "com.mysql.cj.jdbc.Driver";
    String validQuery = "select 1 from dual";

    String host;
    String port;
    String database;
    String username;
    String password;

    String schema;
    String catalog;
    String url;

    int socketTimeout = 30000;
    int connectTimeout = 30000;

    /**
     * Catalog和Schema都属于抽象概念，主要用来解决命名冲突问题
     * 数据库对象表的全限定名可表示为：Catalog.Schema.表名
     | 供应商        | Catalog支持                       | Schema支持                 |
     | ------------- | --------------------------------- | -------------------------- |
     | MySQL         | 数据库名                          | 不支持                     |
     * @return
     */
    public MySQLDataBaseConnProp(String host, String port, String database, String username, String password) {
        this.host = host;
        this.port = port == null ? "3306" : port;
        this.database = database;
        this.username = username;
        this.password = password;

        this.catalog = database;
        this.schema = null;
    }

    public MySQLDataBaseConnProp(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;

        this.catalog = this.database = getDatabaseByUrl(url);
        this.schema = null;

    }

    private static String getDatabaseByUrl(String url) {
        String markStr = "/";
        int begin = url.indexOf(markStr, "jdbc:mysql://".length());

        if(begin > -1){
            begin += 1;
            int end = url.indexOf("?", begin) > -1 ? url.indexOf("?", begin) : url.length();
            return url.substring(begin, end);
        }
        return null;
    }

    @Override
    public DataBaseType getDataBaseType() {
        return MYSQL;
    }

    public String getUrl(){

        if(url != null){
            return url;
        }

        return URL.replaceAll("\\$\\{host\\}", host)
                .replaceAll("\\$\\{port\\}", port)
                .replaceAll("\\$\\{database\\}", database == null ? "" : database)
                .replaceAll("\\$\\{socketTimeout\\}", socketTimeout + "")
                .replaceAll("\\$\\{connectTimeout\\}", connectTimeout + "");
    }

    @Override
    public String getCatalog() {
        return this.catalog;
    }

    @Override
    public String getSchema() {
        return this.schema;
    }

    @Override
    public void setMaxWait(int maxWait) {
        this.socketTimeout = maxWait;
        this.connectTimeout = maxWait;
    }

}

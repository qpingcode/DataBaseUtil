package me.qping.utils.database.connect.impl;

import lombok.Data;
import me.qping.utils.database.connect.DataBaseConnectPropertes;
import me.qping.utils.database.connect.DataBaseType;
import me.qping.utils.database.connect.DataBaseDialect;
import me.qping.utils.database.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static me.qping.utils.database.connect.DataBaseType.MYSQL;

/**
 * @ClassName MySQLDataBaseConnProp
 * @Description mysql连接
 * @Author qping
 * @Date 2019/7/4 16:25
 * @Version 1.0
 **/
@Data
public class MySQLDataBaseConnProp extends DataBaseConnAdapter {

    /**
     * zeroDateTimeBehavior  0000-00-00 00:00:00 读取时不报错，转换为null
     */
    public static final String URL = "jdbc:mysql://#{host}:#{port}/#{database}" +
            "?useUnicode=true" +
            "&characterEncoding=UTF-8" +
            "&tinyInt1isBit=false" +
            "&serverTimezone=#{timezone}" +
            "&rewriteBatchedStatements=true" +
            "&autoReconnect=true" +
            "&failOverReadOnly=false" +
            "&maxReconnects=#{maxReconnects}" +
            "&connectTimeout=#{connectTimeout}" +
            "&socketTimeout=#{socketTimeout}" +
            "&zeroDateTimeBehavior=convertToNull";

    String driver = "com.mysql.cj.jdbc.Driver";
    String validQuery = "select 1 from dual";

    int socketTimeout = 30000;
    int connectTimeout = 30000;
    int maxReconnects = 2;
    Boolean useSSL = null;

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
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("socketTimeout", socketTimeout);
        paramsMap.put("connectTimeout", connectTimeout);
        paramsMap.put("timezone", timezone);
        paramsMap.put("maxReconnects", maxReconnects);

        String url = URL;
        if(useSSL != null){
            if(useSSL){
                url += "&useSSL=true";
            }else{
                url += "&useSSL=false";
            }
        }

        return getURL(url, paramsMap);
    }

    @Override
    public void setMaxWait(int maxWait) {
        this.socketTimeout = maxWait;
        this.connectTimeout = maxWait;
    }

    @Override
    public Properties getConnectionProperties() {
        /**
         * mysql  需要 useInformationSchema=true 和 remarks=true
         * oracle 需要 remarks=true
         * 来源： http://www.tinygroup.org/docs/6638819901697136844
         */
        Properties props = new Properties();
        props.setProperty("user", getUsername());
        props.setProperty("password", getPassword());
        props.setProperty("remarks", "true");               //设置可以获取remarks信息
        props.setProperty("useInformationSchema", "true");  //设置可以获取tables remarks信息
        return props;
    }

    @Override
    public DataBaseDialect getDataBaseDialect() {
        return new DataBaseDialect() {
            @Override
            public String getCatalogQuery() {
                return "show databases";
            }

            @Override
            public String getSchemaQuery() {
                return null;
            }

            @Override
            public String getPageSql(String sql, int pageSize, int pageNum) {
                if(pageSize < 0){
                    throw new RuntimeException("pageSize 不能小于 0 ");
                }

                int begin = pageSize * pageNum;
                int end = pageSize * pageNum + pageSize;

                if(pageNum <= 0|| pageSize == 0){
                    return "select * from (\n" + sql + "\n) tmp_0 limit " + pageSize;
                }else{
                    return "select * from (\n" + sql + "\n) tmp_0 limit " + pageSize + " offset " + begin;
                }
            }

            @Override
            public String getTablePageSql(String tableName, int pageSize, int pageNum) {
                if(pageSize < 0){
                    throw new RuntimeException("pageSize 不能小于 0 ");
                }

                int begin = pageSize * pageNum;
                int end = pageSize * pageNum + pageSize;

                if(pageNum <= 0|| pageSize == 0){
                    return "select * from " + tableName + " tmp_0 limit " + pageSize;
                }else{
                    return "select * from " + tableName + " tmp_0 limit " + pageSize + " offset " + begin;
                }
            }
        };
    }

}

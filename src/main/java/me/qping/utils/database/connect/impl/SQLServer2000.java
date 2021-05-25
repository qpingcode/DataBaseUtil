package me.qping.utils.database.connect.impl;

import lombok.Data;
import me.qping.utils.database.connect.DataBaseType;
import me.qping.utils.database.connect.DataBaseDialect;
import me.qping.utils.database.util.ParamsUtil;

import java.util.HashMap;
import java.util.Map;

import static me.qping.utils.database.connect.DataBaseType.SQLSERVER2000;

/**
 * @ClassName SQLServer2000
 * @Description sqlserver 2000 的适配器
 * @Author qping
 * @Date 2021/1/5 12:03
 * @Version 1.0
 **/
@Data
public class SQLServer2000 extends DataBaseConnAdapter {

    public static final String URL = "jdbc:microsoft:sqlserver://#{host}:#{port};DatabaseName=#{database}";
    String driver = "com.microsoft.jdbc.sqlserver.SQLServerDriver";


    public SQLServer2000(String host, String port, String database, String username, String password) {
        this.host = host;
        this.port = port == null ? "1433" : port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.catalog = database;
    }

    public SQLServer2000(String host, String port, String database, String username, String password, String schema) {
        this(host, port, database, username, password);
        this.schema = schema;
    }

    public SQLServer2000(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.catalog = this.database = getCatalogByUrl(url);
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
    public DataBaseType getDataBaseType() {
        return SQLSERVER2000;
    }

    @Override
    public String getDriver() {
        return driver;
    }

    @Override
    public String getUrl() {

        if(url != null){
            return url;
        }

        Map params = new HashMap();
        params.put("host", host);
        params.put("port", port);
        params.put("database", database);

        String url = ParamsUtil.dealParamUnsafe(params, URL, true);
        return url;
    }

    @Override
    public DataBaseDialect getDataBaseDialect() {
        return new DataBaseDialect() {
            @Override
            public String getCatalogQuery() {
                return "select name from master.dbo.SysDatabases where name not in ('master', 'model', 'msdb', 'tempdb')";
            }

            @Override
            public String getSchemaQuery() {
                return "SELECT TABLE_SCHEMA as name FROM INFORMATION_SCHEMA.TABLES  GROUP BY TABLE_SCHEMA";
            }

            @Override
            public String getPageSql(String sql, int pageSize, int pageNum) {

                if(pageSize < 0){
                    throw new RuntimeException("pageSize 不能小于 0 ");
                }

                if(pageNum <= 0 || pageSize == 0){
                    return "select top " + pageSize + " * from (\n" + sql + "\n) tmp_0";
                }else{
                    throw new RuntimeException("SQLServer 2000 未定义分页方法");
                }
            }

            @Override
            public String getTablePageSql(String tableName, int pageSize, int pageNum) {
                if(pageSize < 0){
                    throw new RuntimeException("pageSize 不能小于 0 ");
                }

                if(pageNum <= 0 || pageSize == 0){
                    return "select top " + pageSize + " * from " + tableName ;
                }else{
                    throw new RuntimeException("SQLServer 2000 未定义分页方法");
                }
            }
        };
    }

}

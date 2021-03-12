package me.qping.utils.database.connect.impl;

import lombok.Data;
import me.qping.utils.database.connect.DataBaseType;
import me.qping.utils.database.connect.DataBaseDialect;

import java.util.HashMap;
import java.util.Map;

import static me.qping.utils.database.connect.DataBaseType.MSSQL;

/**
 * @ClassName MSSQLDataBaseConnProp
 * @Description sqlserver
 * @Author qping
 * @Date 2019/7/4 16:25
 * @Version 1.0
 **/
@Data
public class MSSQLDataBaseConnProp extends DataBaseConnAdapter {

    public static final String URL = "jdbc:sqlserver://#{host}:#{port};DatabaseName=#{database}";


    public static final String URL_WITH_INSTANCE = "jdbc:sqlserver://#{host}:#{port};DatabaseName=#{database};instanceName=#{instanceName}";

    String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    String instanceName = null;

    public MSSQLDataBaseConnProp(){

    }

    /**
     * Catalog和Schema都属于抽象概念，主要用来解决命名冲突问题
     * 数据库对象表的全限定名可表示为：Catalog.Schema.表名
     | 供应商         | Catalog支持                      | Schema支持                             |
     | ------------- | -------------------------------- | ------------------------------------- |
     | MS SQL Server | 数据库名                          | 对象属主名，2005版开始有变，如dbo、sys等   |
     * @return
     */
    public MSSQLDataBaseConnProp(String host, String port, String database, String username, String password) {

        if(host.indexOf("\\") > -1 ){
            String[] hostArr = host.split("\\\\");
            if(hostArr.length > 2){
                throw new RuntimeException("host不合法：" + host);
            }
            host = hostArr[0];
            this.instanceName = hostArr[1];
        }

        this.host = host;
        this.port = port == null ? "1433" : port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.catalog = database;
    }

    public MSSQLDataBaseConnProp(String host, String port, String database, String username, String password, String schema) {
        this(host, port, database, username, password);
        this.schema = schema;
    }

    public MSSQLDataBaseConnProp(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;

        this.catalog = this.database = getCatalogByUrl(url);
    }

    private static String getCatalogByUrl(String url) {
        String markStr = "DatabaseName=";
        int begin = url.indexOf(markStr);
        if( begin > -1){
            int end = url.indexOf(";", begin) > -1 ? url.indexOf(";", begin) : url.length();
            return url.substring(begin + markStr.length(), end);
        }
        return null;
    }

    @Override
    public DataBaseType getDataBaseType() {
        return MSSQL;
    }

    public String getUrl(){
        if(instanceName != null){
            Map<String, Object> paramsMap = new HashMap<>();
            paramsMap.put("instanceName", instanceName);
            return getURL(URL_WITH_INSTANCE, paramsMap);
        }else{
            return getURL(URL, null);
        }
    }


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
    @Override
    public String getSchema() {
        return schema;
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
                return "select name from sys.schemas " +
                        " where name not in ('INFORMATION_SCHEMA', 'db_owner', 'db_accessadmin', 'db_backupoperator', 'db_datareader', 'db_datawriter', 'db_ddladmin', 'db_denydatareader', 'db_denydatawriter', 'db_securityadmin', 'sys')";
            }

            @Override
            public String getPageSql(String sql, int pageSize, int pageNum) {
                if(pageSize < 0){
                    throw new RuntimeException("pageSize 不能小于 0 ");
                }


                int begin = pageSize * pageNum;
                int end = pageSize * pageNum + pageSize;

                if(pageNum <= 0 || pageSize == 0){
                    return "select top " + pageSize + " * from (\n" + sql + "\n) tmp_0";
                }else{
                    return "select * from ( " +
                            "   select *, ROW_NUMBER() OVER (ORDER BY (select 0)) AS rn from (\n"+ sql +"\n) tmp_0 " +
                            " ) as tmp_1 where rn > " + begin +" and rn <= " + end;
                }
            }
        };
    }



}

package me.qping.utils.database.crud.impl;

import lombok.Data;
import me.qping.utils.database.crud.DataBaseConnectType;

/**
 * @ClassName MySQLDataBaseType
 * @Description mysql连接
 * @Author qping
 * @Date 2019/7/4 16:25
 * @Version 1.0
 **/
@Data
public class MSSQLDataBaseType implements DataBaseConnectType {

    public static final String URL = "jdbc:sqlserver://${host}:${port};DatabaseName=${database}";;
    String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    String validQuery = "select 1";

    String host;
    String port;
    String database;
    String username;
    String password;
    String schema;


    public MSSQLDataBaseType(String host, String port, String database, String username, String password) {
        this.host = host;
        this.port = port == null ? "1433" : port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public MSSQLDataBaseType(String host, String port, String database, String username, String password, String schema) {
        this(host, port, database, username, password);
        this.schema = schema;
    }

    @Override
    public String getType() {
        return "mssql";
    }

    public String getUrl(){
        return URL.replaceAll("\\$\\{host\\}", host)
                .replaceAll("\\$\\{port\\}", port)
                .replaceAll("\\$\\{database\\}", database);
    }

    @Override
    public String getCatalog() {
        return this.database;
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

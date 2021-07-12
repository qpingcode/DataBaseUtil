package me.qping.utils.database;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.Data;
import me.qping.utils.database.connect.DataBaseConnectPropertes;
import me.qping.utils.database.connect.DataBaseType;
import me.qping.utils.database.connect.impl.*;
import me.qping.utils.database.util.CrudUtil;
import me.qping.utils.database.util.MetaDataUtil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @ClassName DataBaseDialect
 * @Description jdbc 简单封装
 * @Author qping
 * @Date 2019/6/14 15:51
 * @Version 1.0
 **/
@Data
public class DataBaseUtilBuilder {

    DataBaseConnectPropertes dataBaseProperties;

    int initialSize = 1;
    int minIdle = 1;
    int maxActive = 20;
    int maxWait = 60000;
    boolean usePool = false;
    String timezone;

    public DataBaseUtilBuilder setMaxWait(int maxWait) {
        this.maxWait = maxWait;
        return this;
    }

    String key = "";

    public static DataBaseUtilBuilder create() {
        return new DataBaseUtilBuilder();
    }

    public DataBaseUtilBuilder databaseType(DataBaseConnectPropertes dataBaseProperties) {
        this.dataBaseProperties = dataBaseProperties;
        return this;
    }

    public static DataBaseUtilBuilder mysql(String host, String port, String database, String username, String password) {
        MySQLDataBaseConnProp dataBaseProperties = new MySQLDataBaseConnProp(host, port, database, username, password);
        return create().databaseType(dataBaseProperties);
    }

    public static DataBaseUtilBuilder oracle(String host, String port, String serviceName, String username, String password) {
        OracleDataBaseConnProp dataBaseProperties = new OracleDataBaseConnProp(host, port, true, serviceName, username, password);
        return create().databaseType(dataBaseProperties);
    }

    public static DataBaseUtilBuilder oracle(String host, String port, boolean useServiceName, String database, String username, String password) {
        OracleDataBaseConnProp dataBaseProperties = new OracleDataBaseConnProp(host, port, useServiceName, database, username, password);
        return create().databaseType(dataBaseProperties);
    }

    public static DataBaseUtilBuilder mssql(String host, String port, String database, String username, String password) {
        MSSQLDataBaseConnProp dataBaseProperties = new MSSQLDataBaseConnProp(host, port, database, username, password);
        return create().databaseType(dataBaseProperties);
    }

    public static DataBaseUtilBuilder mssql(String host, String port, String database, String username, String password, String schema) {
        MSSQLDataBaseConnProp dataBaseProperties = new MSSQLDataBaseConnProp(host, port, database, username, password, schema);
        return create().databaseType(dataBaseProperties);
    }

    public static DataBaseUtilBuilder postgre(String host, String port, String database, String username, String password) {
        PostgresqlDataBaseConnProp dataBaseProperties = new PostgresqlDataBaseConnProp(host, port, database, username, password);
        return create().databaseType(dataBaseProperties);
    }

    /**
     *
     * @param dataBaseType  数据库类型
     * @param host          数据库地址
     * @param port          数据库端口
     * @param database      数据库名称
     * @param username      用户名
     * @param password      密码
     * @param useServiceName       oracle：是否使用service name  H2(借用为 useMemoryMode) 是否使用内存模式
     * @param schema        sqlserver、hive 可指定schema         H2(借用为 dbPath）指定数据库文件路径
     * @return
     */
    public static DataBaseUtilBuilder init(DataBaseType dataBaseType, String host, String port, String database, String username, String password, boolean useServiceName, String schema) {

        DataBaseConnectPropertes dataBaseProperties = null;
        switch (dataBaseType) {
            case MYSQL:
                dataBaseProperties = new MySQLDataBaseConnProp(host, port, database, username, password);
                break;
            case SQLSERVER2000:
                dataBaseProperties = new SQLServer2000(host, port, database, username, password, schema);
                break;
            case MSSQL:
                dataBaseProperties = new MSSQLDataBaseConnProp(host, port, database, username, password, schema);
                break;
            case ORACLE:
                dataBaseProperties = new OracleDataBaseConnProp(host, port, useServiceName, database, username, password);
                break;
            case POSTGRESQL:
                dataBaseProperties = new PostgresqlDataBaseConnProp(host, port, database, username, password);
                break;
            case INFOSYSCACHE:
                dataBaseProperties = new InfosysCache(host, port, database, username, password);
                break;
            case DB2:
                dataBaseProperties = new DB2ConnProp(host, port, database, username, password);
                break;
            case HIVE:
                dataBaseProperties = new Hive(host, port, database, username, password, schema);
                break;
            case H2:
                // 当host和port传null时，默认为嵌入式（本地）模式
                // schema 此处借用为dbPath，指定数据库文件路径
                // useServiceName 此处借用为 useMemoryMode，是否使用内存模式
                dataBaseProperties = new H2Database(host, port, schema, database, username, password, useServiceName);
                break;
            default:
                throw new RuntimeException("不支持的数据库类型：" + dataBaseType.name());
        }
        return create().databaseType(dataBaseProperties);
    }

    public static DataBaseUtilBuilder init(String url, String username, String password) {
        DataBaseConnectPropertes dataBaseProperties;
        if (url.indexOf("jdbc:hive") > -1) {
            dataBaseProperties = new Hive(url, username, password);
        } else if (url.indexOf("jdbc:sqlserver") > -1) {
            dataBaseProperties = new MSSQLDataBaseConnProp(url, username, password);
        } else if (url.indexOf("jdbc:microsoft:sqlserver") > -1) {
            dataBaseProperties = new SQLServer2000(url, username, password);
        } else if (url.indexOf("mysql") > -1) {
            dataBaseProperties = new MySQLDataBaseConnProp(url, username, password);
        } else if (url.indexOf("oracle") > -1) {
            dataBaseProperties = new OracleDataBaseConnProp(url, username, password);
        } else if (url.indexOf("postgresql") > -1) {
            dataBaseProperties = new PostgresqlDataBaseConnProp(url, username, password);
        } else if (url.indexOf("jdbc:Cache") > -1) {
            dataBaseProperties = new InfosysCache(url, username, password);
        } else if (url.indexOf("jdbc:db2:") > -1) {
            dataBaseProperties = new DB2ConnProp(url, username, password);
        } else if(url.indexOf("jdbc:h2:") > -1){
            dataBaseProperties = new H2Database(url, username, password);
        } else {
            throw new RuntimeException("不支持的数据库类型，无法解析url：" + url);
        }
        return create().databaseType(dataBaseProperties);
    }

    /**
     * 连接池设置
     *
     * @param initialSize
     * @param minIdle
     * @param maxActive
     * @param maxWait
     * @return
     */
    public DataBaseUtilBuilder pool(int initialSize, int minIdle, int maxActive, int maxWait) {
        this.initialSize = initialSize;
        this.minIdle = minIdle;
        this.maxActive = maxActive;
        this.maxWait = maxWait;
        this.usePool = true;
        return this;
    }

    private DataSource createDataSource() {
        DruidDataSource ds = new DruidDataSource();
        ds.setUrl(dataBaseProperties.getUrl());
        ds.setUsername(dataBaseProperties.getUsername());
        ds.setPassword(dataBaseProperties.getPassword());
        ds.setInitialSize(initialSize);
        ds.setMinIdle(minIdle);
        ds.setMaxActive(maxActive);
        ds.setMaxWait(maxWait);
        ds.setTimeBetweenEvictionRunsMillis(60000);
        ds.setMinEvictableIdleTimeMillis(300000);
        ds.setTestWhileIdle(true);
        ds.setTestOnBorrow(false);
        ds.setTestOnReturn(false);
        ds.setPoolPreparedStatements(true);
        ds.setMaxPoolPreparedStatementPerConnectionSize(20);
        ds.setValidationQuery(dataBaseProperties.getValidQuery());
        return ds;
    }

    public Connection createConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(dataBaseProperties.getUrl(), dataBaseProperties.getUsername(), dataBaseProperties.getPassword());
        return connection;
    }

    public CrudUtil buildCrudUtil() throws ClassNotFoundException {
        Class.forName(dataBaseProperties.getDriver());

        CrudUtil crud = new CrudUtil();
        crud.setDataBaseConnectProperties(dataBaseProperties);

        dataBaseProperties.setMaxWait(maxWait);

        if (usePool) {
            crud.setDataSource(createDataSource());
        }

        return crud;
    }

    public MetaDataUtil build() throws ClassNotFoundException {

        Class.forName(dataBaseProperties.getDriver());


        MetaDataUtil metaDataUtil = new MetaDataUtil();
        metaDataUtil.setDataBaseConnectProperties(dataBaseProperties);
        dataBaseProperties.setMaxWait(maxWait);
        dataBaseProperties.setTimezone(timezone);
        if (usePool) {
            metaDataUtil.setDataSource(createDataSource());
        }
        return metaDataUtil;
    }
}

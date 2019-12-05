package me.qping.utils.database;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.Data;
import me.qping.utils.database.connect.DataBaseConnectPropertes;
import me.qping.utils.database.connect.impl.MSSQLDataBaseType;
import me.qping.utils.database.connect.impl.MySQLDataBaseType;
import me.qping.utils.database.connect.impl.OracleDataBaseType;
import me.qping.utils.database.crud.CrudUtil;
import me.qping.utils.database.metadata.MetaDataUtil;
import me.qping.utils.database.metadata.impl.MSSQLMetaData;
import me.qping.utils.database.metadata.impl.MySQLMetaData;
import me.qping.utils.database.metadata.impl.OracleMetaData;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static me.qping.utils.database.connect.DataBaseConnectType.*;

/**
 * @ClassName DataBase
 * @Description jdbc 简单封装
 * @Author qping
 * @Date 2019/6/14 15:51
 * @Version 1.0
 **/
@Data
public class DataBaseUtilBuilder {

    DataBaseConnectPropertes dataBaseType;

    int initialSize = 1;
    int minIdle = 1;
    int maxActive = 20;
    int maxWait = 60000;
    boolean usePool = false;

    public DataBaseUtilBuilder databaseType(DataBaseConnectPropertes dataBaseType){
        this.dataBaseType = dataBaseType;
        return this;
    }

    public DataBaseUtilBuilder mysql(String host, String port, String database, String username, String password){
        this.dataBaseType = new MySQLDataBaseType(host, port, database, username, password);
        return this;
    }

    public DataBaseUtilBuilder oracle(String host, String port, String serviceName, String username, String password){
        this.dataBaseType = new OracleDataBaseType(host, port, true, serviceName, username, password);
        return this;
    }

    public DataBaseUtilBuilder oracle(String host, String port, boolean useServiceName, String database, String username, String password){
        this.dataBaseType = new OracleDataBaseType(host, port, useServiceName, database, username, password);
        return this;
    }

    public DataBaseUtilBuilder mssql(String host, String port, String database, String username, String password){
        this.dataBaseType = new MSSQLDataBaseType(host, port, database, username, password);
        return this;
    }

    public DataBaseUtilBuilder mssql(String host, String port, String database, String username, String password, String schema){
        this.dataBaseType = new MSSQLDataBaseType(host, port, database, username, password, schema);
        return this;
    }

    public DataBaseUtilBuilder smartInit(String url, String username, String password){
        if(url.indexOf("sqlserver") > -1){
            this.dataBaseType = new MSSQLDataBaseType(url, username, password);
        }else if(url.indexOf("mysql") > -1){
            this.dataBaseType = new MySQLDataBaseType(url, username, password);
        }else if(url.indexOf("oracle") > -1){
            this.dataBaseType = new OracleDataBaseType(url, username, password);
        }else{
            throw new RuntimeException("无法解析url");
        }
        return this;
    }

    /**
     * 连接池设置
     * @param initialSize
     * @param minIdle
     * @param maxActive
     * @param maxWait
     * @return
     */
    public DataBaseUtilBuilder pool(int initialSize, int minIdle, int maxActive, int maxWait){
        this.initialSize = initialSize;
        this.minIdle = minIdle;
        this.maxActive = maxActive;
        this.maxWait = maxWait;
        this.usePool = true;
        return this;
    }

    public DataSource createDataSource(){
        DruidDataSource ds = new DruidDataSource();
        ds.setUrl(dataBaseType.getUrl());
        ds.setUsername(dataBaseType.getUsername());
        ds.setPassword(dataBaseType.getPassword());
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
        ds.setValidationQuery(dataBaseType.getValidQuery());
        return ds;
    }

    public Connection createConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(dataBaseType.getUrl(), dataBaseType.getUsername(), dataBaseType.getPassword());
        return connection;
    }

    public CrudUtil buildCrudUtil(){
        try {
            Class.forName(dataBaseType.getDriver());

            CrudUtil crud = new CrudUtil();
            crud.setDataBaseConnectType(dataBaseType);

            if(usePool){
                crud.setDataSource(createDataSource());
            }

            return crud;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public MetaDataUtil buildMetaDataUtil(){
        MetaDataUtil analyze = null;
        if(this.dataBaseType.getDataBaseType().equals(MYSQL)){
            analyze = new MySQLMetaData();
        }

        if(this.dataBaseType.getDataBaseType().equals(ORACLE)){
            analyze = new OracleMetaData();
        }

        if(this.dataBaseType.getDataBaseType().equals(MSSQL)){
            analyze = new MSSQLMetaData();
        }
        return analyze;

    }
}

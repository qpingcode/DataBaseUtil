package me.qping.utils.database.crud;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.Data;
import me.qping.utils.database.crud.impl.MSSQLDataBaseType;
import me.qping.utils.database.crud.impl.MySQLDataBaseType;
import me.qping.utils.database.crud.impl.OracleDataBaseType;

import javax.sql.DataSource;

/**
 * @ClassName DataBase
 * @Description jdbc 简单封装
 * @Author qping
 * @Date 2019/6/14 15:51
 * @Version 1.0
 **/
@Data
public class DataBaseBuilder {

    DataBaseConnectType dataBaseType;

    int initialSize = 5;
    int minIdle = 1;
    int maxActive = 20;
    int maxWait = 60000;

    public DataBaseBuilder databaseType(DataBaseConnectType dataBaseType){
        this.dataBaseType = dataBaseType;
        return this;
    }

    public DataBaseBuilder mysql(String host, String port, String database, String username, String password){
        this.dataBaseType = new MySQLDataBaseType(host, port, database, username, password);
        return this;
    }

    public DataBaseBuilder oracle(String host, String port, String serviceName, String username, String password){
        this.dataBaseType = new OracleDataBaseType(host, port, serviceName, username, password);
        return this;
    }
    public DataBaseBuilder mssql(String host, String port, String database, String username, String password){
        this.dataBaseType = new MSSQLDataBaseType(host, port, database, username, password);
        return this;
    }

    public DataBaseBuilder dev(){
        this.initialSize = 1;
        this.minIdle = 1;
        this.maxActive = 5;
        this.maxWait = 10000;
        return this;
    }

    public DataBaseBuilder initialSize(int initialSize){
        this.initialSize = initialSize;
        return this;
    }

    public DataBaseBuilder minIdle(int minIdle){
        this.minIdle = minIdle;
        return this;
    }

    public DataBaseBuilder maxActive(int maxActive){
        this.maxActive = maxActive;
        return this;
    }

    public DataBaseBuilder maxWait(int maxWait){
        this.maxWait = maxWait;
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

    public DataBase build(){
        try {

            Class.forName(dataBaseType.getDriver());
            DataBase dataBase = new DataBase();
            dataBase.setDataSource(createDataSource());
            return dataBase;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}

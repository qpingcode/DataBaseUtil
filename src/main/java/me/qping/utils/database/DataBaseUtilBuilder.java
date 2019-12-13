package me.qping.utils.database;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.Data;
import me.qping.utils.database.connect.DataBaseConnectPropertes;
import me.qping.utils.database.connect.DataBaseType;
import me.qping.utils.database.connect.impl.MSSQLDataBaseConnProp;
import me.qping.utils.database.connect.impl.MySQLDataBaseConnProp;
import me.qping.utils.database.connect.impl.OracleDataBaseConnProp;
import me.qping.utils.database.database.DataBaseDialect;
import me.qping.utils.database.util.CrudUtil;
import me.qping.utils.database.database.impl.MSSQLDialect;
import me.qping.utils.database.util.MetaDataUtil;
import me.qping.utils.database.database.impl.MySQLDialect;
import me.qping.utils.database.database.impl.OracleDialect;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static me.qping.utils.database.connect.DataBaseType.*;

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

    public static DataBaseUtilBuilder create(){
        return new DataBaseUtilBuilder();
    }

    public DataBaseUtilBuilder databaseType(DataBaseConnectPropertes dataBaseProperties){
        this.dataBaseProperties = dataBaseProperties;
        return this;
    }

    public static DataBaseUtilBuilder mysql(String host, String port, String database, String username, String password){
        MySQLDataBaseConnProp dataBaseProperties = new MySQLDataBaseConnProp(host, port, database, username, password);
        return create().databaseType(dataBaseProperties);
    }

    public static DataBaseUtilBuilder oracle(String host, String port, String serviceName, String username, String password){
        OracleDataBaseConnProp dataBaseProperties = new OracleDataBaseConnProp(host, port, true, serviceName, username, password);
        return create().databaseType(dataBaseProperties);
    }

    public static DataBaseUtilBuilder oracle(String host, String port, boolean useServiceName, String database, String username, String password){
        OracleDataBaseConnProp dataBaseProperties = new OracleDataBaseConnProp(host, port, useServiceName, database, username, password);
        return create().databaseType(dataBaseProperties);
    }

    public static DataBaseUtilBuilder mssql(String host, String port, String database, String username, String password){
        MSSQLDataBaseConnProp dataBaseProperties = new MSSQLDataBaseConnProp(host, port, database, username, password);
        return create().databaseType(dataBaseProperties);
    }

    public static DataBaseUtilBuilder mssql(String host, String port, String database, String username, String password, String schema){
        MSSQLDataBaseConnProp dataBaseProperties = new MSSQLDataBaseConnProp(host, port, database, username, password, schema);
        return create().databaseType(dataBaseProperties);
    }

    public static DataBaseUtilBuilder init(DataBaseType dataBaseType, String host, String port, String database, String username, String password, boolean isOracleServiceId){
        if(dataBaseType.equals(MYSQL)){
            MySQLDataBaseConnProp dataBaseProperties = new MySQLDataBaseConnProp(host, port, database, username, password);
            return create().databaseType(dataBaseProperties);
        }else if(dataBaseType.equals(MSSQL)){
            MSSQLDataBaseConnProp dataBaseProperties = new MSSQLDataBaseConnProp(host, port, database, username, password);
            return create().databaseType(dataBaseProperties);
        }else if(dataBaseType.equals(ORACLE)){
            OracleDataBaseConnProp dataBaseProperties = new OracleDataBaseConnProp(host, port, !isOracleServiceId, database, username, password);
            return create().databaseType(dataBaseProperties);
        }
        return null;
    }

    public static DataBaseUtilBuilder init(String url, String username, String password){
        DataBaseConnectPropertes dataBaseProperties;
        if(url.indexOf("sqlserver") > -1){
            dataBaseProperties = new MSSQLDataBaseConnProp(url, username, password);
        }else if(url.indexOf("mysql") > -1){
            dataBaseProperties = new MySQLDataBaseConnProp(url, username, password);
        }else if(url.indexOf("oracle") > -1){
            dataBaseProperties = new OracleDataBaseConnProp(url, username, password);
        }else{
            throw new RuntimeException("无法解析url");
        }
        return create().databaseType(dataBaseProperties);
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

    private DataSource createDataSource(){
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

    public CrudUtil buildCrudUtil(){
        try {
            Class.forName(dataBaseProperties.getDriver());

            CrudUtil crud = new CrudUtil();
            crud.setDataBaseConnectProperties(dataBaseProperties);

            if(usePool){
                crud.setDataSource(createDataSource());
            }

            return crud;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public MetaDataUtil build(){

        try {
            Class.forName(dataBaseProperties.getDriver());

            DataBaseDialect dataBaseDialect = null;
            if(this.dataBaseProperties.getDataBaseType().equals(MYSQL)){
                dataBaseDialect = new MySQLDialect();
            } else if(this.dataBaseProperties.getDataBaseType().equals(ORACLE)){
                dataBaseDialect = new OracleDialect();
            } else if(this.dataBaseProperties.getDataBaseType().equals(MSSQL)){
                dataBaseDialect = new MSSQLDialect();
            }

            MetaDataUtil metaDataUtil = new MetaDataUtil();
            metaDataUtil.setDataBaseConnectProperties(dataBaseProperties);
            if(usePool){
                metaDataUtil.setDataSource(createDataSource());
            }
            metaDataUtil.setDataBaseDialect(dataBaseDialect);
            return metaDataUtil;

        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }

        return null;
    }
}

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
public class MySQLDataBaseType implements DataBaseConnectType {

    public static final String URL = "jdbc:mysql://${host}:${port}/${database}?useUnicode=true&characterEncoding=UTF-8&tinyInt1isBit=false&serverTimezone=Asia/Shanghai&rewriteBatchedStatements=true";;
    String driver = "com.mysql.cj.jdbc.Driver";
    String validQuery = "select 1 from dual";

    String host;
    String port;
    String database;
    String username;
    String password;


    public MySQLDataBaseType(String host, String port, String database, String username, String password) {
        this.host = host;
        this.port = port == null ? "3306" : port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    @Override
    public String getType() {
        return "mysql";
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
        return null;
    }

}

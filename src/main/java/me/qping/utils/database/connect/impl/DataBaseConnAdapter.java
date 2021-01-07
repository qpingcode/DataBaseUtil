package me.qping.utils.database.connect.impl;

import lombok.Data;
import me.qping.utils.database.connect.DataBaseConnectPropertes;
import me.qping.utils.database.connect.DataBaseType;

/**
 * @ClassName DataBaseConnAdapter
 * @Description 适配器，提取一些通用方法
 * @Author qping
 * @Date 2021/1/7 10:05
 * @Version 1.0
 **/
@Data
public abstract class DataBaseConnAdapter implements DataBaseConnectPropertes {

    String validQuery = "select 1";
    String catalog;
    String schema;
    String host;
    String port;
    String database;
    String username;
    String password;
    String url;
    int maxWait;


    public DataBaseConnAdapter(){}

    public DataBaseConnAdapter(String url, String username, String password){
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public DataBaseConnAdapter(String host, String port, String database, String username, String password){
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }
}

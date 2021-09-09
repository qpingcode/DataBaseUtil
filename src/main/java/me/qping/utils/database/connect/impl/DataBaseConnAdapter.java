package me.qping.utils.database.connect.impl;

import lombok.Data;
import me.qping.utils.database.connect.DataBaseConnectPropertes;
import me.qping.utils.database.connect.DataBaseDialect;
import me.qping.utils.database.util.ParamsUtil;
import me.qping.utils.database.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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
    String timezone;
    int maxWait;

    String serverEncoding = null;  // ISO8859-1
    String clientEncoding = null;  // GBK

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

    protected String getURL(String template, Map map){

        if(url != null){
            return url;
        }

        Map params = new HashMap();
        params.put("host", host);
        params.put("port", port);
        params.put("database", database);
        if(map != null){
            params.putAll(map);
        }

        if(template == null){
            return null;
        }

        String url = ParamsUtil.dealParamUnsafe(params, template, true);
        return url;
    }

    @Override
    public void setTimezone(String timezone) {
        if(StringUtils.isNotBlank(timezone)){
            this.timezone = timezone;
        }else{
            this.timezone = DataBaseConnectPropertes.SERVER_TIME_ZONE_SHANGHAI;
        }
    }

    public Properties getConnectionProperties(){
        Properties props = new Properties();

        if(getUsername() != null){
            props.setProperty("user", getUsername());
        }

        if(getPassword() != null){
            props.setProperty("password", getPassword());
        }
        return props;
    }

    @Override
    public DataBaseDialect getDataBaseDialect() {
        return new DataBaseDialect() {
            @Override
            public String getCatalogQuery() {
                return null;
            }

            @Override
            public String getSchemaQuery() {
                return null;
            }

            @Override
            public String getPageSql(String sql, int pageSize, int pageNum) {
                return null;
            }

            @Override
            public String getTablePageSql(String tableName, int pageSize, int pageNum) {
                return null;
            }
        };
    }

    public void setEncoding(String serverEncoding, String clientEncoding){
        this.clientEncoding = clientEncoding;
        this.serverEncoding = serverEncoding;
    }

    public String getServerEncoding(){
        return serverEncoding;
    }
    public String getClientEncoding(){
        return clientEncoding;
    }

}

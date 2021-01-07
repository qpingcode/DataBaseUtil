package me.qping.utils.database.connect.impl;

import lombok.Data;
import me.qping.utils.database.connect.DataBaseConnectPropertes;
import me.qping.utils.database.connect.DataBaseType;
import me.qping.utils.database.util.ParamsUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName InfosysCache
 * @Description TODO
 * @Author qping
 * @Date 2021/1/7 10:01
 * @Version 1.0
 **/
@Data
public class InfosysCache extends DataBaseConnAdapter {

    public static final String URL = "jdbc:Cache://#{host}:#{port}/#{database}";

    public InfosysCache(String url, String username, String password) {
        super(url, username, password);
        this.catalog = this.database = getDatabaseByUrl(url);
    }

    public InfosysCache(String host, String port, String database, String username, String password) {
        super(host, port, database, username, password);
        this.catalog = database;
    }

    private static String getDatabaseByUrl(String url) {
        String markStr = "/";
        int begin = url.indexOf(markStr, "jdbc:Cache://".length());

        if(begin > -1){
            begin += 1;
            int end = url.indexOf("?", begin) > -1 ? url.indexOf("?", begin) : url.length();
            return url.substring(begin, end);
        }
        return null;
    }


    @Override
    public DataBaseType getDataBaseType() {
        return DataBaseType.INFOSYSCACHE;
    }

    @Override
    public String getDriver() {
        return "com.intersys.jdbc.CacheDriver";
    }

    @Override
    public String getValidQuery() {
        return "select 1";
    }

    @Override
    public String getUrl() {

        Map params = new HashMap();
        params.put("host", host);
        params.put("port", port);
        params.put("database", database);

        String url = ParamsUtil.dealParamUnsafe(params, URL, true);
        return url;
    }

}

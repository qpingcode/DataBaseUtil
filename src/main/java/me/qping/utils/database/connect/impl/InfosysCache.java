package me.qping.utils.database.connect.impl;

import lombok.Data;
import me.qping.utils.database.connect.DataBaseType;
import me.qping.utils.database.connect.DataBaseDialect;

/**
 * @ClassName InfosysCache
 * @Description infosys cache 支持
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
        return getURL(URL, null);
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
                return " SELECT SCHEMA_NAME as NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE NOT SCHEMA_NAME %STARTSWITH '%' ";
            }

            @Override
            public String getPageSql(String sql, int pageSize, int pageNum) {
                if(pageSize < 0){
                    throw new RuntimeException("pageSize 不能小于 0 ");
                }

                if(pageNum <= 0 || pageSize == 0){
                    return "select top " + pageSize + " * from (\n" + sql + "\n) tmp_0";
                }else{
                    throw new RuntimeException("InfosysCache 未实现方法");
                }
            }

            @Override
            public String getTablePageSql(String tableName, int pageSize, int pageNum) {
                if(pageSize < 0){
                    throw new RuntimeException("pageSize 不能小于 0 ");
                }

                if(pageNum <= 0 || pageSize == 0){
                    return "select top " + pageSize + " * from " + tableName ;
                }else{
                    throw new RuntimeException("InfosysCache 未实现方法");
                }
            }
        };
    }

}

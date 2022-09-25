package me.qping.utils.database.connect.impl;

import me.qping.utils.database.connect.DataBaseDialect;
import me.qping.utils.database.connect.DataBaseType;

public class Dameng extends DataBaseConnAdapter{

    public static final String URL = "jdbc:dm://#{host}:#{port}";
    public Dameng(){

    }
    public Dameng(String url, String username, String password) {
        super(url, username, password);
    }

    public Dameng(String host, String port, String username, String password) {
        super(host, port, null, username, password);
    }

    @Override
    public DataBaseType getDataBaseType() {
        return DataBaseType.DAMENG;
    }

    @Override
    public String getDriver() {
        return "dm.jdbc.driver.DmDriver";
    }

    @Override
    public String getUrl() {
        return getURL(URL, null);
    }

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
                if(pageSize < 0){
                    throw new RuntimeException("pageSize 不能小于 0 ");
                }

                int begin = pageSize * pageNum;
                int end = pageSize * pageNum + pageSize;

                if(pageNum <= 0|| pageSize == 0){
                    return "select * from (\n" + sql + "\n) tmp_0 limit " + pageSize;
                }else{
                    return "select * from (\n" + sql + "\n) tmp_0 limit " + pageSize + " offset " + begin;
                }
            }

            @Override
            public String getTablePageSql(String tableName, int pageSize, int pageNum) {
                if(pageSize < 0){
                    throw new RuntimeException("pageSize 不能小于 0 ");
                }

                int begin = pageSize * pageNum;
                int end = pageSize * pageNum + pageSize;

                if(pageNum <= 0|| pageSize == 0){
                    return "select * from " + tableName + " tmp_0 limit " + pageSize;
                }else{
                    return "select * from " + tableName + " tmp_0 limit " + pageSize + " offset " + begin;
                }
            }
        };
    }
}

package me.qping.utils.database.connect.impl;

import me.qping.utils.database.connect.DataBaseType;
import me.qping.utils.database.connect.DataBaseDialect;

/**
 * @ClassName DB2
 * @Description TODO
 * @Author qping
 * @Date 2021/2/20 17:07
 * @Version 1.0
 **/
public class DB2ConnProp extends DataBaseConnAdapter {

    public static final String URL = "jdbc:db2://#{host}:#{port}/#{database}";

    public static final String URL_WITH_SCHEMA = "jdbc:db2://#{host}:#{port}/#{database}:currentSchema=#{schema};";


    public DB2ConnProp(String url, String username, String password) {
        super(url, username, password);
    }

    public DB2ConnProp(String host, String port, String database, String username, String password) {
        super(host, port, database, username, password);
    }

    @Override
    public DataBaseType getDataBaseType() {
        return DataBaseType.DB2;
    }

    @Override
    public String getDriver() {
        return "com.ibm.db2.jcc.DB2Driver";
    }

    @Override
    public String getValidQuery() {
        return "select 1 from SYSIBM.SYSDUMMY1";
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
                return "select schemaname as name from syscat.schemata";
            }

            @Override
            public String getPageSql(String sql, int pageSize, int pageNum) {
                if(pageSize < 0){
                    throw new RuntimeException("pageSize 不能小于 0 ");
                }


                int begin = pageSize * pageNum;
                int end = pageSize * pageNum + pageSize;

                if(pageNum <= 0 || pageSize == 0){
                    return String.format("select * from (%s) fetch first %s rows only", sql, pageSize);
                }else{
                    return "select * from ( " +
                            "   select *, ROW_NUMBER() OVER () AS rn from (\n"+ sql +"\n) " +
                            " ) where rn > " + begin +" and rn <= " + end;
                }
            }
        };
    }

    @Override
    public String getUrl() {
        return getURL(URL, null);
    }

}

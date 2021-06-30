package me.qping.utils.database.connect.impl;

import me.qping.utils.database.connect.DataBaseDialect;
import me.qping.utils.database.connect.DataBaseType;

/**
 * @ClassName Hive
 * @Description TODO
 * @Author qping
 * @Date 2021/6/30 16:00
 * @Version 1.0
 **/
public class Hive extends DataBaseConnAdapter {

    public static final String URL = "jdbc:hive2://#{host}:#{port}/#{database}";

    public Hive(String url, String username, String password){
        super(url, username, password);
    }

    public Hive(String host, String port, String database, String username, String password, String schema) {
        super(host, port, database, username, password);
        this.schema = schema;
    }

    @Override
    public DataBaseType getDataBaseType() {
        return DataBaseType.HIVE;
    }

    @Override
    public String getDriver() {
        return "org.apache.hive.jdbc.HiveDriver";
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
                return "show databases";
            }

            @Override
            public String getPageSql(String sql, int pageSize, int pageNum) {
                return getTablePageSql("(\n" + sql + "\n)", pageSize, pageNum);
            }

            @Override
            public String getTablePageSql(String tableName, int pageSize, int pageNum) {
                if(pageSize < 0){
                    throw new RuntimeException("pageSize 不能小于 0 ");
                }

                int begin = pageSize * pageNum;
                int end = pageSize * pageNum + pageSize;

//                return "select * from ( " +
//                        "   select *, ROW_NUMBER() OVER (ORDER BY (select 0)) AS rn from " + tableName +" tmp_0 " +
//                        " ) as tmp_1 where rn > " + begin +" and rn <= " + end;

                return "select * from " + tableName + " tmp_0 limit " + begin + "," + pageSize;
            }
        };
    }


}

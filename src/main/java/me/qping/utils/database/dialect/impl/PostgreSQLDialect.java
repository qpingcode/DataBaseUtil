package me.qping.utils.database.dialect.impl;

import me.qping.utils.database.connect.DataBaseConnectPropertes;
import me.qping.utils.database.dialect.DataBaseDialect;
import me.qping.utils.database.metadata.bean.FieldType;

import java.sql.JDBCType;
import java.util.Properties;

/**
 * @ClassName MSSQLDialect
 * @Author qping
 * @Date 2019/8/3 22:07
 * @Version 1.0
 **/
public class PostgreSQLDialect implements DataBaseDialect {


    @Override
    public String getCatalogQuery() {
        return "SELECT datname as name FROM pg_database where datname not in ('postgres','template1','template0')";
    }

    @Override
    public String getSchemaQuery() {
        return "select nspname as name from pg_namespace where nspname not in ('pg_toast','pg_temp_1','pg_toast_temp_1','pg_catalog', 'information_schema')\n";
    }

    @Override
    public String getPageSql(String sql, int pageSize, int pageNum) {

        if(pageSize < 0){
            throw new RuntimeException("pageSize 不能小于 0 ");
        }

        int begin = pageSize * pageNum;
        int end = pageSize * pageNum + pageSize;

        if(pageNum <= 0 || pageSize == 0){
            return "select  * from (\n" + sql + "\n) tmp_0 limit " + pageSize;
        }else{
            return "select * from (\n" + sql + "\n) tmp_0 limit " + pageSize + " offset " + begin;
        }
    }

    /**
     * sqlserver拿不到注释
     * @param connectType
     * @return
     */
    @Override
    public Properties getConnectionProperties(DataBaseConnectPropertes connectType) {
        Properties props = new Properties();
        props.setProperty("user", connectType.getUsername());
        props.setProperty("password", connectType.getPassword());
        return props;
    }

}

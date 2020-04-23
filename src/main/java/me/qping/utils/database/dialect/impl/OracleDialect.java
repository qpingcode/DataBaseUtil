package me.qping.utils.database.dialect.impl;

import me.qping.utils.database.connect.DataBaseConnectPropertes;
import me.qping.utils.database.dialect.DataBaseDialect;
import me.qping.utils.database.metadata.bean.FieldType;

import java.sql.*;
import java.util.*;

/**
 * @ClassName OracleDialect
 * @Author qping
 * @Date 2019/8/3 22:07
 * @Version 1.0
 **/
public class OracleDialect implements DataBaseDialect {

    @Override
    public String getCatalogQuery() {
        return null;
    }

    @Override
    public String getSchemaQuery() {
        return "select username from all_users order by username";
    } // oracle schema 等同于 user

    @Override
    public String getTopNSql(String tableName, int rowCount) {
        return "select * from " + tableName + " where rownum < " + rowCount;
    }

    @Override
    public Properties getConnectionProperties(DataBaseConnectPropertes connectType) {
        Properties props = new Properties();
        props.setProperty("user", connectType.getUsername());
        props.setProperty("password", connectType.getPassword());
        props.setProperty("remarks", "true");               //设置可以获取remarks信息
        return props;
    }

    /**
     * 设置字段类型 Oracle 数据类型
     * 来源： https://docs.oracle.com/cd/E11882_01/java.112/e16548/datacc.htm#JJDBC28370
     * @param origin
     *            列类型字符串
     * @return
     */
    public FieldType getFieldType(String origin) {

        // 参见： OracleType
        // todo BLOB CLOB NCLOB
        String columnType = origin.toLowerCase();

        if(columnType.startsWith("char")
                || columnType.startsWith("varchar2")
                || columnType.startsWith("nchar")
                || columnType.startsWith("tinytext")
                || columnType.startsWith("text")
                || columnType.startsWith("mediumtext")
                || columnType.startsWith("longtext")
                ){
            return FieldType.of(false, "java.lang.String", null, "String", JDBCType.VARCHAR, origin, null);
        }


        if(columnType.startsWith("long")){
            return FieldType.of(false, "java.lang.Long", null, "Long", JDBCType.INTEGER, origin, null);
        }

        if(columnType.startsWith("number")){
            // number 根据位数不同还可以转换为 boolean byte short int long float double
            return FieldType.of(false, "java.math.BigDecimal", "java.math.BigDecimal", "BigDecimal", JDBCType.DECIMAL, origin, null);
        }

        if(columnType.startsWith("raw")
                || columnType.startsWith("longraw")
                || columnType.startsWith("blob")
                || columnType.startsWith("Clob")
                ){
            return FieldType.of(false, "byte", null, "byte[]", JDBCType.BINARY, origin, null);
        }


        if(columnType.startsWith("date")
                || columnType.startsWith("timestamp")
                || columnType.startsWith("timestamp with time zone")
                || columnType.startsWith("timestamp with local time zon")
                ){
            return FieldType.of(true, "java.util.Date", "java.util.Date", "Date", JDBCType.DATE, origin, null);
        }

        if(columnType.startsWith("binary_float")){
            return FieldType.of(false, "java.lang.Float", null, "Float", JDBCType.FLOAT, origin, null);
        }

        if(columnType.startsWith("binary_double")){
            return FieldType.of(false, "java.lang.Double", null, "Double", JDBCType.DOUBLE, origin, null);
        }

        return FieldType.error(origin);
    }
}

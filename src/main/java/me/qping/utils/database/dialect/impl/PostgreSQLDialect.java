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
        int begin = pageSize * pageNum;
        int end = pageSize * pageNum + pageSize;

        if(pageNum <= 0 || pageSize <= 0){
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

    /**
     * 设置字段类型 MSSQL 数据类型
     * 来源： https://docs.microsoft.com/zh-cn/sql/connect/jdbc/using-basic-data-types?view=sql-server-2017
     * @param origin
     *            列类型字符串
     * @return
     */
    public FieldType getFieldType(String origin) {
        String columnType = origin.toLowerCase();

        FieldType fieldType = new FieldType();

        // 参见： mssql-jdbc-jre8.jar  ===>  JDBCType.class
        if(columnType.startsWith("bigint")){
            return FieldType.of(false, "java.lang.Long", null, "Long", JDBCType.BIGINT, origin, null);
        }

        if(columnType.startsWith("binary")
                || columnType.startsWith("varbinary")
                || columnType.startsWith("image")
                || columnType.startsWith("geometry")
                || columnType.startsWith("geography")
                || columnType.startsWith("varbinary")
                || columnType.startsWith("udt")
                ){
            return FieldType.of(false, "byte", null, "byte[]", JDBCType.LONGVARBINARY, origin, null);
        }

        if(columnType.startsWith("bit")){
            return FieldType.of(false, "java.lang.Boolean", null, "Boolean", JDBCType.BIT, origin, null);
        }

        if(columnType.startsWith("char")
                || columnType.startsWith("nchar")
                || columnType.startsWith("ntext")
                || columnType.startsWith("nvarchar")
                || columnType.startsWith("varchar")
                || columnType.startsWith("text")
                || columnType.startsWith("xml")
                || columnType.startsWith("uniqueidentifier")
                ){
            return FieldType.of(false, "java.lang.String", null, "String", JDBCType.VARCHAR, origin, null);
        }

        if(columnType.startsWith("timestamp")
                || columnType.startsWith("date")
                || columnType.startsWith("datetime")
                || columnType.startsWith("datetime2")
                || columnType.startsWith("smalldatetime")
                || columnType.startsWith("time")
                ){
            return FieldType.of(true, "java.util.Date", "java.util.Date", "Date", JDBCType.DATE, origin, null);
        }

        if(columnType.startsWith("real")){
            return FieldType.of(false, "java.lang.Float", null, "Float", JDBCType.REAL, origin, null);
        }


        if(columnType.startsWith("float")){
            return FieldType.of(false, "java.lang.Double", null, "Double", JDBCType.DOUBLE, origin, null);
        }

        if(columnType.startsWith("int")){
            return FieldType.of(false, "java.lang.Integer", null, "Integer", JDBCType.INTEGER, origin, null);
        }

        if(columnType.startsWith("money")
                || columnType.startsWith("smallmoney")
                ){
            return FieldType.of(false, "java.math.BigDecimal", "java.math.BigDecimal", "BigDecimal", JDBCType.DECIMAL, origin, null);
        }

        if(columnType.startsWith("numeric")
                ){
            return FieldType.of(false, "java.math.BigDecimal", "java.math.BigDecimal", "BigDecimal", JDBCType.NUMERIC, origin, null);
        }


        if(columnType.startsWith("smallint")
                || columnType.startsWith("tinyint")
                ){
            return FieldType.of(false, "java.lang.Short", null, "Short", JDBCType.SMALLINT, origin, null);
        }

        return FieldType.error(origin);
    }
}

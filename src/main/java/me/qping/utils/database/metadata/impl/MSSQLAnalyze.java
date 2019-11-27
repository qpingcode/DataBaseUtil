package me.qping.utils.database.metadata.impl;

import me.qping.utils.database.crud.DataBaseConnectType;
import me.qping.utils.database.metadata.Analyze;
import me.qping.utils.database.metadata.FieldType;

import java.sql.JDBCType;
import java.util.Properties;

/**
 * @ClassName MSSQLAnalyze
 * @Author qping
 * @Date 2019/8/3 22:07
 * @Version 1.0
 **/
public class MSSQLAnalyze extends Analyze {


    /**
     * sqlserver拿不到注释
     * @param connectType
     * @return
     */
    @Override
    public Properties getConnectionProperties(DataBaseConnectType connectType) {
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
            return FieldType.of(false, null, "Long", JDBCType.BIGINT, origin);
        }

        if(columnType.startsWith("binary")
                || columnType.startsWith("varbinary")
                || columnType.startsWith("image")
                || columnType.startsWith("geometry")
                || columnType.startsWith("geography")
                || columnType.startsWith("varbinary")
                || columnType.startsWith("udt")
                ){
            return FieldType.of(false, null, "byte[]", JDBCType.LONGVARBINARY, origin);
        }

        if(columnType.startsWith("bit")){
            return FieldType.of(false, null, "Boolean", JDBCType.BIT, origin);
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
            return FieldType.of(false, null, "String", JDBCType.VARCHAR, origin);
        }

        if(columnType.startsWith("timestamp")
                || columnType.startsWith("date")
                || columnType.startsWith("datetime")
                || columnType.startsWith("datetime2")
                || columnType.startsWith("smalldatetime")
                || columnType.startsWith("time")
                ){
            return FieldType.of(true, "import java.util.Date;", "Date", JDBCType.DATE, origin);
        }

        if(columnType.startsWith("real")){
            return FieldType.of(false, null, "Float", JDBCType.REAL, origin);
        }


        if(columnType.startsWith("float")){
            return FieldType.of(false, null, "Double", JDBCType.DOUBLE, origin);
        }

        if(columnType.startsWith("int")){
            return FieldType.of(false, null, "Integer", JDBCType.INTEGER, origin);
        }

        if(columnType.startsWith("money")
                || columnType.startsWith("smallmoney")
                ){
            return FieldType.of(false, "import java.math.BigDecimal;", "BigDecimal", JDBCType.DECIMAL, origin);
        }

        if(columnType.startsWith("numeric")
                ){
            return FieldType.of(false, "import java.math.BigDecimal;", "BigDecimal", JDBCType.NUMERIC, origin);
        }


        if(columnType.startsWith("smallint")
                || columnType.startsWith("tinyint")
                ){
            return FieldType.of(false, null, "Short", JDBCType.SMALLINT, origin);
        }

        return FieldType.error(origin);
    }
}

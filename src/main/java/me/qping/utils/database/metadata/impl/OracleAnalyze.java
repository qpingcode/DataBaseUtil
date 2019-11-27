package me.qping.utils.database.metadata.impl;

import me.qping.utils.database.crud.DataBaseConnectType;
import me.qping.utils.database.metadata.Analyze;
import me.qping.utils.database.metadata.FieldType;
import me.qping.utils.database.metadata.bean.ColumnMeta;
import me.qping.utils.database.metadata.bean.PrimaryKeyMeta;
import me.qping.utils.database.metadata.bean.TableMeta;

import java.sql.*;
import java.util.*;

/**
 * @ClassName OracleAnalyze
 * @Author qping
 * @Date 2019/8/3 22:07
 * @Version 1.0
 **/
public class OracleAnalyze extends Analyze {

    @Override
    public Properties getConnectionProperties(DataBaseConnectType connectType) {
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
            return FieldType.of(false, null, "String", JDBCType.VARCHAR, origin);
        }


        if(columnType.startsWith("long")){
            return FieldType.of(false, null, "Long", JDBCType.INTEGER, origin);
        }

        if(columnType.startsWith("number")){
            // number 根据位数不同还可以转换为 boolean byte short int long float double
            return FieldType.of(false, "import java.math.BigDecimal;", "BigDecimal", JDBCType.DECIMAL, origin);
        }

        if(columnType.startsWith("raw")
                || columnType.startsWith("longraw")
                || columnType.startsWith("blob")
                || columnType.startsWith("Clob")
                ){
            return FieldType.of(false, null, "byte[]", JDBCType.BINARY, origin);
        }


        if(columnType.startsWith("date")
                || columnType.startsWith("timestamp")
                || columnType.startsWith("timestamp with time zone")
                || columnType.startsWith("timestamp with local time zon")
                ){
            return FieldType.of(true, "import java.util.Date;", "Date", JDBCType.DATE, origin);
        }

        if(columnType.startsWith("binary_float")){
            return FieldType.of(false, null, "Float", JDBCType.FLOAT, origin);
        }

        if(columnType.startsWith("binary_double")){
            return FieldType.of(false, null, "Double", JDBCType.DOUBLE, origin);
        }

        return FieldType.error(origin);
    }
}

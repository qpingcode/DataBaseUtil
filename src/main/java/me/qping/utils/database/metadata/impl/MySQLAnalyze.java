package me.qping.utils.database.metadata.impl;

import me.qping.utils.database.crud.DataBaseConnectType;
import me.qping.utils.database.crud.impl.MySQLDataBaseType;
import me.qping.utils.database.metadata.Analyze;
import me.qping.utils.database.metadata.bean.ColumnMeta;
import me.qping.utils.database.metadata.bean.PrimaryKeyMeta;
import me.qping.utils.database.metadata.bean.TableMeta;

import java.sql.*;
import java.util.*;

/**
 * @ClassName MySQLAnalyze
 * @Author qping
 * @Date 2019/8/3 22:07
 * @Version 1.0
 **/
public class MySQLAnalyze extends Analyze {

    @Override
    public Properties getConnectionProperties(DataBaseConnectType connectType) {
        /**
         * mysql  需要 useInformationSchema=true 和 remarks=true
         * oracle 需要 remarks=true
         * 来源： http://www.tinygroup.org/docs/6638819901697136844
         */
        Properties props = new Properties();
        props.setProperty("user", connectType.getUsername());
        props.setProperty("password", connectType.getPassword());
        props.setProperty("remarks", "true");               //设置可以获取remarks信息
        props.setProperty("useInformationSchema", "true");  //设置可以获取tables remarks信息
        return props;
    }

    /**
     * 设置字段类型 MySql数据类型
     * 来源：https://dev.mysql.com/doc/connector-j/5.1/en/connector-j-reference-type-conversions.html
     * @param columnType
     *            列类型字符串
     * @param packageName
     *            封装包信息
     * @return
     */
    public String getFieldType(String columnType, StringBuffer packageName) {
        columnType = columnType.toLowerCase();
        if (columnType.equals("varchar") || columnType.equals("nvarchar")
                || columnType.equals("char")
                || columnType.equals("tinytext")
                || columnType.equals("text")
                || columnType.equals("mediumtext")
                || columnType.equals("longtext")
                ) {
            return "String";
        } else if (columnType.equals("tinyblob")
                ||columnType.equals("blob")
                ||columnType.equals("mediumblob")
                ||columnType.equals("longblob")) {
            return "byte[]";
        } else if (columnType.equals("datetime")
                ||columnType.equals("date")
                ||columnType.equals("timestamp")
                ||columnType.equals("time")
                ||columnType.equals("year")) {
            packageName.append("import java.util.Date;");
            return "Date";
        } else if (columnType.equals("bit")
                ||columnType.equals("int")
                ||columnType.equals("tinyint")
                ||columnType.equals("smallint")
                ||columnType.equals("bool")
                ||columnType.equals("mediumint")
                ||columnType.equals("bigint")
                ) {
            return "Integer";
        } else if (columnType.equals("float")) {
            return "Float";
        } else if (columnType.equals("double")) {
            return "Double";
        } else if (columnType.equals("decimal")) {
            packageName.append("import java.math.BigDecimal;");
            return "BigDecimal";
        }
        return "ErrorType";
    }
}

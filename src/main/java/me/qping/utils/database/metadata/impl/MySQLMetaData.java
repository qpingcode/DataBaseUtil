package me.qping.utils.database.metadata.impl;

import me.qping.utils.database.connect.DataBaseConnectPropertes;
import me.qping.utils.database.metadata.MetaDataUtil;
import me.qping.utils.database.metadata.bean.FieldType;

import java.sql.*;
import java.util.*;

/**
 * @ClassName MySQLMetaData
 * @Author qping
 * @Date 2019/8/3 22:07
 * @Version 1.0
 **/
public class MySQLMetaData extends MetaDataUtil {

    @Override
    public String getCatalogQuery() {
        return "show databases";
    }

    @Override
    public String getSchemaQuery() {
        return null;
    }

    @Override
    public Properties getConnectionProperties(DataBaseConnectPropertes connectType) {
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
     * @param origin
     *            列类型字符串
     * @return
     */
    public FieldType getFieldType(String origin) {
        String columnType = getColumnType(origin.toLowerCase());

        if(columnType.equals("varchar")
                || columnType.equals("nvarchar")
                || columnType.equals("char")
                || columnType.equals("tinytext")
                || columnType.equals("text")
                || columnType.equals("mediumtext")
                || columnType.equals("longtext")
                ){
            String columnDefine = columnType.equals("varchar") ? null : columnType;
            return FieldType.of(false, null, "String", JDBCType.VARCHAR, origin, columnDefine);
        }

        if(columnType.equals("tinyblob")
                || columnType.equals("blob")
                || columnType.equals("mediumblob")
                || columnType.equals("longblob")
                ){
            return FieldType.of(false, null, "byte[]", JDBCType.BINARY, origin, null);
        }

        if(columnType.equals("datetime")
                || columnType.equals("date")
                || columnType.equals("timestamp")
                || columnType.equals("time")
                || columnType.equals("year")
                ){
            return FieldType.of(true, "java.util.Date", "Date", JDBCType.DATE, origin, null);
        }

        if(columnType.equals("bit")
                || columnType.equals("int")
                || columnType.equals("tinyint")
                || columnType.equals("smallint")
                || columnType.equals("bool")
                || columnType.equals("mediumint")
                ){
            return FieldType.of(false, null, "Integer", JDBCType.INTEGER, origin, null);
        }

        if(columnType.equals("bigint")){
            return FieldType.of(false, null, "Long", JDBCType.INTEGER, origin, null);
        }


        if(columnType.equals("float")){
            return FieldType.of(false, null, "Float", JDBCType.FLOAT, origin, null);
        }

        if(columnType.equals("double")){
            return FieldType.of(false, null, "Double", JDBCType.DOUBLE, origin, null);
        }

        if(columnType.equals("decimal")){
            return FieldType.of(false, "java.math.BigDecimal", "BigDecimal", JDBCType.DECIMAL, origin, null);
        }

        switch (columnType){
        }
        return FieldType.error(origin);
    }


    private String getColumnType(String origin){
        if(origin.indexOf("(") == -1) return origin;
        return origin.substring(0, origin.indexOf("("));
    }
}

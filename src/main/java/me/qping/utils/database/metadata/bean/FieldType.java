package me.qping.utils.database.metadata.bean;

import lombok.Data;

import java.sql.JDBCType;
import java.sql.SQLType;

/**
 * @ClassName FieldType
 * @Description 封装字段类型
 * @Author qping
 * @Date 2019/11/13 18:04
 * @Version 1.0
 **/
@Data
public class FieldType {
    boolean isDate = false;
    String columnDefinition;
    String javaFullType;
    String javaPackage;
    String javaType;
    SQLType sqlType;
    String origin;

    public static FieldType of(boolean isDate, Object javaFullType, String javaPackage, String javaType, SQLType sqlType, String origin, String columnDefinition){
        FieldType t = new FieldType();
        t.setDate(isDate);
        t.setJavaPackage(javaPackage);
        t.setJavaType(javaType);
        t.setSqlType(sqlType);
        t.setOrigin(origin);
        t.setColumnDefinition(columnDefinition);
        return t;
    }

    public static FieldType error(String origin){
        FieldType t = new FieldType();
        t.setDate(false);
        t.setJavaPackage(null);
        t.setJavaType(null);
        t.setSqlType(JDBCType.OTHER);
        t.setOrigin(origin);
        return t;
    }
}

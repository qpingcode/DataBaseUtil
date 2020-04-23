package me.qping.utils.database.metadata.bean;

import lombok.Data;

import java.sql.SQLType;

/**
 * @ClassName ColumnMeta
 * @Author qping
 * @Date 2019/8/3 21:32
 * @Version 1.0
 **/
@Data
public class ColumnMeta {

    String name;
    String alias;
    String type;
    String comment;

    int size;
    int digits;
    boolean nullable;
    boolean isPrimaryKey;

    Boolean isDate;
    String javaFullType;
    String javaType;
    String javaPackage;
    String columnDefinition;
    SQLType sqlType;

    public static ColumnMeta of(String name, String type, String comment, int size, int digits, boolean nullable, boolean isPrimaryKey, String javaFullType, String javaType, String javaPackage, Boolean isDate, SQLType sqlType, String columnDefinition){
        ColumnMeta columnMeta = new ColumnMeta();
        columnMeta.setName(name);
        columnMeta.setType(type);
        columnMeta.setComment(comment);
        columnMeta.setSize(size);
        columnMeta.setDigits(digits);
        columnMeta.setNullable(nullable);
        columnMeta.setPrimaryKey(isPrimaryKey);
        columnMeta.setJavaFullType(javaFullType);
        columnMeta.setJavaType(javaType);
        columnMeta.setJavaPackage(javaPackage);
        columnMeta.setIsDate(isDate);
        columnMeta.setSqlType(sqlType);
        columnMeta.setColumnDefinition(columnDefinition);
        return columnMeta;
    }

}

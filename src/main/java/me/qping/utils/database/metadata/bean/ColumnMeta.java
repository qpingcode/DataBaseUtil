package me.qping.utils.database.metadata.bean;

import lombok.Data;

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
    String javaType;
    String javaImport;


    public static ColumnMeta of(String name, String type, String comment, int size, int digits, boolean nullable, boolean isPrimaryKey, String javaType, StringBuffer javaImport, Boolean isDate){
        ColumnMeta columnMeta = new ColumnMeta();
        columnMeta.setName(name);
        columnMeta.setType(type);
        columnMeta.setComment(comment);
        columnMeta.setSize(size);
        columnMeta.setDigits(digits);
        columnMeta.setNullable(nullable);
        columnMeta.setPrimaryKey(isPrimaryKey);
        columnMeta.setJavaType(javaType);
        columnMeta.setJavaImport((javaImport == null || javaImport.toString().equals("")) ? null : javaImport.toString());
        columnMeta.setIsDate(isDate);
        return columnMeta;
    }

}

package me.qping.utils.database.metadata.bean;

import lombok.Data;

import java.sql.JDBCType;

/**
 * @ClassName ColumnMeta
 * @Description 列属性
 * @Author qping
 * @Date 2019/8/3 21:32
 * @Version 1.0
 **/
@Data
public class ColumnMeta {

    String name;            // 列名
    String alias;           // 别名
    String type;            // TYPE_NAME  数据库原始列类型比如VARCHAR(200)
    String comment;         // 备注

    int size;                   // 字段长度
    int digits;                 // 小数精度
    boolean nullable;           // 是否可为空
    boolean isPrimaryKey;       // 是否主键

    Boolean isDate;             // 是否日期类型
    String javaFullType;        // java 类型完整路径，如 java.lang.Integer
    String javaType;            // 自动生成代码时，字段前缀, 如 Integer
    String javaPackage;         // 自动生成代码时，需要 import 的类库 ,如 java.util.Date
    String columnDefinition;    // TYPE_NAME  数据库原始列类型比如VARCHAR(200)
    JDBCType sqlType;           // java.sql.Types

    public static ColumnMeta of(String name, String type, String comment, int size, int digits, boolean nullable, boolean isPrimaryKey, String javaFullType, String javaType, String javaPackage, Boolean isDate, JDBCType jdbcType, String columnDefinition){
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
        columnMeta.setSqlType(jdbcType);
        columnMeta.setColumnDefinition(columnDefinition);
        return columnMeta;
    }

}

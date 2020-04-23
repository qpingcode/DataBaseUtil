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
public class ResultSetColumnMeta {

    String name;
    String type;
    int size;
    int digits;
    String className;

    public static ResultSetColumnMeta of(String name, String type, int size, int digits, String className){
        ResultSetColumnMeta columnMeta = new ResultSetColumnMeta();
        columnMeta.setName(name);
        columnMeta.setType(type);
        columnMeta.setSize(size);
        columnMeta.setDigits(digits);
        columnMeta.setClassName(className);
        return columnMeta;
    }

}

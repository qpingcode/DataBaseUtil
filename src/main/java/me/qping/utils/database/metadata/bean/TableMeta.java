package me.qping.utils.database.metadata.bean;

import lombok.Data;
import me.qping.utils.database.connect.DataBaseType;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName TableMeta
 * @Author qping
 * @Date 2019/8/3 21:32
 * @Version 1.0
 **/
@Data
public class TableMeta {

    DataBaseType databaseType;
    String catalog;
    String schema;
    String name;
    String nameLower;
    String alias;
    String type;
    String comment;

    List<PrimaryKeyMeta> primaryKeys;
    List<ColumnMeta> columns;


    public static TableMeta of(String catalog, String schema, String name, String nameLower, String type, String comment, DataBaseType databaseType){

        TableMeta tableMeta = new TableMeta();

        tableMeta.setCatalog(catalog);
        tableMeta.setSchema(schema);
        tableMeta.setName(name);
        tableMeta.setNameLower(nameLower);
        tableMeta.setType(type);
        tableMeta.setComment(comment);
        tableMeta.setDatabaseType(databaseType);
        return tableMeta;
    }

    public String createInsertSQL(){
        return createInsertSQL(null);
    }

    public String createInsertSQL(List<String> excludeColumns){

        if(excludeColumns != null){
            excludeColumns = excludeColumns.stream().map(v -> v.toUpperCase()).collect(Collectors.toList());
        }

        StringBuffer firstPart = new StringBuffer();
        StringBuffer secondPart = new StringBuffer();
        for(ColumnMeta columnMeta : columns){

            String columnName = columnMeta.getName().toUpperCase();
            if(excludeColumns != null && excludeColumns.contains(columnName)){
                continue;
            }

            firstPart.append(columnName + ",");
            secondPart.append("?,");
        }

        if(firstPart.length() == 0){
            throw new RuntimeException("TableMeta create sql error，column size is zero!");
        }

        StringBuffer sql = new StringBuffer();
        sql.append("insert into ")
                .append(name.toUpperCase())
                .append(" ( ")
                .append(firstPart.substring(0, firstPart.length() - 1))
                .append(" ) ")
                .append(" values ( ")
                .append(secondPart.substring(0, secondPart.length() - 1))
                .append(" ) ");

        return sql.toString();

    }


    public String createInsertSelectAs(){

        StringBuffer firstPart = new StringBuffer();
        for(ColumnMeta columnMeta : columns){
            String columnName = columnMeta.getName().toUpperCase();
            firstPart.append(columnName + ",");
        }

        if(firstPart.length() == 0){
            throw new RuntimeException("TableMeta create sql error，column size is zero!");
        }


        StringBuffer sql = new StringBuffer();
        sql.append("insert into ")
                .append(name.toUpperCase())
                .append(" ( ")
                .append(firstPart.substring(0, firstPart.length() - 1))
                .append(" ) ")
                .append(" select ")
                .append(firstPart.substring(0, firstPart.length() - 1))
                .append(" from QZJ_INTERFACE_V20.dbo." + name.toUpperCase());

        return sql.toString();

    }




}

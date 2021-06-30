package me.qping.utils.database.metadata.bean;

import com.alibaba.druid.util.StringUtils;
import lombok.Data;
import me.qping.utils.database.connect.DataBaseType;

import java.sql.SQLType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName TableMeta
 * @Description 表属性
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


    public void addColumn(String name, String type, String comment, int size, int digits, boolean nullable, boolean isPrimaryKey){
        if(columns == null){
            columns = new ArrayList<>();
        }

        List<ColumnMeta> list = columns.stream().filter(v -> v.getName().equals(name)).collect(Collectors.toList());

        if(list.size() > 0){
            System.out.println("cannt duplicate add column ： " + name);
            return;
        }

        ColumnMeta col = new ColumnMeta();
        col.setName(name);
        col.setType(type);
        col.setComment(comment);
        col.setSize(size);
        col.setDigits(digits);
        col.setNullable(nullable);
        col.setPrimaryKey(isPrimaryKey);

        columns.add(col);
    }


    public static TableMeta of(String catalog, String schema, String name, String type, String comment, DataBaseType databaseType){

        TableMeta tableMeta = new TableMeta();

        tableMeta.setCatalog(catalog);
        tableMeta.setSchema(schema);
        tableMeta.setName(name);
        tableMeta.setNameLower(name == null ? null : name.toLowerCase());
        tableMeta.setType(type);
        tableMeta.setComment(comment);
        tableMeta.setDatabaseType(databaseType);
        return tableMeta;
    }

    public String createCreateSQL(){

        StringBuffer sql = new StringBuffer();

        sql.append("CREATE TABLE ").append(name);
        sql.append(" ( ");

        for(ColumnMeta col: columns){
            sql.append(col.getName()).append(" ").append(col.getType());

            if(col.getSize() > 0){
                sql.append(" (")
                .append(col.getDigits() > 0 ? col.getSize() + "," + col.getDigits() : col.getSize())
                .append(")");
            }

            sql.append(col.isNullable() ? " DEFAULT NULL" : " NOT NULL")
                .append(StringUtils.isEmpty(col.getComment()) ? "" : " COMMENT '" + col.getComment() + "'")
                .append(",");
        }

        sql.deleteCharAt(sql.length() - 1);
        sql.append(" ) ");

        return sql.toString();

    }
    public String createInsertSQL(){
        return createInsertSQL(null);
    }

    public String createInsertSQL(List<String> excludeColumns){

        if(columns == null){
            return null;
        }

        if(excludeColumns != null){
            excludeColumns = excludeColumns.stream().map(v -> v.toUpperCase()).collect(Collectors.toList());
        }

        StringBuffer firstPart = new StringBuffer();
        StringBuffer secondPart = new StringBuffer();
        for(ColumnMeta columnMeta : columns){

            String columnName = columnMeta.getName();
            if(excludeColumns != null && excludeColumns.contains(columnName.toUpperCase())){
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
                .append(name)
                .append(" ( ")
                .append(firstPart.substring(0, firstPart.length() - 1))
                .append(" ) ")
                .append(" values ( ")
                .append(secondPart.substring(0, secondPart.length() - 1))
                .append(" ) ");

        return sql.toString();
    }

    public String createQuerySQL(){

        if(columns == null){
            return null;
        }

        StringBuffer firstPart = new StringBuffer();
        for(ColumnMeta columnMeta : columns){
            String columnName = columnMeta.getName();
            firstPart.append(columnName + ",");
        }

        String tableName = name;
        if((databaseType.equals(DataBaseType.MSSQL) || databaseType.equals(DataBaseType.SQLSERVER2000) || databaseType.equals(DataBaseType.INFOSYSCACHE) || databaseType.equals(DataBaseType.HIVE)) && schema != null){
            tableName = schema + "." + name;
        }

        StringBuffer sql = new StringBuffer();
        sql.append("select ")
                .append(firstPart.substring(0, firstPart.length() - 1))
                .append(" from ")
                .append(tableName);

        return sql.toString();
    }

}

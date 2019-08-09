package me.qping.utils.database.metadata.bean;

import lombok.Data;

import java.util.List;

/**
 * @ClassName TableMeta
 * @Author qping
 * @Date 2019/8/3 21:32
 * @Version 1.0
 **/
@Data
public class TableMeta {

    public static final String TYPE_MSSQL = "mssql";
    public static final String TYPE_MYSQL = "mysql";
    public static final String TYPE_ORACLE = "oracle";
    public static final String TYPE_DB2 = "db2";
    public static final String TYPE_SQLITE = "sqlite";

    String databaseType;
    String catalog;
    String schema;
    String name;
    String alias;
    String type;
    String comment;

    List<PrimaryKeyMeta> primaryKeys;
    List<ColumnMeta> columns;


    public static TableMeta of(String catalog, String schema, String name, String type, String comment, String databaseType){

        TableMeta tableMeta = new TableMeta();

        tableMeta.setCatalog(catalog);
        tableMeta.setSchema(schema);
        tableMeta.setName(name);
        tableMeta.setType(type);
        tableMeta.setComment(comment);
        tableMeta.setDatabaseType(databaseType);
        return tableMeta;
    }

}

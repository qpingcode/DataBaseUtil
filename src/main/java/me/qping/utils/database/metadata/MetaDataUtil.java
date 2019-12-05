package me.qping.utils.database.metadata;

import me.qping.utils.database.connect.DataBaseConnectPropertes;
import me.qping.utils.database.crud.CrudUtil;
import me.qping.utils.database.metadata.bean.ColumnMeta;
import me.qping.utils.database.metadata.bean.FieldType;
import me.qping.utils.database.metadata.bean.PrimaryKeyMeta;
import me.qping.utils.database.metadata.bean.TableMeta;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public abstract class MetaDataUtil extends CrudUtil {


    public static final String TYPE_TABLE = "TABLE";
    public static final String TYPE_VIEW = "VIEW";

    // 数据库类型与 java 类型映射
    // https://blog.csdn.net/weixin_34195546/article/details/87611601
    public abstract FieldType getFieldType(String columnType);

    public abstract String getCatalogQuery();

    public abstract String getSchemaQuery(String catalog);

    public abstract Properties getConnectionProperties(DataBaseConnectPropertes connectType);


    public List<String> getCatalogs() throws SQLException {
        String query = getCatalogQuery();
        if(query == null){
            return null;
        }

        List<Object[]> result = queryArray(query);
        List<String> names = new ArrayList<>();
        for(Object[] o : result){
            if(o != null && o.length > 0 && o[0] != null){
                names.add(o[0].toString());
            }
        }
        return names;
    }

    public List<String> getSchemas(String catalog) throws SQLException {
        String query = getSchemaQuery(catalog);
        if(query == null){
            return null;
        }

        List<Object[]> result = queryArray(query);
        List<String> names = new ArrayList<>();
        for(Object[] o : result){
            if(o != null && o.length > 0 && o[0] != null){
                names.add(o[0].toString());
            }
        }
        return names;
    };

    public List<TableMeta> getObjects(DataBaseConnectPropertes connectType, String catalog, String schema, String[] types){
        try {
            Class.forName(connectType.getDriver());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try (Connection connection = DriverManager.getConnection(connectType.getUrl(), getConnectionProperties(connectType))) {


            DatabaseMetaData metadata = connection.getMetaData();
            ResultSet tableInfo = metadata.getTables(catalog, schema, "%", types);
            List<TableMeta> list = new ArrayList<>();
            while (tableInfo.next()){

                String tableName = tableInfo.getString("TABLE_NAME");
                TableMeta tableMeta = TableMeta.of(
                        tableInfo.getString("TABLE_CAT"),
                        tableInfo.getString("TABLE_SCHEM"),
                        tableName.toUpperCase(),
                        tableName.toLowerCase(),
                        tableInfo.getString("TABLE_TYPE"),  // 表类型
                        tableInfo.getString("REMARKS"),     // 表注释
                        connectType.getDataBaseType()
                );

                list.add(tableMeta);
            }

            return list;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;


    }

    public List<TableMeta> getTables(){
        return getObjects(dataBaseConnectType, dataBaseConnectType.getCatalog(), dataBaseConnectType.getSchema(), new String[]{TYPE_TABLE, TYPE_VIEW});
    }

    public List<TableMeta> getTables(String catalog, String schema){
        return getObjects(dataBaseConnectType, catalog, schema, new String[]{TYPE_TABLE, TYPE_VIEW});
    }

    public TableMeta getTableInfo(String tableName){
        return getTableInfo(tableName, null);
    }

    public TableMeta getTableInfo(String tableName, List<String> excludeColumns){
        return getTableInfo(dataBaseConnectType.getCatalog(), dataBaseConnectType.getSchema(), tableName, excludeColumns);
    }

    private TableMeta getTableInfo(String catalog, String schema, String tableName, List<String> excludeColumns) {

        tableName = tableName.toUpperCase();
        try {
            Class.forName(dataBaseConnectType.getDriver());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try (Connection connection = DriverManager.getConnection(dataBaseConnectType.getUrl(), getConnectionProperties(dataBaseConnectType))){
            TableMeta tableMeta = new TableMeta();

            DatabaseMetaData metadata = connection.getMetaData();

            /**
             * 来源：https://blog.csdn.net/hekewangzi/article/details/41390155
             *
             * types 典型的类型是 "TABLE"、"VIEW"、"SYSTEM TABLE"、"GLOBAL TEMPORARY"、"LOCAL TEMPORARY"、"ALIAS" 和 "SYNONYM"。
             * 来源：https://www.cnblogs.com/lbangel/p/3487796.html
             */
            String[] types = {"TABLE", "VIEW"};

            ResultSet tableInfo = metadata.getTables(catalog, schema, tableName, types);
            if(tableInfo.next()){

                tableMeta = TableMeta.of(
                        tableInfo.getString("TABLE_CAT"),
                        tableInfo.getString("TABLE_SCHEM"),
                        tableName.toUpperCase(),
                        tableName.toLowerCase(),
                        tableInfo.getString("TABLE_TYPE"),  // 表类型
                        tableInfo.getString("REMARKS"),     // 表注释
                        dataBaseConnectType.getDataBaseType()
                );
            }

            List<PrimaryKeyMeta> primaryKeyMetas = new ArrayList<>();
            Set<String> primaryKeySet = new HashSet<>();
            ResultSet primaryKeyResultSet = metadata.getPrimaryKeys(catalog, schema, tableName);
            while(primaryKeyResultSet.next()){
                String primaryKeyColumnName = primaryKeyResultSet.getString("COLUMN_NAME");
                String pkName = primaryKeyResultSet.getString("PK_NAME");
                short keySeq = primaryKeyResultSet.getShort("KEY_SEQ");

                PrimaryKeyMeta primaryKeyMeta = PrimaryKeyMeta.of(primaryKeyColumnName, keySeq);
                primaryKeyMetas.add(primaryKeyMeta);
                primaryKeySet.add(primaryKeyColumnName.toUpperCase());
            }

            List<ColumnMeta> columnMetas = new ArrayList<>();
            if(excludeColumns != null){
                excludeColumns = excludeColumns.stream().map(v -> v.toUpperCase()).collect(Collectors.toList());
            }
            ResultSet columnsInfo = metadata.getColumns(catalog, schema, tableName,"%");
            while(columnsInfo.next()){

                String columnName = columnsInfo.getString("COLUMN_NAME");
                String columnType = columnsInfo.getString("TYPE_NAME");

                columnType = columnType.toLowerCase();
                columnName = columnName.toUpperCase();

                if(excludeColumns != null && excludeColumns.contains(columnName)){
                    continue;
                }

                int size = columnsInfo.getInt("COLUMN_SIZE");
                int digits = columnsInfo.getInt("DECIMAL_DIGITS");
                int nullable = columnsInfo.getInt("NULLABLE");
                String remarks = columnsInfo.getString("REMARKS");

                FieldType fieldType = getFieldType(columnType);

                boolean isPrimaryKey = primaryKeySet.contains(columnName);

                columnMetas.add(ColumnMeta.of(columnName.toUpperCase(), columnType, remarks, size, digits, nullable == 1,
                        isPrimaryKey, fieldType.getJavaType(), fieldType.getJavaPackage(), fieldType.isDate(), fieldType.getSqlType(), fieldType.getColumnDefinition()));
            }

            tableMeta.setColumns(columnMetas);
            tableMeta.setPrimaryKeys(primaryKeyMetas);

            return tableMeta;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }



}

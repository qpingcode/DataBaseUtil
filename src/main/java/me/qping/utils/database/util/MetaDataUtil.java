package me.qping.utils.database.util;

import me.qping.utils.database.connect.DataBaseType;
import me.qping.utils.database.metadata.bean.*;

import java.sql.*;
import java.util.*;

public class MetaDataUtil extends CrudUtil {


    public static final String TYPE_TABLE = "TABLE";
    public static final String TYPE_VIEW = "VIEW";

    public List<String> getCatalogs() throws SQLException {
        String query = dataBaseDialect.getCatalogQuery();
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
        String query = dataBaseDialect.getSchemaQuery();
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

    /**
     *
     * @param types 典型的类型是 "TABLE"、"VIEW"、"SYSTEM TABLE"、"GLOBAL TEMPORARY"、"LOCAL TEMPORARY"、"ALIAS" 和 "SYNONYM"。
     * @return
     * @throws SQLException
     */
    public List<TableMeta> getObjects(String[] types) throws SQLException {
        return getObjects(null, null, types);
    }

    /**
     *
     * @param catalog
     * @param schema
     * @param types 典型的类型是 "TABLE"、"VIEW"、"SYSTEM TABLE"、"GLOBAL TEMPORARY"、"LOCAL TEMPORARY"、"ALIAS" 和 "SYNONYM"。
     * @return
     * @throws SQLException
     */
    public List<TableMeta> getObjects(String catalog, String schema, String[] types) throws SQLException {

        if(catalog == null && schema == null){
            catalog = dataBaseConnectProperties.getCatalog();
            schema = dataBaseConnectProperties.getSchema();
        }

        try (Connection connection = DriverManager.getConnection(dataBaseConnectProperties.getUrl(), dataBaseDialect.getConnectionProperties(dataBaseConnectProperties))) {
            DatabaseMetaData metadata = connection.getMetaData();
            ResultSet tableInfo = metadata.getTables(catalog, schema, "%", types);
            List<TableMeta> list = new ArrayList<>();
            while (tableInfo.next()){

                String tableName = tableInfo.getString("TABLE_NAME");
                TableMeta tableMeta = TableMeta.of(
                        tableInfo.getString("TABLE_CAT"),
                        tableInfo.getString("TABLE_SCHEM"),
                        tableName,
                        tableName.toLowerCase(),
                        tableInfo.getString("TABLE_TYPE"),  // 表类型
                        tableInfo.getString("REMARKS"),     // 表注释
                        dataBaseConnectProperties.getDataBaseType()
                );
                list.add(tableMeta);
            }
            return list;

        } catch (SQLException e) {
           throw e;
        }
    }


    public List<TableMeta> getTables() throws SQLException {
        return getObjects(new String[]{TYPE_TABLE});
    }

    public List<TableMeta> getViews() throws SQLException {
        return getObjects(new String[]{TYPE_VIEW});
    }

    public List<TableMeta> getViews(String catalog, String schema) throws SQLException {
        return getObjects(catalog, schema, new String[]{TYPE_VIEW});
    }

    public List<TableMeta> getTables(String catalog, String schema) throws SQLException {
        return getObjects(catalog, schema, new String[]{TYPE_TABLE});
    }

    public TableMeta getTableInfo(String tableName) throws SQLException {
        return getTableInfo(null, null, tableName);
    }

    public TableMeta getTableInfo(String catalog, String schema, String tableName) throws SQLException {
//        tableName = tableName.toUpperCase();
        try (Connection connection = DriverManager.getConnection(dataBaseConnectProperties.getUrl(), dataBaseDialect.getConnectionProperties(dataBaseConnectProperties))){
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
                        tableName,
                        tableName.toLowerCase(),
                        tableInfo.getString("TABLE_TYPE"),  // 表类型
                        tableInfo.getString("REMARKS"),     // 表注释
                        dataBaseConnectProperties.getDataBaseType()
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
                primaryKeySet.add(primaryKeyColumnName);
            }

            List<ColumnMeta> columnMetas = new ArrayList<>();
            ResultSet columnsInfo = metadata.getColumns(catalog, schema, tableName,"%");

            while(columnsInfo.next()){

                String columnName = columnsInfo.getString("COLUMN_NAME");
                String columnType = columnsInfo.getString("TYPE_NAME");

                columnType = columnType.toLowerCase();

                int size = columnsInfo.getInt("COLUMN_SIZE");
                int digits = columnsInfo.getInt("DECIMAL_DIGITS");
                int nullable = columnsInfo.getInt("NULLABLE");
                String remarks = columnsInfo.getString("REMARKS");

                FieldType fieldType = dataBaseDialect.getFieldType(columnType);

                boolean isPrimaryKey = primaryKeySet.contains(columnName);

                columnMetas.add(ColumnMeta.of(columnName, columnType, remarks, size, digits, nullable == 1,
                        isPrimaryKey, fieldType.getJavaType(), fieldType.getJavaPackage(), fieldType.isDate(), fieldType.getSqlType(), fieldType.getColumnDefinition()));
            }

//            ResultSet foreignKeys = metadata.getImportedKeys(catalog, schema, tableName);
//            while(foreignKeys.next()){
//
//            }

            tableMeta.setColumns(columnMetas);
            tableMeta.setPrimaryKeys(primaryKeyMetas);

            return tableMeta;
        } catch (SQLException e) {
            throw e;
        }
    }

    public Map<String, Object> queryListAndMeta(String sql, Object... paramters) throws SQLException {
        return queryListAndMeta(null, null, sql, paramters);
    }

    public Map<String, Object> queryListAndMeta(String catalogName, String schemaName, String sql, Object... paramters) throws SQLException {

        try(Connection connection = getConnection()){

            if(catalogName != null || schemaName != null){
                switchTo(connection, catalogName, schemaName);
            }

            PreparedStatement ps = connection.prepareStatement(sql);
            prepareParameters(ps, paramters);

            ResultSet rs = ps.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            List<ResultSetColumnMeta> columnMetaList = new ArrayList<>();
            for(int i = 0; i < columnCount; i++){
                String name = metaData.getColumnName(i + 1);
                int type = metaData.getColumnType(i + 1);
                String className = metaData.getColumnClassName(i + 1);
                String label = metaData.getColumnLabel(i + 1);
                String typeName = metaData.getColumnTypeName(i + 1);
                int precision = metaData.getPrecision(i + 1);
                int scale = metaData.getScale(i + 1);

                ResultSetColumnMeta columnMeta = ResultSetColumnMeta.of(name, typeName, precision, scale, className);
                columnMetaList.add(columnMeta);
            }


            List<Map<String, Object>> resultData = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<String, Object>();
                for (int i = 0; i < columnCount; i++) {
                    String label = metaData.getColumnLabel(i + 1);
                    map.put(label, rs.getObject(label));
                }
                resultData.add(map);
            }

            Map<String, Object> res = new HashMap<>();
            res.put("columnMetaList", columnMetaList);
            res.put("result", resultData);
            return res;

        } catch (SQLException e) {
            throw e;
        }
    }

    public void queryListAndMeta(Callback callback, String sql, Object... paramters) throws SQLException {

        try(Connection connection = getConnection()){

            PreparedStatement ps = connection.prepareStatement(sql);
            prepareParameters(ps, paramters);

            ResultSet rs = ps.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            List<ResultSetColumnMeta> columnMetaList = new ArrayList<>();
            for(int i = 0; i < columnCount; i++){
                String name = metaData.getColumnName(i + 1);
                int type = metaData.getColumnType(i + 1);
                String className = metaData.getColumnClassName(i + 1);
                String label = metaData.getColumnLabel(i + 1);
                String typeName = metaData.getColumnTypeName(i + 1);
                int precision = metaData.getPrecision(i + 1);
                int scale = metaData.getScale(i + 1);

                ResultSetColumnMeta columnMeta = ResultSetColumnMeta.of(name, typeName, precision, scale, className);
                columnMetaList.add(columnMeta);
            }

            while (rs.next()) {
                Map<String, Object> map = new HashMap<String, Object>();
                for (int i = 0; i < columnCount; i++) {
                    String label = metaData.getColumnLabel(i + 1);
                    map.put(label, rs.getObject(label));
                }
                callback.next(map, columnMetaList);
            }

        } catch (SQLException e) {
            throw e;
        }
    }



    private void switchTo(Connection connection, String catalogName, String schemaName) throws SQLException {
        DataBaseType dataBaseType = getDataBaseConnectType();
        switch (dataBaseType){
            case MSSQL:
                update(connection, "USE " + catalogName);
                update(connection, "EXECUTE as USER ='" + schemaName + "'");
                break;
            case MYSQL:
                update(connection, "USE " + catalogName);
                break;
            case ORACLE:
                update(connection, "ALTER SESSION SET CURRENT_SCHEMA = '" + schemaName +"'");
                break;
        }
    }



}

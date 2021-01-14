package me.qping.utils.database.util;

import me.qping.common.model.DataRecord;
import me.qping.utils.database.connect.DataBaseType;
import me.qping.utils.database.metadata.bean.*;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class MetaDataUtil extends CrudUtil {


    public static final String TYPE_TABLE = "TABLE";
    public static final String TYPE_VIEW = "VIEW";

    public List<String> getCatalogs() throws SQLException {
        String query = dataBaseDialect.getCatalogQuery();
        if(query == null){
            return null;
        }

        List<DataRecord> result = queryList(query);
        List<String> names = result.stream().map(v->v.getString(0)).collect(Collectors.toList());
        return names;
    }

    public List<String> getSchemas(String catalog) throws SQLException {
        String query = dataBaseDialect.getSchemaQuery();
        if(query == null){
            return null;
        }
        List<DataRecord> result = queryList(query);
        List<String> names = result.stream().map(v->v.getString(0)).collect(Collectors.toList());
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

        String catalog = getDataBaseConnectProperties().getCatalog();
        String schema = getDataBaseConnectProperties().getSchema();
        return getTableInfo(catalog, schema, tableName);
    }

    public TableMeta getTableInfo(String catalog, String schema, String tableName) throws SQLException {
//        tableName = tableName.toUpperCase();

        /*
            oracle：默认不区分大小写
            mysql： windows下不区分大小写，linux下数据库名、表名、表别名、变量名区分大小写，列名不区分
            sqlserver：默认不区分大小写， 可以通过修改排序规则改变： 右击数据库>属性>选项>排序规则： Chinese_PRC_CS_AS 区分大小写、 Chinese_PRC_CI_AS 不区分大小写
         */

        // oracle下小写表名无法查询的bug，只有oracle的驱动会出现这种问题
        if(dataBaseConnectProperties.getDataBaseType().equals(DataBaseType.ORACLE)){
            tableName = tableName.toUpperCase();
        }

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
                int columnType = columnsInfo.getInt("DATA_TYPE");  // SQL type from java.sql.Types
                String typeName = columnsInfo.getString("TYPE_NAME");

                int size = columnsInfo.getInt("COLUMN_SIZE");
                int digits = columnsInfo.getInt("DECIMAL_DIGITS");
                int nullable = columnsInfo.getInt("NULLABLE");
                String remarks = columnsInfo.getString("REMARKS");

                boolean isPrimaryKey = primaryKeySet.contains(columnName);

                // java.sql.Types
                boolean isDate = false;
                String javaType = null, javaFullType = null, javaPackage = null;
                JDBCType jdbcType = JDBCType.valueOf(columnType);
                switch (jdbcType){
                    case BIT:
                    case BOOLEAN:
                        javaType = "Boolean";
                        javaFullType = "java.lang.Boolean";
                        break;
                    case TINYINT:
                        javaType = "Byte";
                        javaFullType = "java.lang.Byte";
                        break;
                    case SMALLINT:
                        javaType = "Short";
                        javaFullType = "java.lang.Short";
                        break;
                    case INTEGER:
                        javaType = "Integer";
                        javaFullType = "java.lang.Integer";
                        break;
                    case BIGINT:
                        javaType = "Long";
                        javaFullType = "java.lang.Long";
                        break;
                    case DOUBLE:
                    case NUMERIC:
                    case DECIMAL:
                    case FLOAT:
                        javaType = "BigDecimal";
                        javaFullType = "java.math.BigDecimal";
                        javaPackage = "java.math.BigDecimal";
                        break;
                    case REAL:
                        javaType = "Float";
                        javaFullType = "java.lang.Float";
                        break;
                    case NCHAR:
                    case NVARCHAR:
                    case LONGNVARCHAR:
                    case CHAR:
                    case VARCHAR:
                    case LONGVARCHAR:
                        javaType = "String";
                        javaFullType = "java.lang.String";
                        break;
                    case DATE:
                    case TIME:
                        isDate = true;
                        javaType = "Date";
                        javaFullType = "java.sql.Date";
                        javaPackage = "java.sql.Date";
                        break;
                    case TIME_WITH_TIMEZONE:
                    case TIMESTAMP_WITH_TIMEZONE:
                    case TIMESTAMP:
                        isDate = true;
                        javaType = "Timestamp";
                        javaFullType = "java.sql.Timestamp";
                        javaPackage = "java.sql.Timestamp";
                        break;
                    case BINARY:
                    case VARBINARY:
                    case LONGVARBINARY:
                        javaType = "byte[]";
                        javaFullType = "byte[]";
                        break;
                    case STRUCT:
                        javaType = "Struct";
                        javaFullType = "java.sql.Struct";
                        javaPackage = "java.sql.Struct";
                        break;
                    case ARRAY:
                        javaType = "Array";
                        javaFullType = "java.sql.Array";
                        javaPackage = "java.sql.Array";
                        break;
                    case BLOB:
                        javaType = "Blob";
                        javaFullType = "java.sql.Blob";
                        javaPackage = "java.sql.Blob";
                        break;
                    case CLOB:
                        javaType = "Clob";
                        javaFullType = "java.sql.Clob";
                        javaPackage = "java.sql.Clob";
                        break;
                    case REF:
                        javaType = "Ref";
                        javaFullType = "java.sql.Ref";
                        javaPackage = "java.sql.Ref";
                        break;
                    case DATALINK:
                        javaType = "URL";
                        javaFullType = "java.net.URL";
                        javaPackage = "java.net.URL";
                        break;
                    case NCLOB:
                        javaType = "NClob";
                        javaFullType = "java.sql.NClob";
                        javaPackage = "java.sql.NClob";
                        break;
                }
                columnMetas.add(ColumnMeta.of(columnName, typeName, remarks, size, digits, nullable == 1, isPrimaryKey, javaFullType, javaType, javaPackage, isDate, jdbcType, null));
            }

            tableMeta.setColumns(columnMetas);
            tableMeta.setPrimaryKeys(primaryKeyMetas);

            return tableMeta;
        } catch (SQLException e) {
            throw e;
        }
    }

    public List<ColumnMeta> queryColumnMeta(String catalogName, String schemaName, String sql, Object... paramters)throws SQLException {
        try(Connection connection = getConnection()){

            if(catalogName != null || schemaName != null){
                switchTo(connection, catalogName, schemaName);
            }

            sql = getDataBaseDialect().getPageSql(sql, 0, 0);

            PreparedStatement ps = connection.prepareStatement(sql);
            prepareParameters(ps, paramters);


            ResultSet rs = ps.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();

            List<ColumnMeta> columnMetaList = getResultColumnMeta(metaData);
            return columnMetaList;

        }catch (SQLException e) {
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

            List<ColumnMeta> columnMetaList = getResultColumnMeta(metaData);

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


    private List<ColumnMeta> getResultColumnMeta(ResultSetMetaData metaData) throws SQLException {
        List<ColumnMeta> columnMetaList = new ArrayList<>();
        for(int i = 0; i < metaData.getColumnCount(); i++){
            String name = metaData.getColumnName(i + 1);
            String label = metaData.getColumnLabel(i + 1);
            int precision = metaData.getPrecision(i + 1);
            int scale = metaData.getScale(i + 1);
            int sqlType = metaData.getColumnType(i + 1);
            String typeName = metaData.getColumnTypeName(i + 1);
            String className = metaData.getColumnClassName(i + 1);

            ColumnMeta columnMeta = new ColumnMeta();
            columnMeta.setName(name);
            columnMeta.setAlias(label);
            columnMeta.setSize(precision);
            columnMeta.setDigits(scale);

            columnMeta.setSqlType(JDBCType.valueOf(sqlType));
            columnMeta.setType(typeName);
            columnMeta.setJavaType(getjavaType(className));
            columnMeta.setJavaPackage(className);
            columnMeta.setJavaType(className.indexOf("java.lang") > -1 ? null : className);

            columnMetaList.add(columnMeta);
        }
        return columnMetaList;
    }

    private String getjavaType(String fullJavaType){

        if(fullJavaType.indexOf("\\.") > -1){
            return fullJavaType.substring(fullJavaType.indexOf("\\."));
        }else{
            return fullJavaType;
        }
    }


}

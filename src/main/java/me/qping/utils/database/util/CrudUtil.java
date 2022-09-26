package me.qping.utils.database.util;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.Data;
import me.qping.common.model.DataRecord;
import me.qping.utils.database.bean.ArrayListWithMeta;
import me.qping.utils.database.bean.BeanConversion;
import me.qping.utils.database.bean.FieldDefines;
import me.qping.utils.database.connect.DataBaseConnectPropertes;
import me.qping.utils.database.connect.DataBaseType;
import me.qping.utils.database.exception.OrmException;
import me.qping.utils.database.metadata.bean.ColumnMeta;
import me.qping.utils.dynamicloader.DynamicClassLoader;

import javax.sql.DataSource;
import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.util.*;

/**
 * @ClassName CrudUtil
 * @Description 增删该查
 * @Author qping
 * @Date 2019/12/5 17:00
 * @Version 1.0
 **/
@Data
public class CrudUtil {

    protected DataBaseConnectPropertes dataBaseConnectProperties;

    protected DataSource dataSource;

    public void close(){
        if(dataSource != null && dataSource instanceof DruidDataSource){
            DruidDataSource druidDataSource = (DruidDataSource) dataSource;
            druidDataSource.close();
        }
    }
    public Connection getConnection() throws SQLException {
        Properties props = new Properties();
        if(dataBaseConnectProperties.getUsername() != null){
            props.setProperty("user", dataBaseConnectProperties.getUsername());
        }
        if(dataBaseConnectProperties.getPassword() != null){
            props.setProperty("password", dataBaseConnectProperties.getPassword());
        }
        return getConnection(props);
    }

    public Connection getMetaConnection() throws SQLException {
        return getConnection(dataBaseConnectProperties.getConnectionProperties());
    }

    public Connection getConnection(Properties properties) throws SQLException {
        Connection connection = null;
        if(dataSource != null){
            connection = dataSource.getConnection();
        }else{
            DynamicClassLoader classLoader = dataBaseConnectProperties.getClassLoader();
            String driverClass = dataBaseConnectProperties.getDriver();
            String url = dataBaseConnectProperties.getUrl();
            if(classLoader != null){
                try {

                    Driver driver = (Driver) classLoader.findClass(driverClass).newInstance();
                    connection = driver.connect(url, properties);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new SQLException("无法获取连接，错误：" + e.getMessage());
                }
            }else{
                connection = DriverManager.getConnection(url, properties);
            }
        }
        return connection;
    }

    public void switchTo(Connection connection, String catalogName, String schemaName) throws SQLException {
        DataBaseType dataBaseType = getDataBaseConnectType();
        switch (dataBaseType){
            case SQLSERVER2000:
            case MSSQL:
                update(connection, "USE " + catalogName);
                update(connection, "EXECUTE as USER ='" + schemaName + "'");
                break;
            case MYSQL:
            case MYSQL5:
                update(connection, "USE " + catalogName);
                break;
            case ORACLE:
                update(connection, "ALTER SESSION SET CURRENT_SCHEMA = '" + schemaName +"'");
                break;
            case HIVE:
                update(connection, "USE " + catalogName);
                break;
//            case POSTGRESQL:
//                update(connection, "\\c " + catalogName );
//                update(connection, "set search_path to " + schemaName );
//                break;
            default:
                throw new RuntimeException("不支持的数据库类型，无法切换到：" + catalogName + " " + schemaName);
        }
    }

    public DataBaseType getDataBaseConnectType(){
        return dataBaseConnectProperties.getDataBaseType();
    }

    public boolean validate() throws SQLException {
        try(Connection connection = getConnection()){

            ResultSet resultSet = connection.prepareStatement(dataBaseConnectProperties.getValidQuery()).executeQuery();
            if(resultSet.next()){
                return true;
            }
            return false;

        } catch (SQLException e) {
            throw e;
        }
    }

    /**
     * 设置预处理参数的方法
     */
    public static void prepareParameters(PreparedStatement ps, Object... params) throws SQLException {
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                try {

                    Object param = params[i];
                    if(param instanceof String){
                        ps.setString(i + 1, (String) param);
                    } else if(param instanceof byte[]){
                        ps.setBytes(i + 1, (byte[]) param);
                    } else {
                        ps.setObject(i + 1, param);
                    }

                } catch (SQLException e) {
                    throw new SQLException("index: " + (i + 1) + " param: " + params[i] + " error: " + e.getMessage());
                }
            }
        }
    }

    public boolean test(){
        try{
            queryList(getConnection(), dataBaseConnectProperties.getValidQuery());
            return true;
        } catch (Exception e) {
            return false;
        }
    }



    public QueryBatch openQuery(String sql, Object... parameters) throws SQLException {
        Connection connection = getConnection();
        return openQuery(connection, sql, parameters);
    }

    public QueryBatch openQuery(Connection connection, String sql, Object... parameters) throws SQLException {
        // JDBC 流式读取
        PreparedStatement ps = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        prepareParameters(ps, parameters);

        if(DataBaseType.MYSQL.equals(getDataBaseConnectType()) || DataBaseType.MYSQL5.equals(getDataBaseConnectType())){
            ps.setFetchSize(Integer.MIN_VALUE);
        }else{
            ps.setFetchSize(0);
        }

        if(DataBaseType.POSTGRESQL.equals(getDataBaseConnectType())){
            connection.setAutoCommit(false);
            ps.setFetchSize(50);
        }

        ResultSet rs = ps.executeQuery();

        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        QueryBatch queryBatch = new QueryBatch();
        queryBatch.setConnection(connection);
        queryBatch.setPs(ps);
        queryBatch.setRs(rs);
        queryBatch.setColumnCount(columnCount);
        queryBatch.setEncoding(dataBaseConnectProperties.getServerEncoding(), dataBaseConnectProperties.getClientEncoding());

        return queryBatch;
    }


    public <T> T queryOne(Class<T> clazz, String sql, Object... parameters) throws SQLException, IllegalAccessException, InstantiationException, OrmException {
        try(Connection connection = getConnection()){
            return queryOne(clazz, connection, sql, parameters);
        } catch (SQLException e) {
            throw e;
        } catch (OrmException e) {
            throw e;
        }
    }


    public <T> T queryOne(Class<T> clazz, Connection connection, String sql, Object... parameters) throws SQLException, IllegalAccessException, InstantiationException, OrmException {
        PreparedStatement ps = connection.prepareStatement(sql);
        prepareParameters(ps, parameters);

        ResultSet rs = ps.executeQuery();
        ResultSetMetaData metaData = rs.getMetaData();
        T t = null;
        if (rs.next()) {
            t = BeanConversion.convert(clazz, metaData, rs, dataBaseConnectProperties);
        }
        return t;
    }


    public DataRecord queryOne(String sql, Object... parameters) throws SQLException {
        try(Connection connection = getConnection()){
            return queryOne(connection, sql, parameters);
        } catch (SQLException e) {
            throw e;
        }
    }

    public DataRecord queryOne(Connection connection, String sql, Object... parameters) throws SQLException {
        List<DataRecord> list = queryList(connection, sql, parameters);
        return list.size() > 0 ? list.get(0) : null;
    }

    public <T> List<T> queryList(Class<T> clazz,String sql, Object... parameters) throws SQLException, IllegalAccessException, OrmException, InstantiationException {
        try(Connection connection = getConnection()){
            return queryList(clazz, connection, sql, parameters);
        } catch (SQLException e) {
            throw e;
        }
    }

    public <T> List<T> queryList(Class<T> clazz, Connection connection, String sql, Object... parameters) throws SQLException, IllegalAccessException, InstantiationException, OrmException {
        PreparedStatement ps = connection.prepareStatement(sql);
        prepareParameters(ps, parameters);

        ResultSet rs = ps.executeQuery();
        ResultSetMetaData metaData = rs.getMetaData();

        int rows = 0;
        List<T> list = new ArrayList<>();
        while (rs.next()) {
            rows++;
            T t = BeanConversion.convert(clazz, metaData, rs, dataBaseConnectProperties);
            list.add(t);
        }
        return list;
    }

    public List<Object[]> queryArray(String sql, Object... parameters) throws SQLException {
        try(Connection connection = getConnection()){
            List<Object[]> list = queryArray(connection, sql, parameters);
            return list;
        } catch (SQLException e) {
            throw e;
        }
    }

    public static List<Object[]> queryArray(Connection connection, String sql, Object... parameters) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(sql);
        prepareParameters(ps, parameters);

        ResultSet rs = ps.executeQuery();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        List<Object[]> result = new ArrayList<>();
        while (rs.next()) {
            Object[] row = new Object[columnCount];
            for(int i = 0; i < columnCount; i++) {
                Object o = rs.getObject(i + 1);
                row[i] = o;
            }
            result.add(row);
        }
        return result;
    }

    public List<DataRecord> queryList(String sql, Object... parameters) throws SQLException {
        try(Connection connection = getConnection()){
            List<DataRecord> list = queryList(connection, sql, parameters);

            // 字符串转码
            serverEncoding(list, dataBaseConnectProperties);

            return list;
        } catch (SQLException e) {
            throw e;
        }
    }

    public static List<DataRecord> queryList(Connection connection, String sql, Object... parameters) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(sql);
        prepareParameters(ps, parameters);

        ResultSet rs = ps.executeQuery();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();



        Map<String,Integer> nameMap = new HashMap<>();
        for (int i = 0; i < columnCount; i++) {
            String label = metaData.getColumnLabel(i + 1);
            nameMap.put(label, i);
        }

        List<DataRecord> result = new ArrayList<>();
        while (rs.next()) {
            result.add(toRecord(rs, columnCount, nameMap));
        }
        return result;
    }

    public ArrayListWithMeta<DataRecord> queryListWithMeta(String sql, Object... parameters) throws SQLException {
        try(Connection connection = getConnection()){
            return queryListWithMeta(connection, sql, parameters);
        } catch (SQLException e) {
            throw e;
        }
    }

    public ArrayListWithMeta<DataRecord> queryListWithMeta(Connection connection, String sql, Object... parameters) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(sql);
        prepareParameters(ps, parameters);


        ArrayListWithMeta<DataRecord> arrayListWithMeta = new ArrayListWithMeta<>();

        ResultSet rs = ps.executeQuery();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();


        Map<String,Integer> nameMap = new HashMap<>();
        for(int i = 0; i < columnCount; i++){
            String label = metaData.getColumnLabel(i + 1);
            nameMap.put(label, i);

            ColumnMeta columnMeta = ColumnMeta.getFromResultSet(metaData, i +1, null);
            arrayListWithMeta.addColumnMeta(columnMeta);
        }


        while (rs.next()) {
            arrayListWithMeta.add(toRecord(rs, columnCount, nameMap));
        }

        // 字符串转码
        serverEncoding(arrayListWithMeta, dataBaseConnectProperties);

        return arrayListWithMeta;
    }

    private void serverEncoding(List<DataRecord> list, DataBaseConnectPropertes dataBaseConnectProperties) throws SQLException {
        String clientEncoding = dataBaseConnectProperties.getClientEncoding();
        String serverEncoding = dataBaseConnectProperties.getServerEncoding();
        if(clientEncoding == null || serverEncoding == null || list == null){
            return;
        }
        for (DataRecord dataRecord : list) {
            for (int i = 0; i < dataRecord.size(); i++) {
                Object o = dataRecord.get(i);
                if(o instanceof String){
                    try {
                        o = new String(((String) o).getBytes(serverEncoding), clientEncoding);
                        dataRecord.put(i, o);
                    } catch (UnsupportedEncodingException e) {
                        throw new SQLException("unsupport encoding : " + e.getMessage());
                    }
                }
            }
        }
    }

    private static DataRecord toRecord(ResultSet rs, int columnCount, Map<String,Integer> nameMap) throws SQLException {
        Object[] row = new Object[columnCount];
        for(int i = 0; i < columnCount; i++) {
            Object o = rs.getObject(i + 1);
            row[i] = o;
        }
        DataRecord record = new DataRecord(row, nameMap);
        return record;
    }

    public <T> T insertReturnPrimaryKey(String sql, Object...parameters) throws SQLException {
        try(Connection connection = getConnection()){
            return insertReturnPrimaryKey(connection, sql, parameters);
        } catch (SQLException e) {
            throw e;
        }
    }

    public static <T> T insertReturnPrimaryKey(Connection connection, String sql, Object...parameters) throws SQLException {
        try(PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            prepareParameters(ps, parameters);
            ps.executeUpdate();

            ResultSet generatedKeys = ps.getGeneratedKeys();
            while (generatedKeys.next()) {
                T generateKey = (T)generatedKeys.getObject(1);
                return generateKey;
            }
            return null;
        } catch (SQLException e) {
            throw e;
        }
    }


    public int insert(String sql, Object...parameters) throws SQLException {
        try(Connection connection = getConnection()){
            return insert(connection, sql, parameters);
        } catch (SQLException e) {
            throw e;
        }
    }

    public static int insert(Connection connection, String sql, Object...parameters) throws SQLException {
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            prepareParameters(ps, parameters);
            ps.executeUpdate();
            return 1;
        } catch (SQLException e) {
            throw e;
        }
    }

    public int insert(Object obj, String... ignoreColumns) throws SQLException {

        String tableName = BeanConversion.getTableAnnotation(obj.getClass());
        FieldDefines fieldDefines = BeanConversion.getColumnAnnotation(obj.getClass());


        int size = fieldDefines.size();

        Set<String> keyset = fieldDefines.keySet();

        StringBuffer prefix = new StringBuffer();
        StringBuffer suffix = new StringBuffer();

        Set<String> ignoreColumnSet = getIgnoreColumnSet(fieldDefines, ignoreColumns);
        Object[] row = new Object[size - ignoreColumnSet.size()];

        int i = -1;
        for(String key : keyset){

            if(ignoreColumnSet.contains(key)){
                continue;
            }

            i++;
            prefix.append(key).append(",");
            suffix.append("?").append(",");

            try {
                row[i] = fieldDefines.getValue(obj, key);
            } catch (IllegalAccessException e) {
                throw new SQLException("object get " + key + " value error: " + e.getMessage());
            }
        }

        String insertSQL = "insert into " + tableName + "(" + prefix.substring(0, prefix.length() - 1) + ") values (" + suffix.substring(0, suffix.length() - 1) + ")";

        System.out.println(insertSQL);

        return insert(insertSQL, row);
    }

    public <T> T insertReturnPrimaryKey(Object obj, String... ignoreColumns) throws SQLException {
        try(Connection connection = getConnection()){
            return insertReturnPrimaryKey(connection, obj, ignoreColumns);
        } catch (SQLException e) {
            throw e;
        }
    }

    private static Set<String> getIgnoreColumnSet(FieldDefines fieldDefines, String... ignoreColumns){
        Set<String> ignoreColumnSet = new HashSet<>();
        if(ignoreColumns != null){
            for (String ignoreColumn : ignoreColumns) {

                if(fieldDefines.get(ignoreColumn) == null){
                    continue;
                }

                if(!fieldDefines.isCaseSensitive()){
                    ignoreColumnSet.add(ignoreColumn.toUpperCase());
                }else{
                    ignoreColumnSet.add(ignoreColumn);
                }
            }
        }
        return ignoreColumnSet;

    }

    public static <T> T insertReturnPrimaryKey(Connection connection, Object obj, String... ignoreColumns) throws SQLException {

        String tableName = BeanConversion.getTableAnnotation(obj.getClass());
        FieldDefines fieldDefines = BeanConversion.getColumnAnnotation(obj.getClass());

        int size = fieldDefines.size();

        Set<String> keyset = fieldDefines.keySet();
        StringBuffer prefix = new StringBuffer();
        StringBuffer suffix = new StringBuffer();

        Set<String> ignoreColumnSet = getIgnoreColumnSet(fieldDefines, ignoreColumns);
        Object[] row = new Object[size - ignoreColumnSet.size()];

        int i = -1;
        for(String key : keyset){

            if(ignoreColumnSet.contains(key)){
                continue;
            }

            i++;
            prefix.append(key).append(",");
            suffix.append("?").append(",");

            try {
                row[i] = fieldDefines.getValue(obj, key);
            } catch (IllegalAccessException e) {
                throw new SQLException("object get " + key + " value error: " + e.getMessage());
            }
        }

        String insertSQL = "insert into " + tableName + "(" + prefix.substring(0, prefix.length() - 1) + ") values (" + suffix.substring(0, suffix.length() - 1) + ")";

        try(PreparedStatement ps = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)){
            prepareParameters(ps, row);
            ps.executeUpdate();

            ResultSet generatedKeys = ps.getGeneratedKeys();
            while (generatedKeys.next()) {
                T generateKey = (T)generatedKeys.getObject(1);
                return generateKey;
            }
            return null;
        } catch (SQLException e) {
            throw e;
        }
    }


    public static int update(Connection connection, String sql, Object... parameters) throws SQLException {

        PreparedStatement ps = connection.prepareStatement(sql);
        try{
            prepareParameters(ps, parameters);
            return ps.executeUpdate();
        } catch (Exception ex){
            throw ex;
        } finally {
            if(ps != null) ps.close();
        }

    }

    public int update(String sql, Object... parameters) throws SQLException {
        try(Connection connection = getConnection()){
            return update(connection, sql, parameters);
        } catch (SQLException e) {
            throw e;
        }
    }

    public void updateBatch(String sql, List<Object[]> data) throws SQLException {
        try(Connection connection = getConnection()){
            updateBatch(connection, sql, data);
        } catch (SQLException e) {
            throw e;
        }
    }
    public static void updateBatch(Connection connection, String sql, List<Object[]> data) throws SQLException {
        PreparedStatement ps = null;
        try{
            connection.setAutoCommit(false);
            ps = connection.prepareStatement(sql);

            for(Object[] d : data){
                prepareParameters(ps, d);
                ps.addBatch();
            }

            ps.executeBatch();
            connection.commit();
        } catch (SQLException | RuntimeException e) {
            if(connection != null){
                try{
                    connection.rollback();
                }catch (SQLException re){}
            }
            throw e;
        }finally {
            if(connection != null){
                connection.setAutoCommit(true);
            }
            if(ps != null){
                try {
                    ps.close();
                }catch (Exception ex){ }
            }
        }
    }

    public UpdateBatch openUpdate(String sql) throws SQLException {
        Connection connection = getConnection();
        return openUpdate(connection, sql);
    }

    public UpdateBatch openUpdate(Connection connection, String sql) throws SQLException {

        UpdateBatch updateBatch = new UpdateBatch();
        updateBatch.setConnection(connection);
        updateBatch.setUpdateSQL(sql);

        return updateBatch;
    }

    public long count(String sql, Object... parameters) throws SQLException {
        try(Connection connection = getConnection()){
            return count(connection, sql, parameters);
        } catch (SQLException e) {
            throw e;
        }
    }

    public long count(Connection connection, String sql, Object... parameters) throws SQLException {
        DataRecord dataRecord = queryOne("select count(1) TEMP_CNT from ( \n" + sql + "\n ) TEMP_TB1", parameters);
        Object cnt =  dataRecord.get(0);
        if(cnt == null){
            throw new SQLException("无法构建count语句查询数量");
        }
        return Long.parseLong(cnt.toString());
    }
}

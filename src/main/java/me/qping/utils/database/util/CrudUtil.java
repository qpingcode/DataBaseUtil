package me.qping.utils.database.util;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.Data;
import me.qping.utils.database.bean.BeanConversion;
import me.qping.utils.database.bean.FieldDefines;
import me.qping.utils.database.connect.DataBaseConnectPropertes;
import me.qping.utils.database.connect.DataBaseType;
import me.qping.utils.database.dialect.DataBaseDialect;
import me.qping.utils.database.exception.OrmException;

import javax.sql.DataSource;
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

    protected DataBaseDialect dataBaseDialect;

    protected DataBaseConnectPropertes dataBaseConnectProperties;

    protected DataSource dataSource;

    public void close(){
        if(dataSource != null && dataSource instanceof DruidDataSource){
            DruidDataSource druidDataSource = (DruidDataSource) dataSource;
            druidDataSource.close();
        }
    }

    public Connection getConnection() throws SQLException {
        Connection connection = null;
        if(dataSource != null){
            connection = dataSource.getConnection();
        }else{
            connection = DriverManager.getConnection(
                    dataBaseConnectProperties.getUrl(),
                    dataBaseConnectProperties.getUsername(),
                    dataBaseConnectProperties.getPassword()
            );
        }
        return connection;
    }

    public void switchTo(Connection connection, String catalogName, String schemaName) throws SQLException {
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
//            case POSTGRESQL:
//                update(connection, "\\c " + catalogName );
//                update(connection, "set search_path to " + schemaName );
//                break;
            default:
                throw new RuntimeException("不支持的数据库类型，无法切换到：" + catalogName + " " + schemaName);
        }
    }

    public DataBaseDialect getDataBaseDialect(){
        return dataBaseDialect;
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
    public void prepareParameters(PreparedStatement ps, Object... params) throws SQLException {
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                try {
                    ps.setObject(i + 1, params[i]);
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

        if(DataBaseType.MYSQL.equals(getDataBaseConnectType())){
            ps.setFetchSize(Integer.MIN_VALUE);
        }else{
            ps.setFetchSize(0);
        }

        ResultSet rs = ps.executeQuery();

        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        QueryBatch queryBatch = new QueryBatch();
        queryBatch.setConnection(connection);
        queryBatch.setPs(ps);
        queryBatch.setRs(rs);
        queryBatch.setColumnCount(columnCount);

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

//            rows++;
//            if(rows > 1){
//                throw new SQLException("queryOne get rows more that 1!");
//            }
            t = BeanConversion.convert(clazz, metaData, rs);
        }
        return t;
    }


    public Map<String, Object> queryOne(String sql, Object... parameters) throws SQLException {
        try(Connection connection = getConnection()){
            return queryOne(connection, sql, parameters);
        } catch (SQLException e) {
            throw e;
        }
    }

    public Map<String, Object> queryOne(Connection connection, String sql, Object... parameters) throws SQLException {

        PreparedStatement ps = connection.prepareStatement(sql);
        prepareParameters(ps, parameters);

        ResultSet rs = ps.executeQuery();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        Map<String, Object> result = new HashMap<>();

        if (rs.next()) {
            for (int i = 0; i < columnCount; i++) {
                String label = metaData.getColumnLabel(i + 1);
                result.put(label, rs.getObject(label));
            }
        }

        return result;
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
            T t = BeanConversion.convert(clazz, metaData, rs);
            list.add(t);
        }
        return list;
    }

    public List<Map<String, Object>> queryList(String sql, Object... parameters) throws SQLException {
        try(Connection connection = getConnection()){
            return queryList(connection, sql, parameters);
        } catch (SQLException e) {
            throw e;
        }
    }

    public List<Map<String, Object>> queryList(Connection connection, String sql, Object... parameters) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(sql);
        prepareParameters(ps, parameters);

        ResultSet rs = ps.executeQuery();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        List<Map<String, Object>> result = new ArrayList<>();

        while (rs.next()) {
            Map<String, Object> map = new HashMap<String, Object>();
            for (int i = 0; i < columnCount; i++) {
                String label = metaData.getColumnLabel(i + 1);
                map.put(label, rs.getObject(label));
            }
            result.add(map);
        }
        return result;
    }

    public List<Object[]> queryArray(String sql, Object... parameters) throws SQLException {
        try(Connection connection = getConnection()){
            return queryArray(connection, sql, parameters);
        } catch (SQLException e) {
            throw e;
        }
    }

    public List<Object[]> queryArray(Connection connection, String sql, Object... parameters) throws SQLException {

        PreparedStatement ps = connection.prepareStatement(sql);
        prepareParameters(ps, parameters);

        ResultSet rs = ps.executeQuery();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        List<Object[]> result = new ArrayList<>();

        while (rs.next()) {
            Object[] values = new Object[columnCount];
            for (int i = 0; i < columnCount; i++) {
                values[i] = rs.getObject(i + 1);
            }
            result.add(values);
        }
        return result;
    }

    public int insertReturnPrimaryKey(String sql, Object...parameters) throws SQLException {
        try(Connection connection = getConnection()){
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            prepareParameters(ps, parameters);
            ps.executeUpdate();

            ResultSet generatedKeys = ps.getGeneratedKeys();
            while (generatedKeys.next()) {
                Integer generateKey = generatedKeys.getInt(1);
                return generateKey;
            }
            return -1;
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

    public int insert(Connection connection, String sql, Object...parameters) throws SQLException {
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            prepareParameters(ps, parameters);
            ps.executeUpdate();
            return 1;
        } catch (SQLException e) {
            throw e;
        }
    }

    public int insert(Object obj) throws SQLException {

        String tableName = BeanConversion.getTableAnnotation(obj.getClass());
        FieldDefines fieldDefines = BeanConversion.getColumnAnnotation(obj.getClass());


        int size = fieldDefines.size();

        if(size == 0) return 0;

        Set<String> keyset = fieldDefines.keySet();
        Object[] row = new Object[size];
        StringBuffer prefix = new StringBuffer();
        StringBuffer suffix = new StringBuffer();

        int i = -1;
        for(String key : keyset){
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

    public int update(Connection connection, String sql, Object... parameters) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(sql);
        prepareParameters(ps, parameters);
        return ps.executeUpdate();
    }

    public int update(String sql, Object... parameters) throws SQLException {
        try(Connection connection = getConnection()){
            return update(connection, sql, parameters);
        } catch (SQLException e) {
            throw e;
        }
    }

    public void updateBatch(String sql, List<Object[]> data) throws SQLException {

        Connection connection = null;
        PreparedStatement ps = null;
        try{
            connection = getConnection();
            connection.setAutoCommit(false);

            ps = connection.prepareStatement(sql);

            for(Object[] d : data){
                prepareParameters(ps, d);
                ps.addBatch();
            }

            ps.executeBatch();
            connection.commit();
            connection.setAutoCommit(true);

        } catch (SQLException | RuntimeException e) {

            if(connection != null){
                try{
                    connection.rollback();
                }catch (SQLException re){}

                connection.setAutoCommit(true);
            }

            throw e;
        }finally {
            if(ps != null){
                try {
                    ps.close();
                }catch (Exception ex){ }
            }

            if(connection != null){
                try {
                    connection.close();
                }catch (Exception ex){ }
            }
        }
    }

}

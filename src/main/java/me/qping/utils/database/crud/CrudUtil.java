package me.qping.utils.database.crud;

import lombok.Data;
import me.qping.utils.database.connect.DataBaseConnectPropertes;
import me.qping.utils.database.connect.DataBaseType;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    protected Connection getConnection() throws SQLException {

        if(dataSource != null){
            return dataSource.getConnection();
        }else{
            Connection connection = DriverManager.getConnection(
                    dataBaseConnectProperties.getUrl(),
                    dataBaseConnectProperties.getUsername(),
                    dataBaseConnectProperties.getPassword()
            );
            return connection;
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

    public Map<String, Object> queryOne(String sql, Object... paramters) throws SQLException {
        Map<String, Object> result = new HashMap<>();

        try(Connection connection = getConnection()){
            PreparedStatement ps = connection.prepareStatement(sql);
            prepareParameters(ps, paramters);

            ResultSet rs = ps.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            int rows = 0;
            while (rs.next()) {

                rows++;
                if(rows > 1){
                    throw new SQLException("queryOne get rows more that 1!");
                }

                for (int i = 0; i < columnCount; i++) {
                    String label = metaData.getColumnLabel(i + 1);
                    result.put(label, rs.getObject(label));
                }
            }
        } catch (SQLException e) {
            throw e;
        }

        return result;
    }

    public List<Map<String, Object>> queryList(String sql, Object... paramters) throws SQLException {
        List<Map<String, Object>> result = new ArrayList<>();

        try(Connection connection = getConnection()){
            PreparedStatement ps = connection.prepareStatement(sql);
            prepareParameters(ps, paramters);

            ResultSet rs = ps.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();


            while (rs.next()) {
                Map<String, Object> map = new HashMap<String, Object>();
                for (int i = 0; i < columnCount; i++) {
                    String label = metaData.getColumnLabel(i + 1);
                    map.put(label, rs.getObject(label));
                }
                result.add(map);
            }
        } catch (SQLException e) {
            throw e;
        }

        return result;
    }

    public List<Object[]> queryArray(String sql, Object... paramters) throws SQLException {
        List<Object[]> result = new ArrayList<>();

        try(Connection connection = getConnection()){
            PreparedStatement ps = connection.prepareStatement(sql);
            prepareParameters(ps, paramters);

            ResultSet rs = ps.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Object[] values = new Object[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    values[i] = rs.getObject(i + 1);
                }
                result.add(values);
            }
        } catch (SQLException e) {
            throw e;
        }

        return result;
    }

    public int insertReturnPrimaryKey(String sql, Object...paramters) throws SQLException {
        try(Connection connection = getConnection()){
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            prepareParameters(ps, paramters);
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

    public int insert(Connection connection, String sql, Object...paramters) throws SQLException {
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            prepareParameters(ps, paramters);
            ps.executeUpdate();
            return 1;
        } catch (SQLException e) {
            throw e;
        }
    }

    public int insert(String sql, Object...paramters) throws SQLException {
        try(Connection connection = getConnection()){
            PreparedStatement ps = connection.prepareStatement(sql);
            prepareParameters(ps, paramters);
            ps.executeUpdate();
            return 1;
        } catch (SQLException e) {
            throw e;
        }
    }

    public int update(String sql, Object... paramters) throws SQLException {
        try(Connection connection = getConnection()){
            PreparedStatement ps = connection.prepareStatement(sql);
            prepareParameters(ps, paramters);
            return ps.executeUpdate();
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

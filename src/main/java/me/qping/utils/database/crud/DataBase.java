package me.qping.utils.database.crud;

import lombok.Data;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

/**
 * @ClassName DataBase
 * @Description jdbc 简单封装
 * @Author qping
 * @Date 2019/6/14 15:51
 * @Version 1.0
 **/
@Data
public class DataBase {

    DataBaseConnectType dataBaseConnectType;

    DataSource dataSource;


    public Connection getConnection() throws SQLException {


        if(dataSource != null){
            return dataSource.getConnection();
        }else{
            Connection connection = DriverManager.getConnection(dataBaseConnectType.getUrl(), dataBaseConnectType.getUsername(), dataBaseConnectType.getPassword());
            return connection;
        }
    }

    public static DataBaseBuilder builder() {
        DataBaseBuilder builder = new DataBaseBuilder();
        return builder;
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
        try(Connection connection = getConnection()){
            connection.setAutoCommit(false);
            PreparedStatement ps = connection.prepareStatement(sql);

            for(Object[] d : data){
                prepareParameters(ps, d);
                ps.addBatch();
            }

            ps.executeBatch();
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw e;
        }
    }

}

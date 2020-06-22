package me.qping.utils.database.util;

import lombok.Data;
import org.apache.ibatis.jdbc.SQL;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 返回一个resultset的封装结构，用于一行一行获取数据
 */
@Data
public class QueryBatch {

    int columnCount;
    Connection connection;
    PreparedStatement ps;
    ResultSet rs;

    int count;

    public boolean next() throws SQLException {
        boolean flag = rs.next();
        if(flag){
            count++;
        }
        return flag;
    }

    public Map<String,Object> get() throws SQLException{
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        Map<String, Object> result = new HashMap<>();
        for (int i = 0; i < columnCount; i++) {
            String label = metaData.getColumnLabel(i + 1);
            result.put(label, rs.getObject(label));
        }
        return result;
    }

    public Object[] getArray() throws SQLException {
        Object[] values = new Object[columnCount];
        for (int i = 0; i < columnCount; i++) {
            values[i] = rs.getObject(i + 1);
        }
        return values;
    }

    public void close(){
        if(rs != null) {
            try{
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

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

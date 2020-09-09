package me.qping.utils.database.util;

import lombok.Data;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 返回一个resultset的封装结构，用于一行一行获取数据
 */
@Data
public class UpdateBatch {

    Connection connection;
    String updateSQL;
    int batchSize = 1000;
    List<Object[]> batchRows = new ArrayList<>();

    public void setConnection(Connection connection) throws SQLException{
        this.connection = connection;
        connection.setAutoCommit(false);
    }


    public void add(Object[] row) throws SQLException {
        batchRows.add(row);
        if(batchRows.size() >= batchSize){
            flush();
        }
    }

    public void flush() throws SQLException {
        if(batchRows.size() > 0){
            CrudUtil.updateBatch(connection, updateSQL, batchRows);
            batchRows.clear();
        }
    }

    public void close(){
        if(connection != null){
            try {
                flush();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try {
                connection.setAutoCommit(true);
                connection.close();
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }


}

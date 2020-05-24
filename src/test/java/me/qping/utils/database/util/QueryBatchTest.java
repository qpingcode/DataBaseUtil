package me.qping.utils.database.util;

import me.qping.utils.database.DataBaseUtilBuilder;
import me.qping.utils.database.metadata.bean.TableMeta;
import org.junit.Test;

import java.sql.*;

public class QueryBatchTest {


    @Test
    public void getTableInfo() {

        long begin = System.currentTimeMillis();
        try {
            MetaDataUtil crud = DataBaseUtilBuilder.init(
                    "jdbc:mysql://192.168.80.20:30306/data_luhe_origin?useUnicode=true&characterEncoding=UTF-8&tinyInt1isBit=false",
                    "root",
                    "rxthinkingmysql"
            ).build();

            String sql = "select * from MEDICALRECORD";

            Connection connection = crud.getConnection();

            PreparedStatement ps = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            ps.setFetchSize(Integer.MIN_VALUE);

//            PreparedStatement ps = connection.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            for(int i = 0; i<100;i ++){
                if(rs.next()){
                    System.out.println(rs.getObject(1));
                }
            }

            System.out.println( (System.currentTimeMillis() - begin) / 1000 + "s" );
            rs.close();
            ps.close();
            connection.close();
            System.out.println( (System.currentTimeMillis() - begin) / 1000 + "s" );

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}

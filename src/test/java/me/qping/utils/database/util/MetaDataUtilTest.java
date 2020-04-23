package me.qping.utils.database.util;

import me.qping.utils.database.DataBaseUtilBuilder;
import me.qping.utils.database.metadata.bean.ResultSetColumnMeta;
import me.qping.utils.database.metadata.bean.TableMeta;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;

public class MetaDataUtilTest {

    @Test
    public void getTableInfo() {

        try {
            MetaDataUtil crud = DataBaseUtilBuilder.init(
                    "jdbc:mysql://192.168.80.20:30306/data_transform?useUnicode=true&characterEncoding=UTF-8&tinyInt1isBit=false",
                    "root",
                    "rxthinkingmysql"
            ).build();

            TableMeta tableInfo = crud.getTableInfo("dt_category");

            System.out.println(tableInfo.getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testQueryColumnMeta() {

        try {
            MetaDataUtil crud = DataBaseUtilBuilder.init(
                    "jdbc:mysql://192.168.80.20:30306/data_transform_platform?useUnicode=true&characterEncoding=UTF-8&tinyInt1isBit=false",
                    "root",
                    "rxthinkingmysql"
            ).build();

            List<ResultSetColumnMeta> tableInfo = crud.queryColumnMeta(null, null, "select * from PERSONALINFORMATION", null);

            System.out.println(tableInfo);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
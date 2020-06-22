package me.qping.utils.database.util;

import me.qping.utils.database.DataBaseUtilBuilder;
import me.qping.utils.database.metadata.bean.ResultSetColumnMeta;
import me.qping.utils.database.metadata.bean.TableMeta;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class PostgreTest {

    @Test
    public void conn() {

        try {
            MetaDataUtil crud = DataBaseUtilBuilder.init(
                    "jdbc:postgresql://localhost:5432/test",
                    "postgres",
                    "123456"
            ).build();

            List<Map<String, Object>> data = crud.queryList("select * from tb_test");


            TableMeta tableInfo = crud.getTableInfo("tb_test");


            List<String> catalogs = crud.getCatalogs();
            List<String> schemas = crud.getSchemas("test");
            List<TableMeta> tables = crud.getTables("postgre","information_schema");
            System.out.println(tableInfo.getColumns().size());


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
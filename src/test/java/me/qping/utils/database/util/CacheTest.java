package me.qping.utils.database.util;

import me.qping.common.model.DataRecord;
import me.qping.utils.database.DataBaseUtilBuilder;
import me.qping.utils.database.metadata.bean.ResultSetColumnMeta;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class CacheTest {

    @Test
    public void conn() throws ClassNotFoundException, SQLException {


        Class.forName("com.intersys.jdbc.CacheDriver");
        Connection connection = DriverManager.getConnection(
                    "jdbc:Cache://10.10.10.101:1972/dhc-app",
                    "_system",
                    "SYS"
            );

        CrudUtil util = new CrudUtil();
        List<DataRecord> list = util.queryList(
                connection,
                "exec DHCDoc_Interface_Inside_IOT.BaseInfo_GetIPPatOrdMsg('2020-10-01','2020-10-01')"
        );

        System.out.println(list.size());

    }

}
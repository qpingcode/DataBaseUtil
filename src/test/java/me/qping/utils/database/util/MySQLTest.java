package me.qping.utils.database.util;

import me.qping.common.model.DataRecord;
import me.qping.utils.database.DataBaseUtilBuilder;
import me.qping.utils.database.connect.DataBaseType;
import me.qping.utils.database.metadata.bean.TableMeta;
import org.junit.Test;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

/**
 * @ClassName OracleTest
 * @Description TODO
 * @Author qping
 * @Date 2021/1/13 11:20
 * @Version 1.0
 **/
public class MySQLTest {


    public MetaDataUtil conn() throws ClassNotFoundException, SQLException {
        DataBaseUtilBuilder builder = DataBaseUtilBuilder.init(
                DataBaseType.MYSQL,
                "192.168.1.201",
                "30306",
                "data_transform",
                "root",
                "",
                true,
                null
        );

        MetaDataUtil metaDataUtil = builder.build();
        boolean flag = metaDataUtil.test();
        System.out.println(flag);

        return metaDataUtil;

    }

    @Test
    public void testClob() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        MetaDataUtil util = conn();

//        List<TestClob> data = util.queryList(TestClob.class,"select * from test_clob ");
        List<DataRecord> data = util.queryList("select * from test_code ");
        System.out.println(data);

        Date d =  new Date(new java.util.Date().getTime());

        java.sql.Time time = new java.sql.Time(new java.util.Date().getTime());

        String insertSQL = "insert into test_code(COL_TIME) values(?)";


        Connection conn = util.getConnection();
//        Blob blob = conn.cre
        util.update(insertSQL, time);



    }


}

package me.qping.utils.database.util;

import me.qping.common.model.DataRecord;
import me.qping.utils.database.DataBaseUtilBuilder;
import me.qping.utils.database.connect.DataBaseType;
import me.qping.utils.database.metadata.bean.ColumnMeta;
import me.qping.utils.database.metadata.bean.TableMeta;
import me.qping.utils.database.util.bean.oracle.TestClob;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.sql.*;
import java.util.List;

/**
 * @ClassName OracleTest
 * @Description TODO
 * @Author qping
 * @Date 2021/1/13 11:20
 * @Version 1.0
 **/
public class OracleTest {


    public MetaDataUtil conn() throws ClassNotFoundException, SQLException {
        DataBaseUtilBuilder builder = DataBaseUtilBuilder.init(
                DataBaseType.ORACLE,
                "192.168.1.112",
                "1521",
                "ORCL",
                "rxthinking",
                "rxthinking",
                true,
                null
        );

        MetaDataUtil metaDataUtil = builder.build();
        boolean flag = metaDataUtil.test();
        System.out.println(flag);

        return metaDataUtil;

    }

    @Test
    public void testTableMeta() throws SQLException, ClassNotFoundException {
        MetaDataUtil util = conn();
        TableMeta table = util.getTableInfo("test_clob");
        System.out.println(table);
    }

    @Test
    public void testClob() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        MetaDataUtil util = conn();

//        List<TestClob> data = util.queryList(TestClob.class,"select * from test_clob ");
        List<DataRecord> data = util.queryList("select * from test_clob ");
        System.out.println(data);

        Date d =  new java.sql.Date(new java.util.Date().getTime());

        Clob clob = (Clob) data.get(0).get("LOG_B");


        String insertSQL = "insert into test_clob(COL_BLOB) values(?)";


        Connection conn = util.getConnection();
//        Blob blob = conn.cre
        PreparedStatement ps = conn.prepareStatement(insertSQL);

        String str = "1234567中文啊aa";
        ps.setBlob(1, new ByteArrayInputStream(str.getBytes()), str.getBytes().length);

        ps.executeUpdate();

    }


}

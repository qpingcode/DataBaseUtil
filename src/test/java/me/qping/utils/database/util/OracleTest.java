package me.qping.utils.database.util;

import me.qping.common.model.DataRecord;
import me.qping.utils.database.DataBaseUtilBuilder;
import me.qping.utils.database.connect.DataBaseType;
import me.qping.utils.database.metadata.bean.TableMeta;
import org.junit.Test;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
    public void testClob() throws Exception {

        MetaDataUtil util = conn();

//        List<DataRecord> data = util.queryList("select * from test_clob ");


        QueryBatch batchQuery = util.openQuery("select * from test_clob ");
        boolean has = batchQuery.next();
        DataRecord data =  batchQuery.get();

        System.out.println(data);

        Clob clob = (Clob) data.get("COL_CLOB");


        String insertSQL = "insert into test_clob(COL_BLOB) values(?)";

        Connection connection = util.getConnection();
        PreparedStatement ps = connection.prepareStatement(insertSQL);

//        Clob newClob = (Clob) createOracleLob(connection,  "oracle.sql.CLOB");
//        oracleStr2Clob(str, newClob);
//        ps.setClob(1, newClob);
//        ps.executeUpdate();


        ps.setBytes(1, clob.getSubString(1, (int) clob.length()).getBytes("GBK"));
        ps.executeUpdate();


    }



}

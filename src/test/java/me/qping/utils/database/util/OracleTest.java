package me.qping.utils.database.util;

import me.qping.common.model.DataRecord;
import me.qping.utils.database.DataBaseUtilBuilder;
import me.qping.utils.database.connect.DataBaseType;
import me.qping.utils.database.util.bean.oracle.TestClob;
import org.junit.Test;

import java.sql.SQLException;
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
    public void testClob() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        MetaDataUtil util = conn();

        List<TestClob> data = util.queryList(TestClob.class,"select * from TEST.test_clob ");

        System.out.println(data);


    }


}

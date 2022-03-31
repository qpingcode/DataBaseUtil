package me.qping.utils.database.util;

import me.qping.common.model.DataRecord;
import me.qping.utils.database.DataBaseUtilBuilder;
import me.qping.utils.database.connect.DataBaseType;
import org.junit.Test;

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
public class MySQL5Test {


    public MetaDataUtil conn() throws ClassNotFoundException, SQLException {
        DataBaseUtilBuilder builder = DataBaseUtilBuilder.init(
                DataBaseType.MYSQL5,
                "localhost",
                "3309",
                "gjkh",
                "gjkh",
                "gjkh@2022",
                true,
                null
        );

        MetaDataUtil metaDataUtil = builder.build();
        boolean flag = metaDataUtil.test();
        System.out.println(flag);

        return metaDataUtil;

    }

    @Test
    public void testConn() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        MetaDataUtil util = conn();



    }


}

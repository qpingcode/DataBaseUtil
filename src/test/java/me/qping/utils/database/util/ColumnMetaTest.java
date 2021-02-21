package me.qping.utils.database.util;

import me.qping.utils.database.DataBaseUtilBuilder;
import me.qping.utils.database.connect.DataBaseType;
import me.qping.utils.database.metadata.bean.ColumnMeta;
import org.junit.Test;

import java.sql.*;
import java.util.List;

/**
 * @ClassName ColumnMetaTest
 * @Description TODO
 * @Author qping
 * @Date 2021/1/15 09:07
 * @Version 1.0
 **/
public class ColumnMetaTest {

    public MetaDataUtil mysql() throws ClassNotFoundException, SQLException {
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


    public MetaDataUtil oracle() throws ClassNotFoundException, SQLException {
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
    public void testGetFromSQL() throws SQLException, ClassNotFoundException {

        MetaDataUtil util = oracle();
        Connection connection = util.getConnection();
        List<ColumnMeta> list = util.queryColumnMeta(null, null, "select * from test_clob where id = ? ");

        System.out.println(list.size());

    }
}

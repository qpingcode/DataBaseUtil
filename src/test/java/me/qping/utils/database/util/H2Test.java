package me.qping.utils.database.util;

import me.qping.common.model.DataRecord;
import me.qping.utils.database.DataBaseUtilBuilder;
import me.qping.utils.database.connect.DataBaseType;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

/**
 * @ClassName SQLiteTest
 * @Description TODO
 * @Author qping
 * @Date 2021/7/9 11:54
 * @Version 1.0
 **/
public class H2Test {

    MetaDataUtil metaDataUtil;

    @Before
    public void conn() throws ClassNotFoundException, SQLException {

        DataBaseUtilBuilder builder = DataBaseUtilBuilder.init(
                DataBaseType.H2,
                null,
                null,
                "test",
                "sa",
                "",
                false,
                "~/.h2"
        );

        metaDataUtil = builder.build();
    }


    @Test
    public void testCRUD() throws SQLException, ClassNotFoundException {

//        metaDataUtil.update("create table test(name varchar(100))");
//        metaDataUtil.update("insert into test values('小王'),('效力')");
        List<DataRecord> dataRecords = metaDataUtil.queryList("select * from test limit 0,1");
        for (DataRecord dataRecord : dataRecords) {
            System.out.println(dataRecord);
        }
    }

    @Test
    public void testDialect() throws SQLException, ClassNotFoundException {

        List<DataRecord> dataRecords = metaDataUtil.queryList("show schemas");
        for (DataRecord dataRecord : dataRecords) {
            System.out.println(dataRecord);
        }
    }

}

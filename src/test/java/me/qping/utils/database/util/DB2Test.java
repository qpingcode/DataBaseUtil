package me.qping.utils.database.util;

import me.qping.common.model.DataRecord;
import me.qping.utils.database.DataBaseUtilBuilder;
import me.qping.utils.database.connect.DataBaseType;
import me.qping.utils.database.metadata.bean.TableMeta;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

/**
 * @ClassName DB2Test
 * @Description TODO
 * @Author qping
 * @Date 2021/2/20 17:20
 * @Version 1.0
 **/
public class DB2Test {

    public MetaDataUtil conn() throws ClassNotFoundException, SQLException {
        DataBaseUtilBuilder builder = DataBaseUtilBuilder.init(
                DataBaseType.DB2,
                "localhost",
                "50000",
                "testdb",
                "db2inst1",
                "mypass",
                true,
                null
        );

        MetaDataUtil metaDataUtil = builder.build();


        return metaDataUtil;

    }

    @Test
    public void testConnection() throws SQLException, ClassNotFoundException {

        MetaDataUtil metaDataUtil = conn();

        boolean f = metaDataUtil.test();
        System.out.println(f);

        DataRecord r = metaDataUtil.queryOne("SELECT current date FROM sysibm.sysdummy1");
        System.out.println(r.get(0));

    }

    @Test
    public void testGetTables() throws SQLException, ClassNotFoundException {

        MetaDataUtil meta = conn();

        List<TableMeta> a = meta.getTables();

        for(TableMeta t : a){
            System.out.println(a);
        }

    }

}

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
public class HiveTest {

    public MetaDataUtil conn() throws ClassNotFoundException, SQLException {


        DataBaseUtilBuilder builder = DataBaseUtilBuilder.init(
                "jdbc:hive2://192.168.101.166:10000/datahub",
                "hive",
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

        DataRecord r = metaDataUtil.queryOne("SELECT 1");
        System.out.println(r.get(0));

    }

    @Test
    public void testDialect() throws SQLException, ClassNotFoundException {

        MetaDataUtil meta = conn();

        List<String> schemas = meta.getSchemas(null);

        // dbs
        for(String d : schemas){
            System.out.println(d);
        }

        // tables
        List<TableMeta> tables = meta.getTables(null, "sys");
        for (TableMeta table : tables) {
            System.out.println(table);
        }

        TableMeta tableInfo = meta.getTableInfo(null, "sys", "table_params");
        System.out.println(tableInfo);


    }

    @Test
    public void testGetData() throws SQLException, ClassNotFoundException {
        MetaDataUtil meta = conn();
        String orginSQL = "select * from sys.table_params";

        String sql = meta.dataBaseConnectProperties.getDataBaseDialect().getPageSql(orginSQL, 10, 1);

        System.out.println(sql);


        List<DataRecord> dataRecords = meta.queryList(sql);
        for (DataRecord dataRecord : dataRecords) {
            System.out.println(dataRecord);
        }

    }

}

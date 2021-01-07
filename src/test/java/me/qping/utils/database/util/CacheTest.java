package me.qping.utils.database.util;

import me.qping.common.model.DataRecord;
import me.qping.utils.database.DataBaseUtilBuilder;
import me.qping.utils.database.connect.DataBaseType;
import me.qping.utils.database.metadata.bean.ResultSetColumnMeta;
import me.qping.utils.database.metadata.bean.TableMeta;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class CacheTest {

    @Test
    public void conn() throws ClassNotFoundException, SQLException {


//        Class.forName("com.intersys.jdbc.CacheDriver");
//        Connection connection = DriverManager.getConnection(
//                    "jdbc:Cache://127.0.0.1:1972/dhc-app",
//                    "_system",
//                    "SYS"
//            );
//
//        CrudUtil util = new CrudUtil();
//        List<DataRecord> list = util.queryList(
//                connection,
//                "exec DHCDoc_Interface_Inside_IOT.BaseInfo_GetIPPatOrdMsg('2020-10-01','2020-10-01')"
//        );
//
//
//        System.out.println(list.size());
//        System.out.println(list.get(0).toString());


        DataBaseUtilBuilder builder = DataBaseUtilBuilder.init(
                DataBaseType.INFOSYSCACHE,
                "127.0.0.1",
                "1972",
                "dhc-app",
                "_system",
                "SYS",
                true,
                null
        );

        MetaDataUtil metaDataUtil = builder.build();



        // 查询数据
//        List<DataRecord> list = metaDataUtil.queryList("exec DHCDoc_Interface_Inside_IOT.BaseInfo_GetIPPatOrdMsg('2020-10-01','2020-10-01')");
//        System.out.println(list.size());
//        for(DataRecord dataRecord : list){
//            System.out.println(dataRecord);
//        }
//
//        // 查询单表结构
//        TableMeta t = metaDataUtil.getTableInfo("t_user");
//        System.out.println(t);
//
//        // 查询所有的表
        List<String> databases = metaDataUtil.getCatalogs();
        for(String m : databases ){
            System.out.println(m);
        }
//
//        List<String> schemas = metaDataUtil.getSchemas("test");
//        for(String m : schemas ){
//            System.out.println(m);
//        }
//
        // 查询所有的表
//        List<TableMeta> tables = metaDataUtil.getTables();
//        for(TableMeta m : tables ){
//            System.out.println(m);
//        }
//
// TableMeta(databaseType=INFOSYSCACHE, catalog=null, schema=CHSS, name=DictHIVTest, nameLower=dicthivtest, alias=null, type=TABLE, comment=null, primaryKeys=null, columns=null)
        List<DataRecord> list2 = metaDataUtil.queryList("select top 2 * from CHSS.DIctHIVTest");
        for(DataRecord dataRecord : list2){
            System.out.println(dataRecord);
        }





    }

}
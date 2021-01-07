package me.qping.utils.database.util;

import me.qping.common.model.DataRecord;
import me.qping.utils.database.DataBaseUtilBuilder;
import me.qping.utils.database.connect.DataBaseType;
import me.qping.utils.database.metadata.bean.TableMeta;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class SQLServer2000Test {

    @Test
    public void conn() throws ClassNotFoundException, SQLException {

        DataBaseUtilBuilder builder = DataBaseUtilBuilder.init(
                DataBaseType.SQLSERVER2000,
                "192.168.1.110",
                "1433",
                "test",
                "sa",
                "123",
                true,
                "dbo"
        );

        MetaDataUtil metaDataUtil = builder.build();


//        // 查询数据
//        List<DataRecord> list = metaDataUtil.queryList("select * from t_user");
//
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
//        List<String> databases = metaDataUtil.getCatalogs();
//        for(String m : databases ){
//            System.out.println(m);
//        }
//
//        List<String> schemas = metaDataUtil.getSchemas("test");
//        for(String m : schemas ){
//            System.out.println(m);
//        }
//
//        // 查询所有的表
////        List<TableMeta> tables = metaDataUtil.getTables("test", "dbo");
//        List<TableMeta> tables = metaDataUtil.getTables();
//        for(TableMeta m : tables ){
//            System.out.println(m);
//        }
//
//
//        List<DataRecord> list = metaDataUtil.queryList("select top 2 * from t_user");
//        System.out.println(list.size());


        List<DataRecord> list2 = metaDataUtil.queryList("select * from (  select *, ROW_NUMBER() OVER (ORDER BY (select 0)) AS rn from (select * from t_user) tmp_0  ) as tmp_1 where rn > 2 and rn <= 4;");
        System.out.println(list2.size());



    }

}
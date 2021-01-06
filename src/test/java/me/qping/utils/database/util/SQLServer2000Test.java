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
                null
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

        // 查询所有的表
        List<TableMeta> tables = metaDataUtil.getTables();
        for(TableMeta m : tables ){
            System.out.println(m);
        }



    }

}
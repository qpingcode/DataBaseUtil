package me.qping.utils;

import me.qping.utils.database.crud.DataBase;
import me.qping.utils.database.metadata.MetaDataUtil;
import me.qping.utils.database.metadata.bean.TableMeta;

/**
 * @ClassName Test
 * @Description TODO
 * @Author qping
 * @Date 2019/9/28 10:12
 * @Version 1.0
 **/
public class Test {

    public static void main(String[] args) {

        String url = "jdbc:sqlserver://127.0.0.1:1434;DatabaseName=QZJ_INTERFACE_V30";
//            String url = "jdbc:sqlserver://192.168.1.111:1433;DatabaseName=V30";
        String username = "sa";
        String password = "123456";

        MetaDataUtil metaUtil = MetaDataUtil.builder().smartInit(url, username, password).build();


        String tableName = "TB_MZ_SFMXB";

        TableMeta meta = metaUtil.analyze(tableName); // , SqlHelper.getExcludeColumns(excludeStr)

        System.out.println(meta.createInsertSelectAs());



    }
}

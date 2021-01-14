package me.qping.utils.database.dialect.impl;

import me.qping.utils.database.connect.DataBaseConnectPropertes;
import me.qping.utils.database.dialect.DataBaseDialect;
import me.qping.utils.database.metadata.bean.FieldType;

import java.sql.*;
import java.util.*;

/**
 * @ClassName MySQLDialect
 * @Author qping
 * @Date 2019/8/3 22:07
 * @Version 1.0
 **/
public class MySQLDialect implements DataBaseDialect {

    @Override
    public String getCatalogQuery() {
        return "show databases";
    }

    @Override
    public String getSchemaQuery() {
        return null;
    }


    @Override
    public String getPageSql(String sql, int pageSize, int pageNum) {

        if(pageSize < 0){
            throw new RuntimeException("pageSize 不能小于 0 ");
        }

        int begin = pageSize * pageNum;
        int end = pageSize * pageNum + pageSize;

        if(pageNum <= 0|| pageSize == 0){
            return "select * from (\n" + sql + "\n) tmp_0 limit " + pageSize;
        }else{
            return "select * from (\n" + sql + "\n) tmp_0 limit " + pageSize + " offset " + begin;
        }
    }

    @Override
    public Properties getConnectionProperties(DataBaseConnectPropertes connectType) {
        /**
         * mysql  需要 useInformationSchema=true 和 remarks=true
         * oracle 需要 remarks=true
         * 来源： http://www.tinygroup.org/docs/6638819901697136844
         */
        Properties props = new Properties();
        props.setProperty("user", connectType.getUsername());
        props.setProperty("password", connectType.getPassword());
        props.setProperty("remarks", "true");               //设置可以获取remarks信息
        props.setProperty("useInformationSchema", "true");  //设置可以获取tables remarks信息
        return props;
    }

}

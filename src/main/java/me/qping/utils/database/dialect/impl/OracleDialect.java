package me.qping.utils.database.dialect.impl;

import me.qping.utils.database.connect.DataBaseConnectPropertes;
import me.qping.utils.database.dialect.DataBaseDialect;
import me.qping.utils.database.metadata.bean.FieldType;

import java.sql.*;
import java.util.*;

/**
 * @ClassName OracleDialect
 * @Author qping
 * @Date 2019/8/3 22:07
 * @Version 1.0
 **/
public class OracleDialect implements DataBaseDialect {

    @Override
    public String getCatalogQuery() {
        return null;
    }

    @Override
    public String getSchemaQuery() {
        return "select username from all_users order by username";
    } // oracle schema 等同于 user

    @Override
    public String getPageSql(String sql, int pageSize, int pageNum) {

        if(pageSize < 0){
            throw new RuntimeException("pageSize 不能小于 0 ");
        }

        int begin = pageSize * pageNum;
        int end = pageSize * pageNum + pageSize;

        if(pageNum <= 0 || pageSize == 0){
            return "select * from (\n" + sql + "\n) where rownum <= " + pageSize;
        }else{
            return "select * from (" +
                    "    select tmp_0.*, rownum as rn from (\n" + sql + "\n)  tmp_0 where rownum <= " + end +
                    " ) where rn > " + begin;
        }
    }


    @Override
    public Properties getConnectionProperties(DataBaseConnectPropertes connectType) {
        Properties props = new Properties();
        props.setProperty("user", connectType.getUsername());
        props.setProperty("password", connectType.getPassword());
        props.setProperty("remarks", "true");               //设置可以获取remarks信息
        return props;
    }

}

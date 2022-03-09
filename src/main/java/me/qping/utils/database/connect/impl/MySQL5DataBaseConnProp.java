package me.qping.utils.database.connect.impl;

import lombok.Data;
import me.qping.utils.database.connect.DataBaseDialect;
import me.qping.utils.database.connect.DataBaseType;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static me.qping.utils.database.connect.DataBaseType.MYSQL;
import static me.qping.utils.database.connect.DataBaseType.MYSQL5;

/**
 * @ClassName MySQLDataBaseConnProp
 * @Description mysql连接
 * @Author qping
 * @Date 2019/7/4 16:25
 * @Version 1.0
 **/
@Data
public class MySQL5DataBaseConnProp extends MySQLDataBaseConnProp {

    String driver = "com.mysql.jdbc.Driver";

    /**
     * Catalog和Schema都属于抽象概念，主要用来解决命名冲突问题
     * 数据库对象表的全限定名可表示为：Catalog.Schema.表名
     | 供应商        | Catalog支持                       | Schema支持                 |
     | ------------- | --------------------------------- | -------------------------- |
     | MySQL         | 数据库名                          | 不支持                     |
     * @return
     */
    public MySQL5DataBaseConnProp(String host, String port, String database, String username, String password) {
        super(host, port, database, username, password);
    }

    public MySQL5DataBaseConnProp(String url, String username, String password) {
        super(url, username, password);
    }

    @Override
    public DataBaseType getDataBaseType() {
        return MYSQL5;
    }


}

package me.qping.utils.database.dialect.impl;

import me.qping.utils.database.connect.DataBaseConnectPropertes;
import me.qping.utils.database.dialect.DataBaseDialect;
import me.qping.utils.database.metadata.bean.FieldType;

import java.sql.JDBCType;
import java.util.Properties;

/**
 * @ClassName SQLServer2000
 * @Author qping
 * @Date 2021/1/6 16:56
 * @Version 1.0
 **/
public class SQLServer2000Dialect  implements DataBaseDialect {

    @Override
    public String getCatalogQuery() {
        return "select name from master.dbo.SysDatabases where name not in ('master', 'model', 'msdb', 'tempdb')";
    }

    @Override
    public String getSchemaQuery() {
        return "SELECT TABLE_SCHEMA as name FROM INFORMATION_SCHEMA.TABLES  GROUP BY TABLE_SCHEMA";
    }

    @Override
    public String getPageSql(String sql, int pageSize, int pageNum) {

        if(pageSize < 0){
            throw new RuntimeException("pageSize 不能小于 0 ");
        }

        if(pageNum <= 0 || pageSize == 0){
            return "select top " + pageSize + " * from (\n" + sql + "\n) tmp_0";
        }else{
            throw new RuntimeException("SQLServer 2000 未定义分页方法");
        }
    }

    /**
     * sqlserver拿不到注释
     * @param connectType
     * @return
     */
    @Override
    public Properties getConnectionProperties(DataBaseConnectPropertes connectType) {
        Properties props = new Properties();
        props.setProperty("user", connectType.getUsername());
        props.setProperty("password", connectType.getPassword());
        return props;
    }

    /**
     * 设置字段类型 MSSQL 数据类型
     * 来源： https://docs.microsoft.com/zh-cn/sql/connect/jdbc/using-basic-data-types?view=sql-server-2017
     * @param origin
     *            列类型字符串
     * @return
     */
    public FieldType getFieldType(String origin) {
        return FieldType.error(origin);
    }


}

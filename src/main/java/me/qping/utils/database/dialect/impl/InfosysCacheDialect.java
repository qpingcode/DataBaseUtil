package me.qping.utils.database.dialect.impl;

import me.qping.utils.database.connect.DataBaseConnectPropertes;
import me.qping.utils.database.dialect.DataBaseDialect;
import me.qping.utils.database.metadata.bean.FieldType;

import java.sql.JDBCType;
import java.util.Properties;

/**
 * @ClassName InfosysCacheDialect
 * @Author qping
 * @Date 2021/1/6 16:56
 * @Version 1.0
 **/
public class InfosysCacheDialect implements DataBaseDialect {

    @Override
    public String getCatalogQuery() {
        return null;
    }

    @Override
    public String getSchemaQuery() {
        return " SELECT SCHEMA_NAME as NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE NOT SCHEMA_NAME %STARTSWITH '%' ";
    }

    @Override
    public String getPageSql(String sql, int pageSize, int pageNum) {

        if(pageSize < 0){
            throw new RuntimeException("pageSize 不能小于 0 ");
        }

        if(pageNum <= 0 || pageSize == 0){
            return "select top " + pageSize + " * from (\n" + sql + "\n) tmp_0";
        }else{
            throw new RuntimeException("InfosysCache 未实现方法");
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

    public FieldType getFieldType(String origin) {
         return FieldType.error(origin);
    }
}

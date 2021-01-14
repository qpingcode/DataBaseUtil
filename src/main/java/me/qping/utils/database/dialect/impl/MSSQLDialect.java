package me.qping.utils.database.dialect.impl;

import me.qping.utils.database.connect.DataBaseConnectPropertes;
import me.qping.utils.database.dialect.DataBaseDialect;
import me.qping.utils.database.metadata.bean.FieldType;

import java.sql.JDBCType;
import java.util.Properties;

/**
 * @ClassName MSSQLDialect
 * @Author qping
 * @Date 2019/8/3 22:07
 * @Version 1.0
 **/
public class MSSQLDialect implements DataBaseDialect {


    @Override
    public String getCatalogQuery() {
        return "select name from master.dbo.SysDatabases where name not in ('master', 'model', 'msdb', 'tempdb')";
    }

    @Override
    public String getSchemaQuery() {
        return "select name from sys.schemas " +
                " where name not in ('INFORMATION_SCHEMA', 'db_owner', 'db_accessadmin', 'db_backupoperator', 'db_datareader', 'db_datawriter', 'db_ddladmin', 'db_denydatareader', 'db_denydatawriter', 'db_securityadmin', 'sys')";
    }

    @Override
    public String getPageSql(String sql, int pageSize, int pageNum) {

        if(pageSize < 0){
            throw new RuntimeException("pageSize 不能小于 0 ");
        }


        int begin = pageSize * pageNum;
        int end = pageSize * pageNum + pageSize;

        if(pageNum <= 0 || pageSize == 0){
            return "select top " + pageSize + " * from (\n" + sql + "\n) tmp_0";
        }else{
            return "select * from ( " +
                    "   select *, ROW_NUMBER() OVER (ORDER BY (select 0)) AS rn from (\n"+ sql +"\n) tmp_0 " +
                    " ) as tmp_1 where rn > " + begin +" and rn <= " + end;
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
}

package me.qping.utils.database.connect.impl;

import lombok.Data;
import me.qping.utils.database.connect.DataBaseConnectPropertes;
import me.qping.utils.database.connect.DataBaseType;

import static me.qping.utils.database.connect.DataBaseType.MSSQL;

/**
 * @ClassName JTDSDataBaseConnProp
 * @Description sqlserver jtds驱动
 * @Author qping
 * @Date 2019/7/4 16:25
 * @Version 1.0
 **/
@Data
public class JTDSDataBaseConnProp extends MSSQLDataBaseConnProp {

    public static final String URL_JTDS = "jdbc:jtds:sqlserver://${host}:${port}/${database}";;
    String driver = "net.sourceforge.jtds.jdbc.Driver";

    public JTDSDataBaseConnProp(String host, String port, String database, String username, String password) {
       super(host, port, database, username, password);
    }


    public JTDSDataBaseConnProp(String host, String port, String database, String username, String password, String schema) {
        super(host, port, database, username, password, schema);
    }

    public JTDSDataBaseConnProp(String url, String username, String password) {
        super(url, username, password);
    }

    public JTDSDataBaseConnProp() {

    }

    @Override
    public DataBaseType getDataBaseType() {
        return MSSQL;
    }

    public String getUrl(){
        return getURL(URL_JTDS, null);
    }

    /**
     * sqlserver 如果当前登录用户为Sue，且不指定scheme，执行 "select * from table_test"
     * 默认的搜索顺序是：
     *      sys.table_test （Sys Schema）
     *      Sue.table_test （Default Schema）
     *      dbo.table_test （Dbo Schema）
     *
     * 在查询数据库表中的数据时，最好指定特定的Schema前缀，
     * 这样数据库就不用去扫描Sys Schema了，就可以提高查询的速度了
     */
    @Override
    public String getSchema() {
        return schema;
    }

    @Override
    public void setMaxWait(int maxWait) {
    }

    public static void main(String[] args) {
        JTDSDataBaseConnProp d = new JTDSDataBaseConnProp();
        System.out.println(d.getDriver());
    }
}

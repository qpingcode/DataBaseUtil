package me.qping.utils.database.connect.impl;

import lombok.Data;
import me.qping.utils.database.connect.DataBaseConnectPropertes;
import me.qping.utils.database.connect.DataBaseType;

import static me.qping.utils.database.connect.DataBaseType.MSSQL;

/**
 * @ClassName MySQLDataBaseConnProp
 * @Description mysql连接
 * @Author qping
 * @Date 2019/7/4 16:25
 * @Version 1.0
 **/
@Data
public class JTDSDataBaseConnProp implements DataBaseConnectPropertes {

    public static final String URL = "jdbc:jtds:sqlserver://${host}:${port}/${database}";;
    String driver = "net.sourceforge.jtds.jdbc.Driver";

    String validQuery = "select 1";

    String host;
    String port;
    String database;
    String username;
    String password;
    String schema;
    String catalog;
    String url;

    public JTDSDataBaseConnProp(String host, String port, String database, String username, String password) {
        this.host = host;
        this.port = port == null ? "1433" : port;
        this.database = database;
        this.username = username;
        this.password = password;

        this.catalog = database;
    }

    public JTDSDataBaseConnProp(String host, String port, String database, String username, String password, String schema) {
        this(host, port, database, username, password);
        this.schema = schema;
    }

    public JTDSDataBaseConnProp(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;

        this.catalog = this.database = getCatalogByUrl(url);
    }

    private static String getCatalogByUrl(String url) {
        String markStr = "/";
        int begin = url.indexOf(markStr, "jdbc:jtds:sqlserver://".length());

        if(begin > -1){
            begin = url.indexOf("/", begin) + 1;
            int end = url.indexOf("?", begin) > -1 ? url.indexOf("?", begin) : url.length();
            return url.substring(begin, end);
        }
        return null;
    }

    @Override
    public DataBaseType getDataBaseType() {
        return MSSQL;
    }

    public String getUrl(){

        if(url != null){
            return url;
        }

        return URL.replaceAll("\\$\\{host\\}", host)
                .replaceAll("\\$\\{port\\}", port)
                .replaceAll("\\$\\{database\\}", database == null ? "" : database);
    }

    @Override
    public String getCatalog() {
        return this.catalog;
    }

    @Override
    public String getSchema() {
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
        return schema;
    }

}

package me.qping.utils.database.connect.impl;

import lombok.Data;
import me.qping.utils.database.connect.DataBaseType;
import me.qping.utils.database.connect.DataBaseDialect;

import static me.qping.utils.database.connect.DataBaseType.POSTGRESQL;

/**
 * @ClassName PostgresqlDataBaseConnProp
 * @Description postgre sql 驱动
 * @Author qping
 * @Date 2020/6/19
 * @Version 1.0
 **/
@Data
public class PostgresqlDataBaseConnProp extends DataBaseConnAdapter {

    public static final String URL = "jdbc:postgresql://${host}:${port}/${database}?socketTimeout=30&connectTimeout=30";;
    String driver = "org.postgresql.Driver";

    public PostgresqlDataBaseConnProp(String host, String port, String database, String username, String password) {
        this.host = host;
        this.port = port == null ? "5432" : port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.catalog = database;
        this.schema = "public";
    }

    public PostgresqlDataBaseConnProp(String host, String port, String database, String username, String password, String schema) {
        this(host, port, database, username, password);
        this.schema = schema;
    }

    public PostgresqlDataBaseConnProp(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;

        this.catalog = this.database = getCatalogByUrl(url);
    }

    private static String getCatalogByUrl(String url) {
        String markStr = "/";
        int begin = url.indexOf(markStr, "jdbc:postgresql://".length());

        if(begin > -1){
            begin = url.indexOf("/", begin) + 1;
            int end = url.indexOf("?", begin) > -1 ? url.indexOf("?", begin) : url.length();
            return url.substring(begin, end);
        }
        return null;
    }

    @Override
    public DataBaseType getDataBaseType() {
        return POSTGRESQL;
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
    public DataBaseDialect getDataBaseDialect() {

        return new DataBaseDialect() {

            @Override
            public String getCatalogQuery() {
                return "SELECT datname as name FROM pg_database where datname not in ('postgres','template1','template0')";
            }

            @Override
            public String getSchemaQuery() {
                return "select nspname as name from pg_namespace where nspname not in ('pg_toast','pg_temp_1','pg_toast_temp_1','pg_catalog', 'information_schema')\n";
            }

            @Override
            public String getPageSql(String sql, int pageSize, int pageNum) {

                if(pageSize < 0){
                    throw new RuntimeException("pageSize 不能小于 0 ");
                }

                int begin = pageSize * pageNum;
                int end = pageSize * pageNum + pageSize;

                if(pageNum <= 0 || pageSize == 0){
                    return "select  * from (\n" + sql + "\n) tmp_0 limit " + pageSize;
                }else{
                    return "select * from (\n" + sql + "\n) tmp_0 limit " + pageSize + " offset " + begin;
                }
            }

            @Override
            public String getTablePageSql(String tableName, int pageSize, int pageNum) {
                if(pageSize < 0){
                    throw new RuntimeException("pageSize 不能小于 0 ");
                }
                int begin = pageSize * pageNum;

                if(pageNum <= 0 || pageSize == 0){
                    return "select  * from " + tableName + " limit " + pageSize;
                }else{
                    return "select * from " + tableName + " limit " + pageSize + " offset " + begin;
                }
            }
        };
    }

}

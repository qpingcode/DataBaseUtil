package me.qping.utils.database.connect.impl;

import lombok.Data;
import me.qping.utils.database.connect.DataBaseType;
import me.qping.utils.database.connect.DataBaseDialect;

import java.util.Properties;

import static me.qping.utils.database.connect.DataBaseType.ORACLE;

/**
 * @ClassName OracleDataBaseConnProp
 * @Description oracle连接串的生成
 * @Author qping
 * @Date 2019/7/4 15:59
 * @Version 1.0
 **/
public class OracleDataBaseConnProp extends DataBaseConnAdapter {

    public static final String SID_URL = "jdbc:oracle:thin:@${host}:${port}:${sid}";
    public static final String SERVICE_NAME_URL = "jdbc:oracle:thin:@//${host}:${port}/${serviceName}";

    String driver = "oracle.jdbc.driver.OracleDriver";

    String serviceName;
    boolean useServiceName;

    /**
     * Catalog和Schema都属于抽象概念，主要用来解决命名冲突问题
     * 数据库对象表的全限定名可表示为：Catalog.Schema.表名
     | 供应商        | Catalog支持                       | Schema支持                 |
     | ------------- | --------------------------------- | -------------------------- |
     | Oracle        | 不支持                            | Oracle User ID            |
     * @return
     */
    public OracleDataBaseConnProp(String host, String port, boolean useServiceName, String serviceName, String username, String password) {

        this.useServiceName = useServiceName;
        this.host = host;
        this.port = port == null ? "1521" : port;
        this.serviceName = serviceName;
        this.username = username;
        this.password = password;

        this.catalog = null;
        this.schema = this.database = username.toUpperCase();
    }

    public OracleDataBaseConnProp(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;

        this.catalog = null;
        this.schema = this.database = username.toUpperCase();
    }


    @Override
    public DataBaseType getDataBaseType() {
        return ORACLE;
    }

    @Override
    public String getDriver() {
        return driver;
    }

    @Override
    public String getValidQuery() {
        return "select 1 from dual";
    }

    public String getUrl(){

        if(url != null){
            return url;
        }

        if(useServiceName){
            return SERVICE_NAME_URL.replaceAll("\\$\\{host\\}", host)
                    .replaceAll("\\$\\{port\\}", port)
                    .replaceAll("\\$\\{serviceName\\}", serviceName);
        }else{
            return SID_URL.replaceAll("\\$\\{host\\}", host)
                    .replaceAll("\\$\\{port\\}", port)
                    .replaceAll("\\$\\{sid\\}", serviceName);
        }


    }

    @Override
    public DataBaseDialect getDataBaseDialect() {
        return new DataBaseDialect() {

            @Override
            public String getCatalogQuery() {
                return null;
            }

            // oracle schema 等同于 user
            @Override
            public String getSchemaQuery() {
                return "select username from all_users order by username";
            }

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
            public String getTablePageSql(String tableName, int pageSize, int pageNum) {
                if(pageSize < 0){
                    throw new RuntimeException("pageSize 不能小于 0 ");
                }

                int begin = pageSize * pageNum;
                int end = pageSize * pageNum + pageSize;

                if(pageNum <= 0 || pageSize == 0){
                    return "select * from " + tableName + " where rownum <= " + pageSize;
                }else{
                    return "select * from (" +
                            "    select tmp_0.*, rownum as rn from " + tableName + "  tmp_0 where rownum <= " + end +
                            " ) where rn > " + begin;
                }
            }
        };
    }

    @Override
    public Properties getConnectionProperties() {
        Properties props = new Properties();
        props.setProperty("user", getUsername());
        props.setProperty("password", getPassword());
        props.setProperty("remarks", "true");               //设置可以获取remarks信息
        return props;
    }

}

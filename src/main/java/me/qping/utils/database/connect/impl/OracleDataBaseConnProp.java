package me.qping.utils.database.connect.impl;

import lombok.Data;
import me.qping.utils.database.connect.DataBaseConnectPropertes;
import me.qping.utils.database.connect.DataBaseType;

import static me.qping.utils.database.connect.DataBaseType.ORACLE;

/**
 * @ClassName OracleDataBaseConnProp
 * @Description oracle连接串的生成
 * @Author qping
 * @Date 2019/7/4 15:59
 * @Version 1.0
 **/
@Data
public class OracleDataBaseConnProp implements DataBaseConnectPropertes {

    public static final String SID_URL = "jdbc:oracle:thin:@${host}:${port}:${sid}";
    public static final String SERVICE_NAME_URL = "jdbc:oracle:thin:@//${host}:${port}/${serviceName}";

    String driver = "oracle.jdbc.driver.OracleDriver";
    String validQuery = "select 1 from dual";

    String host;
    String port;
    String serviceName;
    String username;
    String password;
    String url;

    boolean useServiceName;

    String database;
    String schema;
    String catalog;

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
    public String getCatalog() {
        return this.catalog;
    }

    @Override
    public String getSchema() {
        return this.schema;
    }
}

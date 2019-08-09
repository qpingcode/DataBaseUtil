package me.qping.utils.database.crud.impl;

import lombok.Data;
import me.qping.utils.database.crud.DataBaseConnectType;

/**
 * @ClassName OracleDataBaseType
 * @Description oracle连接串的生成
 * @Author qping
 * @Date 2019/7/4 15:59
 * @Version 1.0
 **/
@Data
public class OracleDataBaseType implements DataBaseConnectType {

    public static final String SID_URL = "jdbc:oracle:thin:@${host}:${port}:${sid}";
    public static final String SERVICE_NAME_URL = "jdbc:oracle:thin:@//${host}:${port}/${serviceName}";

    String driver = "oracle.jdbc.driver.OracleDriver";
    String validQuery = "select 1 from dual";

    String host;
    String port;
    String serviceName;
    String username;
    String password;


    public OracleDataBaseType(String host, String port, String serviceName, String username, String password) {
        this.host = host;
        this.port = port == null ? "1521" : port;
        this.serviceName = serviceName;
        this.username = username;
        this.password = password;
    }


    @Override
    public String getType() {
        return "oracle";
    }

    public String getUrl(){
        return SERVICE_NAME_URL.replaceAll("\\$\\{host\\}", host)
                .replaceAll("\\$\\{port\\}", port)
                .replaceAll("\\$\\{serviceName\\}", serviceName);
    }

    @Override
    public String getCatalog() {
        return null;
    }

    @Override
    public String getSchema() {
        return this.username;
    }
}

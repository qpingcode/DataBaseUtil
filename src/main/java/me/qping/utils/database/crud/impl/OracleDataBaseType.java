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
    String database;
    String username;
    String password;

    boolean useServiceName;

    public OracleDataBaseType(String host, String port, boolean useServiceName, String database, String username, String password) {
        this.useServiceName = useServiceName;
        this.host = host;
        this.port = port == null ? "1521" : port;
        this.database = database;
        this.username = username;
        this.password = password;
    }


    @Override
    public String getType() {
        return "oracle";
    }

    public String getUrl(){
        if(useServiceName){
            return SERVICE_NAME_URL.replaceAll("\\$\\{host\\}", host)
                    .replaceAll("\\$\\{port\\}", port)
                    .replaceAll("\\$\\{serviceName\\}", database);
        }else{
            return SID_URL.replaceAll("\\$\\{host\\}", host)
                    .replaceAll("\\$\\{port\\}", port)
                    .replaceAll("\\$\\{sid\\}", database);
        }


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

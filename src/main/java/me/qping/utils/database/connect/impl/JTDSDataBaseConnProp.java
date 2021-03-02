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

    public static final String URL_JTDS = "jdbc:jtds:sqlserver://#{host}:#{port}/#{database}";;
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

    public static void main(String[] args) {
        JTDSDataBaseConnProp d = new JTDSDataBaseConnProp();
        System.out.println(d.getDriver());
    }
}

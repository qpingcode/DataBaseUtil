package me.qping.utils.database.metadata.impl;

import me.qping.utils.database.crud.DataBaseConnectType;
import me.qping.utils.database.metadata.Analyze;
import me.qping.utils.database.metadata.bean.ColumnMeta;
import me.qping.utils.database.metadata.bean.PrimaryKeyMeta;
import me.qping.utils.database.metadata.bean.TableMeta;

import java.sql.*;
import java.util.*;

/**
 * @ClassName OracleAnalyze
 * @Author qping
 * @Date 2019/8/3 22:07
 * @Version 1.0
 **/
public class OracleAnalyze extends Analyze {

    @Override
    public Properties getConnectionProperties(DataBaseConnectType connectType) {
        Properties props = new Properties();
        props.setProperty("user", connectType.getUsername());
        props.setProperty("password", connectType.getPassword());
        props.setProperty("remarks", "true");               //设置可以获取remarks信息
        return props;
    }

    /**
     * 设置字段类型 Oracle 数据类型
     * 来源： https://docs.oracle.com/cd/E11882_01/java.112/e16548/datacc.htm#JJDBC28370
     * @param columnType
     *            列类型字符串
     * @param packageName
     *            封装包信息
     * @return
     */
    public String getFieldType(String columnType, StringBuffer packageName) {

        // todo BLOB CLOB NCLOB
        columnType = columnType.toLowerCase();

        switch (columnType){
            case "char":
            case "varchar2":
            case "nchar":
            case "long":
                return "String";

            case "number":
                // number 根据位数不同还可以转换为 boolean byte short int long float double
                packageName.append("import java.math.BigDecimal;");
                return "BigDecimal";

            case "raw":
            case "longraw":
                return "byte[]";

            case "date":
            case "timestamp":
            case "timestamp with time zone":
            case "timestamp with local time zone":
                packageName.append("import java.util.Date;");
                return "Date";

            case "binary_float":
                return "Float";

            case "binary_double":
                return "Double";

            case "blob":
                packageName.append("import java.sql.Blob;");
                return "Blob";

            case "clob":
                packageName.append("import java.sql.Clob;");
                return "Clob";

        }

        return "ErrorType";
    }
}

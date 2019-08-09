package me.qping.utils.database.metadata.impl;

import me.qping.utils.database.crud.DataBaseConnectType;
import me.qping.utils.database.metadata.Analyze;

import java.util.Properties;

/**
 * @ClassName MSSQLAnalyze
 * @Author qping
 * @Date 2019/8/3 22:07
 * @Version 1.0
 **/
public class MSSQLAnalyze extends Analyze {

    @Override
    public Properties getConnectionProperties(DataBaseConnectType connectType) {
        Properties props = new Properties();
        props.setProperty("user", connectType.getUsername());
        props.setProperty("password", connectType.getPassword());
        props.setProperty("remarksReporting", "true");
        return props;
    }

    /**
     * 设置字段类型 Oracle 数据类型
     * 来源： https://docs.microsoft.com/zh-cn/sql/connect/jdbc/using-basic-data-types?view=sql-server-2017
     * @param columnType
     *            列类型字符串
     * @param packageName
     *            封装包信息
     * @return
     */
    public String getFieldType(String columnType, StringBuffer packageName) {
        columnType = columnType.toLowerCase();

        switch (columnType) {
            case "bigint":
                return "Long";

            case "binary":
            case "image":
            case "geometry":
            case "geography":
            case "varbinary":
            case "udt":
            case "timestamp":
                return "byte[]";

            case "bit":
                return "Boolean";

            case "char":
            case "nchar":
            case "ntext":
            case "nvarchar":
            case "varchar":
            case "text":
            case "xml":
            case "uniqueidentifier":
                return "String";

            case "date":
            case "datetime":
            case "datetime2":
            case "smalldatetime":
                packageName.append("import java.sql.Timestamp;");
                return "Timestamp";

            case "float":
                return "Double";

            case "int":
                return "Integer";


            case "money":
            case "smallmoney":
            case "numeric":
                packageName.append("import java.math.BigDecimal;");
                return "BigDecimal";

            case "real":
                return "Float";

            case "smallint":
            case "tinyint":
                return "Short";

            case "time":
                packageName.append("import java.sql.Time;");
                return "Time";
        }

        return "ErrorType";
    }
}

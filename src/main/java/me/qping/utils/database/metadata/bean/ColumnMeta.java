package me.qping.utils.database.metadata.bean;

import lombok.Data;
import me.qping.utils.database.connect.DataBaseType;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

/**
 * @ClassName ColumnMeta
 * @Description 列属性
 * @Author qping
 * @Date 2019/8/3 21:32
 * @Version 1.0
 **/
@Data
public class ColumnMeta {


    public static final int CLOB_LENGTH = 9999999;

//    public static final int TYPE_NONE = 0;
//    public static final int TYPE_STRING = 1;
//    public static final int TYPE_NUMBER = 2;
//    public static final int TYPE_DATE = 3;
//    public static final int TYPE_BOOLEAN = 4;
//    public static final int TYPE_INTEGER = 5;
//    public static final int TYPE_BIGNUMBER = 6;
//    public static final int TYPE_CLOB = 7;
//    public static final int TYPE_BLOB = 8;
//    public static final int TYPE_BINARY = 9;
//    public static final int TYPE_TIMESTAMP = 10;


    String name;            // 列名
    String alias;           // 别名
    String type;            // TYPE_NAME  数据库原始列类型比如VARCHAR(200)
    String comment;         // 备注

    int size;                   // 字段长度
    int digits;                 // 小数精度
    boolean nullable;           // 是否可为空
    boolean isPrimaryKey;       // 是否主键

    Boolean isDate;             // 是否日期类型
    String javaFullType;        // java 类型完整路径，如 java.lang.Integer
    String javaType;            // 自动生成代码时，字段前缀, 如 Integer
    String javaPackage;         // 自动生成代码时，需要 import 的类库 ,如 java.util.Date
    String columnDefinition;    // TYPE_NAME  数据库原始列类型比如VARCHAR(200)
    JDBCType sqlType;           // java.sql.Types
    int selfType;               // 自定义识别类型

    public static ColumnMeta of(String name, String type, String comment, int size, int digits, boolean nullable, boolean isPrimaryKey, String javaFullType, String javaType, String javaPackage, Boolean isDate, JDBCType jdbcType, String columnDefinition) {
        ColumnMeta columnMeta = new ColumnMeta();
        columnMeta.setName(name);
        columnMeta.setType(type);
        columnMeta.setComment(comment);
        columnMeta.setSize(size);
        columnMeta.setDigits(digits);
        columnMeta.setNullable(nullable);
        columnMeta.setPrimaryKey(isPrimaryKey);
        columnMeta.setJavaFullType(javaFullType);
        columnMeta.setJavaType(javaType);
        columnMeta.setJavaPackage(javaPackage);
        columnMeta.setIsDate(isDate);
        columnMeta.setSqlType(jdbcType);
        columnMeta.setColumnDefinition(columnDefinition);
        return columnMeta;
    }

    public static ColumnMeta getFromSqlType(JDBCType jdbcType) {
        String javaType = null;
        switch (jdbcType){
            case BIT:
            case BOOLEAN:
                javaType = "java.lang.Boolean";
                break;
            case TINYINT:
                javaType = "java.lang.Byte";
                break;
            case SMALLINT:
                javaType = "java.lang.Short";
                break;
            case INTEGER:
                javaType = "java.lang.Integer";
                break;
            case BIGINT:
                javaType = "java.lang.Long";
                break;
            case DOUBLE:
            case NUMERIC:
            case DECIMAL:
            case FLOAT:
            case REAL:
                javaType = "java.math.BigDecimal";
                break;
            case NCHAR:
            case NVARCHAR:
            case LONGNVARCHAR:
            case CHAR:
            case VARCHAR:
            case LONGVARCHAR:
                javaType = "java.lang.String";
                break;
            case DATE:
            case TIME:
                javaType = "java.sql.Date";
                break;
            case TIME_WITH_TIMEZONE:
            case TIMESTAMP_WITH_TIMEZONE:
            case TIMESTAMP:
                javaType = "java.sql.Timestamp";
                break;
            case BINARY:
            case VARBINARY:
            case LONGVARBINARY:
                javaType = "byte[]";
                break;
            case STRUCT:
                javaType = "java.sql.Struct";
                break;
            case ARRAY:
                javaType = "java.sql.Array";
                break;
            case BLOB:
                javaType = "java.sql.Blob";
                break;
            case CLOB:
                javaType = "java.sql.Clob";
                break;
            case REF:
                javaType = "java.sql.Ref";
                break;
            case DATALINK:
                javaType = "java.net.URL";
                break;
            case NCLOB:
                javaType = "java.sql.NClob";
                break;
        }

        ColumnMeta col = new ColumnMeta();
        col.setSqlType(jdbcType);

        if(javaType == null){
            return col;
        }
        col.setJavaFullType(javaType);
        col.setJavaType(getjavaType(javaType));
        col.setJavaPackage(javaType.indexOf("java.lang") > -1 ? null : javaType);
        return col;
    }


    public static ColumnMeta getFromResultSet(ResultSetMetaData rsmeta, int index, DataBaseType dataBaseType) throws SQLException {

        String name = rsmeta.getColumnName(index);
        String label = rsmeta.getColumnLabel(index);
        int precision = rsmeta.getPrecision(index);
        int scale = rsmeta.getScale(index);
        int columnType = rsmeta.getColumnType(index);
        String typeName = rsmeta.getColumnTypeName(index);
        String className = rsmeta.getColumnClassName(index);
        int displaySize = rsmeta.getColumnDisplaySize(index);


        int size = displaySize;
        int digits = scale;
        JDBCType jdbcType = JDBCType.valueOf(columnType);



        boolean signed = false;
        try {
            signed = rsmeta.isSigned(index);
        } catch ( Exception ignored) { }

        if(jdbcType.equals(JDBCType.DOUBLE)
                || jdbcType.equals(JDBCType.NUMERIC)
                || jdbcType.equals(JDBCType.DECIMAL)
                || jdbcType.equals(JDBCType.FLOAT)
                || jdbcType.equals(JDBCType.REAL)
                ){
            size = precision;
        }

        ColumnMeta col = getFromSqlType(jdbcType);
        col.setName(name);
        col.setAlias(label);
        col.setSqlType(jdbcType);
        col.setSize(size);
        col.setDigits(digits);
        col.setType(typeName);
        return col;
    }



    private static String getjavaType(String fullJavaType){
        if(fullJavaType.indexOf("\\.") > -1){
            return fullJavaType.substring(fullJavaType.indexOf("\\."));
        }else{
            return fullJavaType;
        }
    }


}

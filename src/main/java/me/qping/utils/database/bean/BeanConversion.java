package me.qping.utils.database.bean;

import me.qping.utils.database.exception.OrmException;
import oracle.sql.CLOB;

import java.lang.reflect.Field;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName BeanConversion
 * @Description bean转换
 * @Author qping
 * @Date 2020/1/16 11:58
 * @Version 1.0
 **/
public class BeanConversion {

    public static <T> T convert(Class<T> clazz, ResultSetMetaData metaData, ResultSet rs) throws SQLException, OrmException, IllegalAccessException, InstantiationException {
        FieldDefines fieldDefines = getColumnAnnotation(clazz);
        int columnCount = metaData.getColumnCount();
        T t = clazz.newInstance();
        for (int i = 0; i < columnCount; i++) {
            String label = metaData.getColumnLabel(i + 1);
            String className = metaData.getColumnClassName(i + 1);

            Object value = rs.getObject(label);

            Field field = fieldDefines.get(label);

            if(field == null || value == null){
                continue;
            }

            if(!field.getType().getName().equals(className)){
                try{
                    value = conversionType(value, field.getType());
                }catch (Exception ex){
                    throw new OrmException(String.format("列 %s 转换错误,期望类型 %s ，实际类型 %s", field.getName(), field.getType().getName(), className));
                }
            }

            field.set(t, value);
        }
        return t;
    }

    private static <T> Object conversionType(Object value, Class<T> targetClass) throws ParseException, OrmException, SQLException {
        if(targetClass.equals(Integer.class)){
            return Integer.parseInt(String.valueOf(value));
        }
        else if(targetClass.equals(String.class)){

            if(value instanceof Clob){
                Clob clob = ((Clob) value);
                return clob.getSubString((long) 1, (int) clob.length());
            }

            if(value instanceof Blob){
                throw new OrmException("无法将BLOB转换为String");
            }


            return String.valueOf(value);
        }
        else if(targetClass.equals(byte[].class)){
            if(value instanceof Blob){
                Blob blob = ((Blob) value);
                return blob.getBytes(1, (int) blob.length());
            }
            if(value instanceof Clob){
                Clob clob = ((Clob) value);
                return clob.getSubString(1, (int) clob.length()).getBytes();
            }
        }
        else if(targetClass.equals(java.util.Date.class)){
            if(value instanceof String){
                new SimpleDateFormat("yyyy-MM-dd").parse(value.toString());
            }
        }

        return (T) value;

    }

    public static FieldDefines getColumnAnnotation(Class clazz){
        Field[] fields = clazz.getDeclaredFields();
        FieldDefines fieldDefines = new FieldDefines();

        for(Field field : fields){
            DataBaseColumn[] columns = field.getAnnotationsByType(DataBaseColumn.class);
//            DatabaseColumn column = field.getAnnotation(DatabaseColumn.class);
            if(columns == null || columns.length == 0){
                continue;
            }
            field.setAccessible(true);
            for(DataBaseColumn column : columns){
                fieldDefines.put(column.value(), field);
            }
        }
        return fieldDefines;
    }

    public static String getTableAnnotation(Class clazz){
        if(!clazz.isAnnotationPresent(DataBaseTable.class)){
            throw new RuntimeException("must use @DataBaseTable annotation");
        }
        DataBaseTable table = (DataBaseTable) clazz.getAnnotation(DataBaseTable.class);
        return table.value();
    }
}

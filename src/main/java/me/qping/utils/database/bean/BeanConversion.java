package me.qping.utils.database.bean;

import me.qping.utils.database.exception.OrmException;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
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
        Map<String, Field> beanFields = getClassAnnotation(clazz);
        int columnCount = metaData.getColumnCount();
        T t = clazz.newInstance();
        for (int i = 0; i < columnCount; i++) {
            String label = metaData.getColumnLabel(i + 1);
            String className = metaData.getColumnClassName(i + 1);

            Object value = rs.getObject(label);

            Field field = beanFields.get(label);
            if(field == null){
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

    private static <T> Object conversionType(Object value, Class<T> targetClass) throws ParseException, OrmException {

        if(targetClass.equals(Integer.class)){
            return Integer.parseInt(String.valueOf(value));
        }

        else if(targetClass.equals(String.class)){
            return String.valueOf(value);
        }

        else if(targetClass.equals(java.util.Date.class)){
            if(value instanceof String){
                new SimpleDateFormat("yyyy-MM-dd").parse(value.toString());
            }
        }

        return (T) value;

    }

    private static Map<String, Field> getClassAnnotation(Class clazz){
        Field[] fields = clazz.getDeclaredFields();
        Map<String, Field> beanDefine = new HashMap<>();
        for(Field field : fields){
            DataBaseColumn[] columns = field.getAnnotationsByType(DataBaseColumn.class);
//            DatabaseColumn column = field.getAnnotation(DatabaseColumn.class);
            if(columns == null || columns.length == 0){
                continue;
            }
            field.setAccessible(true);
            for(DataBaseColumn column : columns){
                beanDefine.put(column.value(), field);
            }
        }
        return beanDefine;
    }
}

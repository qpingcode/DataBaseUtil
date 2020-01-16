package me.qping.utils.database.bean;

import java.lang.reflect.Field;

/**
 * @ClassName BeanField
 * @Description bean中关于orm的定义
 * @Author qping
 * @Date 2020/1/16 11:02
 * @Version 1.0
 **/
public class BeanField {

    Field field;
    String name = null;

    boolean userDefineIndex = false;

    public BeanField(Field field, DatabaseColumn column) {
        this.name = column.value();
        this.field = field;
    }
}

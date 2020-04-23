package me.qping.utils.database.bean;


import java.lang.annotation.*;

@Repeatable(value = DataBaseColumns.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataBaseColumn {
    public String value() default "";
}

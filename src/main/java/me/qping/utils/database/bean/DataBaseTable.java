package me.qping.utils.database.bean;


import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataBaseTable {
    public String value() default "";
}

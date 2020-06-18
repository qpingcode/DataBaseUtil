package me.qping.utils.database.bean;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FieldDefines {

    boolean caseSensitive = false;

    Map<String, Field> fieldMap = new HashMap<>();

    public int size(){
        return fieldMap.keySet().size();
    }

    public Set<String> keySet(){
        return fieldMap.keySet();
    }

    public void put(String fieldName, Field field){

        if(fieldName == null) return;

        if(!caseSensitive){
            fieldName = fieldName.toUpperCase();
        }

        fieldMap.put(fieldName, field);
    }

    public  Field get(String fieldName){

        if(fieldName == null) return null;

        if(!caseSensitive){
            fieldName = fieldName.toUpperCase();
        }

        return fieldMap.get(fieldName);
    }

    public Object getValue(Object obj, String fieldName) throws IllegalAccessException {
        if(obj == null || fieldName == null || get(fieldName) == null) return null;
        return  get(fieldName).get(obj);

    }

    public void setCaseSensitive(boolean sensitive){
        this.caseSensitive = sensitive;
    }

}

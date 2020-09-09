package me.qping.utils.database.util;

import me.qping.utils.database.exception.OrmException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName DataRecord
 * @Description 返回数据集
 * @Author qping
 * @Date 2020/9/7 18:05
 * @Version 1.0
 **/
public class DataRecord {

    Object[] data;
    Map<String, Integer> nameMap = new HashMap<>();

    public DataRecord(){

    }

    public DataRecord(Object[] row, Map<String, Integer> nameMap) {
        this.data = row;
        this.nameMap = nameMap;
    }

    public Object get(int index){
        return data[index];
    }

    public Object get(String name) throws OrmException {
        if(!nameMap.containsKey(name)){
            throw new OrmException("数据集不存在指定的列名");
        }
        return data[nameMap.get(name)];
    }

    public String getString(int index){
        Object obj = get(index);
        return obj == null ? null : (String) obj;
    }

    public Integer getInt(int index){
        Object obj = get(index);
        return obj == null ? null : (Integer) obj;
    }

    public String getString(String name) throws OrmException {
        Object obj = get(name);
        return obj == null ? null : (String) obj;
    }

    public Integer getInt(String name) throws OrmException {
        Object obj = get(name);
        return obj == null ? null : (Integer) obj;
    }

    public Object[] toArray(){
        return data;
    }

    public Map<String, Object> toMap(){
        Set<String> keyset = nameMap.keySet();
        Map<String, Object> map = new HashMap<>();
        for(String key : keyset){
            map.put(key, data[nameMap.get(key)]);
        }
        return map;
    }
}

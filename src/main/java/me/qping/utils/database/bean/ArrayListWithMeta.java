package me.qping.utils.database.bean;

import me.qping.utils.database.metadata.bean.ColumnMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName ArrayListWithMeta
 * @Description TODO
 * @Author qping
 * @Date 2021/6/13 15:18
 * @Version 1.0
 **/
public class ArrayListWithMeta<E> extends ArrayList<E> {

    List<ColumnMeta> columnMetaList = new ArrayList<>();

    public List<ColumnMeta> getColumnMetaList(){
        return this.columnMetaList;
    }


    public void addColumnMeta(ColumnMeta columnMeta){
        this.columnMetaList.add(columnMeta);
    }

}

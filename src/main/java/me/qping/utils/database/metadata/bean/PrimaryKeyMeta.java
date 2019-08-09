package me.qping.utils.database.metadata.bean;

import lombok.Data;

/**
 * @ClassName ColumnMeta
 * @Author qping
 * @Date 2019/8/3 21:32
 * @Version 1.0
 **/
@Data
public class PrimaryKeyMeta {
    String name;
    int seq;

    public static PrimaryKeyMeta of(String name, int seq){
        PrimaryKeyMeta primaryKeyMeta = new PrimaryKeyMeta();
        primaryKeyMeta.setName(name);
        primaryKeyMeta.setSeq(seq);
        return primaryKeyMeta;
    }
}

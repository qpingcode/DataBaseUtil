package me.qping.utils.database.metadata.bean;

import lombok.Data;

/**
 * @ClassName ForeignKeyMeta
 * @Description 外键
 * @Author qping
 * @Date 2019/12/16 14:19
 * @Version 1.0
 **/
@Data
public class ForeignKeyMeta {

    String name;

    String pkTableCat;
    String pkTableSchema;
    String pkTableName;
    String pkColumnName;

    String fkTableCat;
    String fkTableSchema;
    String fkTableName;
    String fkColumnName;


    int updateRule;
    int deleteRule;

    String fkName;
    String pkName;

    int seq;

    public static ForeignKeyMeta of(String name, int seq){
        ForeignKeyMeta keyMeta = new ForeignKeyMeta();
        keyMeta.setName(name);
        keyMeta.setSeq(seq);
        return keyMeta;
    }


}

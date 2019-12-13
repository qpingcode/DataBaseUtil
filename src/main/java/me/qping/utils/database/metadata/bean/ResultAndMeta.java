package me.qping.utils.database.metadata.bean;

import lombok.Data;

import java.util.List;

/**
 * @ClassName ResultAndMeta
 * @Author qping
 * @Date 2019/12/12 11:19
 * @Version 1.0
 **/
@Data
public class ResultAndMeta {

    List<ResultSetColumnMeta> columnMetaList;
    List<Object[]> result;

}

package me.qping.utils.database.util;

import me.qping.utils.database.metadata.bean.ResultSetColumnMeta;

import java.util.List;
import java.util.Map;

/**
 * @ClassName Callback
 * @Description 回调
 * @Author qping
 * @Date 2020/1/16 14:44
 * @Version 1.0
 **/
public interface Callback {
    void next(Map<String, Object> map, List<ResultSetColumnMeta> columnMetaList, int index);
    void finish(int total);
}

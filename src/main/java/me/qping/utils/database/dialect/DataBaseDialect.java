package me.qping.utils.database.dialect;

import me.qping.utils.database.connect.DataBaseConnectPropertes;
import me.qping.utils.database.metadata.bean.FieldType;

import java.util.Properties;

/**
 * @ClassName DataBaseDialect
 * @Description 数据库方言
 * @Author qping
 * @Date 2019/12/12 16:28
 * @Version 1.0
 **/
public interface DataBaseDialect {

    public Properties getConnectionProperties(DataBaseConnectPropertes connectType);

    // 数据库类型与 java 类型映射
    // https://blog.csdn.net/weixin_34195546/article/details/87611601
    public FieldType getFieldType(String columnType);

    public String getCatalogQuery();

    public String getSchemaQuery();

    public String getTopNSql(String tableName, int rowCount);

}

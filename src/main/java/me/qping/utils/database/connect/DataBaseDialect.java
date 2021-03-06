package me.qping.utils.database.connect;

import me.qping.utils.database.connect.DataBaseConnectPropertes;
import me.qping.utils.database.metadata.bean.FieldType;

import java.sql.SQLException;
import java.util.Properties;

/**
 * @ClassName DataBaseDialect
 * @Description 数据库方言
 * @Author qping
 * @Date 2019/12/12 16:28
 * @Version 1.0
 **/
public interface DataBaseDialect {

    public String getCatalogQuery();

    public String getSchemaQuery();

    /**
     * 构建分页sql
     * @param sql
     * @param pageSize  每页数量
     * @param pageNum   当前第几页，从0开始，当pageNum小于0时，会构建获取前pageSize条记录的sql
     * @return
     * @throws SQLException
     */
    public String getPageSql(String sql, int pageSize, int pageNum);


    /**
     * 构建分页sql
     * @param tableName 表名
     * @param pageSize  每页数量
     * @param pageNum   当前第几页，从0开始，当pageNum小于0时，会构建获取前pageSize条记录的sql
     * @return
     * @throws SQLException
     */
    public String getTablePageSql(String tableName, int pageSize, int pageNum);

}

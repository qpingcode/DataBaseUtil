package me.qping.utils.database.connect.impl;

import me.qping.utils.database.connect.DataBaseDialect;
import me.qping.utils.database.connect.DataBaseType;
import me.qping.utils.database.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName H2Database
 * @Description H2数据库
 * @Author qping
 * @Date 2021/6/30 16:00
 * @Version 1.0
 **/
public class H2Database extends DataBaseConnAdapter {

    int DEFAULT_PORT = 9092;

    String URL = null;
    String dbPath;
    boolean useMemoryMode;


    // jdbc:h2:tcp://<server>[:<port>]/[<path>]<databaseName>
    public static final String TCP_URL = "jdbc:h2:tcp://#{host}:#{port}/#{dbPath}#{database}";
    public static final String TCP_MEM_URL = "jdbc:h2:tcp://#{host}:#{port}/#{dbPath}/mem:#{database}";

    // jdbc:h2:[file:][<path>]<databaseName>
    public static final String EMBED_URL = "jdbc:h2:file:#{dbPath}#{database}";
    public static final String EMBED_MEM_URL = "jdbc:h2:file:mem:#{database}";


    public H2Database(String url, String username, String password){
        super(url, username, password);
    }

    public H2Database(String host, String port, String dbPath, String database, String username, String password, boolean useMemoryMode) {
        super(host, port, database, username, password);

        if(StringUtils.isNotBlank(dbPath) && !dbPath.endsWith("/")){
            this.dbPath = dbPath + "/";
        }else{
            this.dbPath = dbPath;
        }

        this.useMemoryMode = useMemoryMode;

        if(host != null && port == null){
            port = DEFAULT_PORT + "";
        }

        // 当ip和端口都为空时，使用嵌入（本地）模式
        if(host == null && port == null){
            URL = useMemoryMode ? EMBED_MEM_URL : EMBED_URL;
        }else{
            URL = useMemoryMode ? TCP_MEM_URL : TCP_URL;
        }

    }

    @Override
    public DataBaseType getDataBaseType() {
        return DataBaseType.H2;
    }

    @Override
    public String getDriver() {
        return "org.h2.Driver";
    }

    @Override
    public String getUrl() {
        Map map = new HashMap();
        map.put("dbPath", dbPath);
        return getURL(URL, map);
    }

    @Override
    public DataBaseDialect getDataBaseDialect() {
        return new DataBaseDialect() {
            @Override
            public String getCatalogQuery() {
                return null;
            }

            @Override
            public String getSchemaQuery() {
                return "show databases";
            }

            @Override
            public String getPageSql(String sql, int pageSize, int pageNum) {
                return getTablePageSql("(\n" + sql + "\n)", pageSize, pageNum);
            }

            @Override
            public String getTablePageSql(String tableName, int pageSize, int pageNum) {
                if(pageSize < 0){
                    throw new RuntimeException("pageSize 不能小于 0 ");
                }
                int begin = pageSize * pageNum;
                int end = pageSize * pageNum + pageSize;
                return "select * from " + tableName + " tmp_0 limit " + begin + "," + pageSize;
            }
        };
    }


}

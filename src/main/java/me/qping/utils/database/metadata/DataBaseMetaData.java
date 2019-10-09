package me.qping.utils.database.metadata;

import lombok.Data;
import me.qping.utils.database.crud.DataBaseConnectType;
import me.qping.utils.database.metadata.bean.TableMeta;
import me.qping.utils.database.metadata.impl.MSSQLAnalyze;
import me.qping.utils.database.metadata.impl.MySQLAnalyze;
import me.qping.utils.database.metadata.impl.OracleAnalyze;

import java.util.List;

/**
 * @ClassName DataBaseMetaData
 * @Author qping
 * @Date 2019/8/2 16:11
 * @Version 1.0
 **/
@Data
public class DataBaseMetaData {


    DataBaseConnectType connectType;

    public static DataBaseMetaDataBuilder builder(){
        return new DataBaseMetaDataBuilder();
    }


    public static final String TYPE_TABLE = "TABLE";
    public static final String TYPE_VIEW = "VIEW";

    public List<TableMeta> listTable(){
        Analyze analyze = getAnalyze(connectType.getDataBaseType());
        return analyze.list(connectType, connectType.getCatalog(), connectType.getSchema(), new String[]{TYPE_TABLE, TYPE_VIEW});
    }

    public TableMeta analyze(String tableName){
        Analyze analyze = getAnalyze(connectType.getDataBaseType());
        return analyze.analyze(connectType, connectType.getCatalog(), connectType.getSchema(), tableName);
    }

    public Analyze getAnalyze(String type){
        Analyze analyze = null;
        if(connectType.getDataBaseType().equals(TableMeta.TYPE_MYSQL)){
            analyze = new MySQLAnalyze();
        }

        if(connectType.getDataBaseType().equals(TableMeta.TYPE_ORACLE)){
            analyze = new OracleAnalyze();
        }

        if(connectType.getDataBaseType().equals(TableMeta.TYPE_MSSQL)){
            analyze = new MSSQLAnalyze();
        }
        return analyze;
    }


}

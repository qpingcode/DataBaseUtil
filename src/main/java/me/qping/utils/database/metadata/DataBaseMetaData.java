package me.qping.utils.database.metadata;

import lombok.Data;
import me.qping.utils.database.crud.DataBase;
import me.qping.utils.database.crud.DataBaseConnectType;
import me.qping.utils.database.crud.impl.MSSQLDataBaseType;
import me.qping.utils.database.crud.impl.MySQLDataBaseType;
import me.qping.utils.database.metadata.bean.TableMeta;
import me.qping.utils.database.metadata.impl.MSSQLAnalyze;
import me.qping.utils.database.metadata.impl.MySQLAnalyze;
import me.qping.utils.database.metadata.impl.OracleAnalyze;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public List<TableMeta> listTable(String type){
        Analyze analyze = getAnalyze(connectType.getType());
        return analyze.list(connectType, connectType.getCatalog(), connectType.getSchema(), type);
    }

    public TableMeta analyze(String tableName){
        Analyze analyze = getAnalyze(connectType.getType());
        return analyze.analyze(connectType, connectType.getCatalog(), connectType.getSchema(), tableName);
    }

    public Analyze getAnalyze(String type){
        Analyze analyze = null;
        if(connectType.getType().equals(TableMeta.TYPE_MYSQL)){
            analyze = new MySQLAnalyze();
        }

        if(connectType.getType().equals(TableMeta.TYPE_ORACLE)){
            analyze = new OracleAnalyze();
        }

        if(connectType.getType().equals(TableMeta.TYPE_MSSQL)){
            analyze = new MSSQLAnalyze();
        }
        return analyze;
    }


}

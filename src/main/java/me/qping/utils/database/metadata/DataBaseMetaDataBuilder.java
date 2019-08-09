package me.qping.utils.database.metadata;

import me.qping.utils.database.crud.DataBase;
import me.qping.utils.database.crud.DataBaseBuilder;
import me.qping.utils.database.crud.DataBaseConnectType;
import me.qping.utils.database.crud.impl.MSSQLDataBaseType;
import me.qping.utils.database.crud.impl.MySQLDataBaseType;
import me.qping.utils.database.crud.impl.OracleDataBaseType;
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
public class DataBaseMetaDataBuilder extends DataBase {

    DataBaseConnectType dataBaseType;

    public DataBaseMetaDataBuilder databaseType(DataBaseConnectType dataBaseType){
        this.dataBaseType = dataBaseType;
        return this;
    }

    public DataBaseMetaDataBuilder mysql(String host, String port, String database, String username, String password){
        this.dataBaseType = new MySQLDataBaseType(host, port, database, username, password);
        return this;
    }

    public DataBaseMetaDataBuilder oracle(String host, String port, String serviceName, String username, String password){
        this.dataBaseType = new OracleDataBaseType(host, port, serviceName, username, password);
        return this;
    }
    public DataBaseMetaDataBuilder mssql(String host, String port, String database, String username, String password){
        this.dataBaseType = new MSSQLDataBaseType(host, port, database, username, password);
        return this;
    }



    public DataBaseMetaData build(){
        DataBaseMetaData metaData = new DataBaseMetaData();
        metaData.setConnectType(this.dataBaseType);
        return metaData;
    }



}

package me.qping.utils.database.connect;

public enum DataBaseType {
    MYSQL(1), MSSQL(2), ORACLE(3), DB2(4), SQLITE(5), POSTGRESQL(6), SQLSERVER2000(7), INFOSYSCACHE(8), HIVE(9), H2(10), MYSQL5(11);

    int intVal;

    private DataBaseType(int intVal){
        this.intVal = intVal;
    }

    public int intVal(){
        return intVal;
    }

    public static DataBaseType valueOf(Integer value) {

        for(DataBaseType dict : DataBaseType.values()){
            if(dict.intVal == value){
                return dict;
            }
        }

        return null;
    }

}

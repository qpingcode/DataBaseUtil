package me.qping.utils;

import com.microsoft.sqlserver.jdbc.SQLServerBulkCopy;
import com.microsoft.sqlserver.jdbc.SQLServerBulkCopyOptions;
import com.sun.rowset.CachedRowSetImpl;
import me.qping.utils.database.crud.DataBase;
import me.qping.utils.database.metadata.DataBaseMetaData;
import me.qping.utils.database.metadata.bean.TableMeta;

import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLType;
import java.util.List;

/**
 * @ClassName Test
 * @Description TODO
 * @Author qping
 * @Date 2019/9/28 10:12
 * @Version 1.0
 **/
public class Test {

    public static void main(String[] args) {

        // ----------------------  test sqlserver ---------------------
        DataBase db = DataBase.builder().mssql("127.0.0.1", "1434", "QZJ_INTERFACE_V30", "sa", "123456", "dbo").build();

//        int i = db.insert("insert into tb_mz_sfmxb(" +
//                "CHARGE_PROJ_ID,KLX,KH,OP_EM_HP_MARK,OP_EM_HP_NO,ACCOUNT_NO,PROJ_DENO,PROJ_NAME,CHARGE_REFUND," +
//                "FIN_CAT_CODE,PROJ_UP,PROJ_AMT,PROJ_MON,YLJGDM,YLJGMC,DPT_CODE,DPT_NAME,RCD_NAME,RCD_DT,XGBZ,ROWID" +
//                ") values('1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','2018-01-01','1')");
//        System.out.println(i);

        SQLServerBulkCopyOptions
                  options = new SQLServerBulkCopyOptions();



        try {
            SQLServerBulkCopy bulkCopy = new SQLServerBulkCopy("");
            CachedRowSetImpl rowSet = new CachedRowSetImpl();
            rowSet.populate(null);
            ResultSet a = null;
            bulkCopy.writeToServer(a);
            rowSet.updateObject(1, "", JDBCType.DATE);
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
}

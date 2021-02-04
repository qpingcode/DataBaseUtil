import me.qping.utils.database.DataBaseUtilBuilder;
import me.qping.utils.database.exception.OrmException;
import me.qping.utils.database.util.CrudUtil;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @ClassName TestQueryOne
 * @Description TODO
 * @Author qping
 * @Date 2020/1/16 11:23
 * @Version 1.0
 **/
public class TestQueryOne {


    @Test
    public void testQueryOne() throws ClassNotFoundException, IllegalAccessException, SQLException, InstantiationException, OrmException {

        CrudUtil crud = DataBaseUtilBuilder.init(
                "jdbc:mysql://127.0.0.1:3306/?useUnicode=true&characterEncoding=UTF-8&tinyInt1isBit=false",
                "",
                ""
        ).buildCrudUtil();


        HisPatientVO vo = crud.queryOne(HisPatientVO.class, "select * from V_PATIENT_INFO_MZ where PATIENT_NO = ? and PATIENT_TYPE = ?", "123", 1);
        System.out.println(vo);


    }
}

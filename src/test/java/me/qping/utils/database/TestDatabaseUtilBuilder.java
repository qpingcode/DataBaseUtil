package me.qping.utils.database;

import me.qping.common.model.DataRecord;
import me.qping.utils.database.connect.impl.Dameng;
import me.qping.utils.database.util.CrudUtil;
import me.qping.utils.database.util.MetaDataUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.crypto.interfaces.DHPublicKey;
import java.sql.*;
import java.util.List;

//@RunWith(PowerMockRunner.class)
//@PrepareForTest({DataBaseUtilBuilder.class})
//@PowerMockIgnore({"javax.management.*"})
public class TestDatabaseUtilBuilder {

    @Test
    public void TestDameng() throws ClassNotFoundException {

        CrudUtil crudUtil = DataBaseUtilBuilder.dameng("localhost", "5236", "SYSDBA", "123456789").buildCrudUtil();
        try {
            List<DataRecord> list = crudUtil.queryList("select * from TEST");
            System.out.println(list);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }
}

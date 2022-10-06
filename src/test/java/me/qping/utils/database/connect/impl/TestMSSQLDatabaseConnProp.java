package me.qping.utils.database.connect.impl;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

public class TestMSSQLDatabaseConnProp {

    @Test
    public void TestGetUrl(){
        MSSQLDataBaseConnProp sqlserver = new MSSQLDataBaseConnProp("localhost", "1433", "test", "testuser", "testpasswd");
        Assert.assertEquals("jdbc:sqlserver://localhost:1433;DatabaseName=test", sqlserver.getUrl());

        sqlserver.setPort(null);
        Assert.assertEquals("jdbc:sqlserver://localhost;DatabaseName=test", sqlserver.getUrl());

        sqlserver.setHost("localhost\\ris");
        sqlserver.setPort("14330");
        Assert.assertEquals("jdbc:sqlserver://localhost:14330;DatabaseName=test;instanceName=ris", sqlserver.getUrl());

        sqlserver.setPort(null);
        Assert.assertEquals("jdbc:sqlserver://localhost;DatabaseName=test;instanceName=ris", sqlserver.getUrl());

        sqlserver.setPort("null");
        Assert.assertEquals("jdbc:sqlserver://localhost;DatabaseName=test;instanceName=ris", sqlserver.getUrl());
    }

    public void TestGetDataBaseType(){
        Assert.assertEquals("com.microsoft.sqlserver.jdbc.SQLServerDriver", new MSSQLDataBaseConnProp().getDriver());
    }

}

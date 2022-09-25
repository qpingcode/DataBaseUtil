package me.qping.utils.database.connect.impl;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Dameng.class})
public class TestDameng {

    @Test
    public void TestGetUrl(){
        Dameng dameng = new Dameng("localhost", "5236", "SYSDBA", "SYSDBA");
        Assert.assertEquals("jdbc:dm://localhost:5236", dameng.getUrl());
    }

    public void TestGetDataBaseType(){
        Assert.assertEquals("dm.jdbc.driver.DmDriver", new Dameng().getDriver());
    }

}

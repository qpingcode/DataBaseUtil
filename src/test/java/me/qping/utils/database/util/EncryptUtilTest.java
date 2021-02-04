package me.qping.utils.database.util;

import org.junit.Test;

public class EncryptUtilTest {


    @Test
    public void de() throws Exception {
        EncryptUtil u = new EncryptUtil();
        u.setPassword("Rxthinking@Nj2020");
        System.out.println(u.de("HyOdVDayOUJqZYwaBJXd3KK8CsmhCGHG"));
    }
}

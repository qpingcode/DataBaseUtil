package me.qping.utils.database.util;

import org.junit.Test;

/**
 * @ClassName EncrptUtilTest
 * @Description TODO
 * @Author qping
 * @Date 2021/1/25 15:04
 * @Version 1.0
 **/
public class EncrptUtilTest {

    @Test
    public void test(){

        EncryptUtil u = new EncryptUtil();
        u.setPassword("Rxthinking@Nj2020");

        try {
            u.de("HyOdVDayOUJqZYwaBJXd3KK8CsmhCGHG");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

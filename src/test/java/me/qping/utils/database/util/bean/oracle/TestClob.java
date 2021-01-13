package me.qping.utils.database.util.bean.oracle;

import me.qping.utils.database.bean.DataBaseColumn;

/**
 * @ClassName TestClob
 * @Description TODO
 * @Author qping
 * @Date 2021/1/13 11:33
 * @Version 1.0
 **/
public class TestClob {

    @DataBaseColumn("id")
    Integer id;

    @DataBaseColumn("name")
    String name;

    @DataBaseColumn("log_c")
    String logC;

    @DataBaseColumn("log_b")
    String logB;

    Byte[] aaa = new Byte[1];


    public static void main(String[] args) {
        TestClob t = new TestClob();
//        System.out.println(t.aaa.getClass().isArray());

        System.out.println(t.aaa.getClass().equals(Byte[].class) );

        byte[] b = new byte[10];
        Byte[] B = new Byte[10];



    }

}

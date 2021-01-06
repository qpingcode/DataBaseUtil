package me.qping.utils.database.util;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentPBEConfig;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @ClassName EncryptUtil
 * @Description 密码加密解密模块
 * @Author qping
 * @Date 2020/12/4 11:03
 * @Version 1.0
 **/
public class EncryptUtil {

    String passwordPattern="^ENC(.*)$";

    String password = "abcdefg@123#!@!@#$%^&*()_+=-0987654321`~";

    public void setPasswordPattern(String passwordPattern){
        this.passwordPattern = passwordPattern;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public String en(String pwd) throws Exception {
        StandardPBEStringEncryptor standardPBEStringEncryptor = new StandardPBEStringEncryptor();
        EnvironmentPBEConfig config = new EnvironmentPBEConfig();

        config.setAlgorithm("PBEWithMD5AndDES");          // 加密的算法，这个算法是默认的
        config.setPassword(password);                        // 加密的密钥，随便自己填写，很重要千万不要告诉别人
        standardPBEStringEncryptor.setConfig(config);
        String plainText = pwd;         //自己的密码
        String encryptedText = standardPBEStringEncryptor.encrypt(plainText);
        return encryptedText;
    }

    public String de(String enc) throws Exception {
        StandardPBEStringEncryptor standardPBEStringEncryptor = new StandardPBEStringEncryptor();
        EnvironmentPBEConfig config = new EnvironmentPBEConfig();

        config.setAlgorithm("PBEWithMD5AndDES");
        config.setPassword(password);
        standardPBEStringEncryptor.setConfig(config);
        String encryptedText = enc;   //加密后的密码
        String plainText = standardPBEStringEncryptor.decrypt(encryptedText);
        System.out.println(plainText);
        return plainText;
    }

    public String extract(String txt){


        return  null;

    }


//    public static void main(String[] args) {
//        EncryptUtil u = new EncryptUtil();
//
//
//    }

    //考虑一般缓存行大小是64字节，一个 long 类型占8字节
    static  long[][] arr;

    public static void main(String[] args) {
        arr = new long[1024 * 1024][];
        for (int i = 0; i < 1024 * 1024; i++) {
            arr[i] = new long[8];
            for (int j = 0; j < 8; j++) {
                arr[i][j] = 0L;
            }
        }
        long sum = 0L;
        long marked = System.currentTimeMillis();
        for (int i = 0; i < 1024 * 1024; i+=1) {
            for(int j =0; j< 8;j++){
                sum = arr[i][j];
            }
        }
        System.out.println("Loop times:" + (System.currentTimeMillis() - marked) + "ms");

        marked = System.currentTimeMillis();
        for (int i = 0; i < 8; i+=1) {
            for(int j =0; j< 1024 * 1024;j++){
                sum = arr[j][i];
            }
        }
        System.out.println("Loop times:" + (System.currentTimeMillis() - marked) + "ms");
    }

}

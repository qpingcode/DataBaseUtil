package me.qping.utils.database.exception;

/**
 * @ClassName OrmException
 * @Description Orm转换错误
 * @Author qping
 * @Date 2020/1/16 11:37
 * @Version 1.0
 **/
public class OrmException extends RuntimeException {
    public OrmException(String msg) {
        super(msg);
    }
}

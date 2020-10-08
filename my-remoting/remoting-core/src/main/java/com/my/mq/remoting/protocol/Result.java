package com.my.mq.remoting.protocol;

/**
 * @author handx
 * @version 1.0.0
 * @ClassName Result.java
 * @Description 返回结果
 * @createTime 2020年09月30日 23:12:00
 */
public class Result {

    private int code;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}

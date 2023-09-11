package com.example.random.domain.common.exception;

public class NewException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private int code;

    private Object data;

    /**
     * 默认异常
     */
    public NewException(){
        this(200,"请求成功");
    }

    public NewException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    public NewException(int code, String msg, Object data) {
        super(msg);
        this.code = code;
        this.data = data;
    }

    public NewException(String message) {
        super(message);
        code = 420;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}

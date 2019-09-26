package com.aaron.mvvmlibrary.exception;

public class ResponseThrowable extends Exception {
    /**
     * 错误码
     */
    private int code;
    /**
     * 错误提示信息
     */
    private String message;
    /**
     * 真实的错误信息
     */
    private String errorMessage;

    public ResponseThrowable(Throwable throwable, int code) {
        super(throwable);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}

package com.osozos.community.common;

public class Result<T> {
    private int code;
    private T data;
    private String message;

    private Result(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    private Result(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static <K> Result<K> success(K data, String message) {
        return new Result<K>(200, data, message);
    }

    public static <K> Result<K> failed(String message) {
        return new Result<K>(404, message);
    }

}

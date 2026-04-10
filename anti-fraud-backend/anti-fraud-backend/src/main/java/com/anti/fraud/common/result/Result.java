package com.anti.fraud.common.result;

import lombok.Data;
import java.io.Serializable;

/**
 * 统一响应结果封装类
 * 
 * 使用规范：
 * - success()           : 无返回值的成功响应
 * - success(T data)     : 仅返回数据的成功响应
 * - success(String message) : 仅返回消息的成功响应
 * - success(String message, T data) : 返回消息和数据的成功响应
 * - fail()              : 默认失败响应
 * - fail(String message) : 返回错误消息的失败响应
 * - fail(Integer code, String message) : 返回指定错误码和消息的失败响应
 * 
 * @param <T> 数据类型
 */
@Data
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /** 响应码：200成功，其他为错误 */
    private Integer code;
    /** 响应消息 */
    private String message;
    /** 响应数据 */
    private T data;
    /** 时间戳 */
    private Long timestamp;

    public Result() {
        this.timestamp = System.currentTimeMillis();
    }

    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    // ==================== 成功响应 ====================
    
    /**
     * 无返回值的成功响应
     * @param <T> 泛型类型
     * @return Result实例
     */
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null);
    }

    /**
     * 仅返回数据的成功响应
     * @param data 响应数据
     * @param <T> 数据类型
     * @return Result实例
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    /**
     * 返回消息和数据的成功响应（推荐使用）
     * @param message 响应消息
     * @param data 响应数据
     * @param <T> 数据类型
     * @return Result实例
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    /**
     * 仅返回消息的成功响应
     * @param message 响应消息
     * @param <T> 泛型类型
     * @return Result实例
     */
    @SuppressWarnings("unchecked")
    public static <T> Result<T> successMsg(String message) {
        return (Result<T>) new Result<Void>(200, message, null);
    }

    // ==================== 失败响应 ====================

    /**
     * 默认失败响应
     * @param <T> 泛型类型
     * @return Result实例
     */
    public static <T> Result<T> fail() {
        return new Result<>(500, "操作失败", null);
    }

    /**
     * 返回错误消息的失败响应
     * @param message 错误消息
     * @param <T> 泛型类型
     * @return Result实例
     */
    public static <T> Result<T> fail(String message) {
        return new Result<>(500, message, null);
    }

    /**
     * 返回指定错误码和消息的失败响应
     * @param code 错误码
     * @param message 错误消息
     * @param <T> 泛型类型
     * @return Result实例
     */
    public static <T> Result<T> fail(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    // ==================== 认证相关响应 ====================

    /**
     * 未授权响应
     * @param message 错误消息
     * @param <T> 泛型类型
     * @return Result实例
     */
    public static <T> Result<T> unauthorized(String message) {
        return new Result<>(401, message, null);
    }

    /**
     * 禁止访问响应
     * @param message 错误消息
     * @param <T> 泛型类型
     * @return Result实例
     */
    public static <T> Result<T> forbidden(String message) {
        return new Result<>(403, message, null);
    }
}

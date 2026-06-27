package com.dms.server.entity.common;

import lombok.Data;

/**
 * 统一响应结果封装
 * 格式：{ code, msg, data, total }
 *
 * @param <T> 响应数据类型
 * @author dms
 */
@Data
public class Result<T> {

    /** 状态码，200=成功 */
    private Integer code;

    /** 提示信息 */
    private String msg;

    /** 业务数据 */
    private T data;

    /** 列表总条数（非列表接口为 0） */
    private Long total;

    // ==================== 成功响应 ====================

    /**
     * 成功（无数据）
     */
    public static <T> Result<T> success() {
        return build(200, "操作成功", null, 0L);
    }

    /**
     * 成功（有数据，无分页）
     */
    public static <T> Result<T> success(T data) {
        return build(200, "操作成功", data, 0L);
    }

    /**
     * 成功（有数据，有分页总数）
     */
    public static <T> Result<T> success(T data, Long total) {
        return build(200, "操作成功", data, total);
    }

    /**
     * 成功（自定义提示信息）
     */
    public static <T> Result<T> success(String msg, T data) {
        return build(200, msg, data, 0L);
    }

    // ==================== 失败响应 ====================

    /**
     * 失败
     */
    public static <T> Result<T> error(String msg) {
        return build(500, msg, null, 0L);
    }

    /**
     * 失败（自定义错误码）
     */
    public static <T> Result<T> error(Integer code, String msg) {
        return build(code, msg, null, 0L);
    }

    // ==================== 私有构建方法 ====================

    private static <T> Result<T> build(Integer code, String msg, T data, Long total) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        result.setTotal(total);
        return result;
    }
}

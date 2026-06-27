package com.dms.server.constant;

/**
 * 公共常量定义
 *
 * @author dms
 */
public final class CommonConstants {

    private CommonConstants() {}

    // ==================== 寮类型 ====================
    public static final String DORM_TYPE_MALE   = "male";
    public static final String DORM_TYPE_FEMALE = "female";
    public static final String DORM_TYPE_MIXED  = "mixed";

    // ==================== 社员类型 ====================
    public static final String EMP_TYPE_JP = "jp";
    public static final String EMP_TYPE_CN = "cn";

    // ==================== 社员来源地 ====================
    public static final String EMP_ORIGIN_SHENYANG = "shenyang";
    public static final String EMP_ORIGIN_DALIAN   = "dalian";

    // ==================== 寮費部门显示名（中国社员） ====================
    public static final String EMP_DEPT_SHENYANG = "瀋陽";
    public static final String EMP_DEPT_DALIAN   = "大連";

    // ==================== 部屋状态 ====================
    public static final String ROOM_STATUS_VACANT   = "vacant";
    public static final String ROOM_STATUS_OCCUPIED = "occupied";

    // ==================== 入退寮状态 ====================
    public static final String OCCUPANCY_STATUS_ACTIVE      = "active";
    public static final String OCCUPANCY_STATUS_CHECKED_OUT = "checked_out";

    // ==================== 寮費状态 ====================
    public static final String FEE_STATUS_PENDING   = "pending";
    public static final String FEE_STATUS_CONFIRMED = "confirmed";

    // ==================== 逻辑删除 ====================
    public static final Integer NOT_DELETED = 0;
    public static final Integer DELETED     = 1;

    // ==================== 寮費单价 ====================
    public static final Integer FEE_UNIT_PRICE = 2000;

    // ==================== 提示信息 ====================
    public static final String MSG_SUCCESS              = "操作成功";
    public static final String MSG_DORM_NOT_FOUND       = "寮情報が存在しません";
    public static final String MSG_EMP_NOT_FOUND        = "社員情報が存在しません";
    public static final String MSG_EMP_ID_EXISTS        = "工号已存在，请勿重复录入";
    public static final String MSG_ROOM_NOT_FOUND       = "部屋情報が存在しません";
    public static final String MSG_ROOM_OCCUPIED        = "该部屋已有人入居，无法办理入寮";
    public static final String MSG_NOT_ACTIVE           = "该入居记录不是入居中状态，无法退寮";
    public static final String MSG_OCCUPANCY_NOT_FOUND  = "入居記録が存在しません";
    public static final String MSG_FEE_NOT_FOUND        = "寮費記録が存在しません";
    public static final String MSG_FEE_CONFIRMED        = "已是确定状态，无需重复操作";
    public static final String MSG_YEAR_MONTH_REQUIRED  = "年月不能为空";
}

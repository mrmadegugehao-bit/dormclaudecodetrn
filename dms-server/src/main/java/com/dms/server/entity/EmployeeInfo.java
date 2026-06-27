package com.dms.server.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 社員信息实体，对应表 employee_info
 * 日本社員（type=jp）和中国出张社員（type=cn）共用此表
 *
 * @author dms
 */
@Data
public class EmployeeInfo {

    /** 主键ID */
    private Long id;

    /** 工号（如 E001 / C001） */
    private String empId;

    /** 姓名 */
    private String name;

    /** 类型：jp=日本社員 / cn=中国出张社員 */
    private String type;

    /** 性别：male / female */
    private String gender;

    // ==================== 日本社員专属字段 ====================

    /** 所属部门（jp） */
    private String dept;

    /** 所属课（jp） */
    private String division;

    /** 雇用形态：regular / contract（jp） */
    private String empType;

    /** 手机号（jp） */
    private String mobile;

    /** 邮箱（jp） */
    private String email;

    /** 首次入寮日期（jp） */
    private LocalDate firstCheckIn;

    // ==================== 中国社員专属字段 ====================

    /** 来源地：shenyang / dalian（cn） */
    private String origin;

    /** 出张类型：longterm / shortterm（cn） */
    private String bizType;

    /** 来日日期（cn） */
    private LocalDate arrivalDate;

    /** 预定回国日期（cn） */
    private LocalDate departureDate;

    // ==================== 公共字段 ====================

    /** 逻辑删除：0=正常，1=已删除 */
    private Integer isDeleted;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}

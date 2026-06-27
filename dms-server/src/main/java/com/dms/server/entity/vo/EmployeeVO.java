package com.dms.server.entity.vo;

import lombok.Data;
import java.time.LocalDate;

/**
 * 社員响应 VO（日本/中国社員共用）
 *
 * @author dms
 */
@Data
public class EmployeeVO {

    /** 社員ID */
    private Long id;

    /** 工号 */
    private String empId;

    /** 姓名 */
    private String name;

    /** 类型：jp / cn */
    private String type;

    /** 性别：male / female */
    private String gender;

    // ==================== 日本社員字段 ====================

    /** 所属部门（jp） */
    private String dept;

    /** 所属课（jp） */
    private String division;

    /** 雇用形态（jp） */
    private String empType;

    /** 手机号（jp） */
    private String mobile;

    /** 邮箱（jp） */
    private String email;

    /** 首次入寮日期（jp） */
    private LocalDate firstCheckIn;

    // ==================== 中国社員字段 ====================

    /** 来源地（cn） */
    private String origin;

    /** 出张类型（cn） */
    private String bizType;

    /** 来日日期（cn） */
    private LocalDate arrivalDate;

    /** 预定回国日期（cn） */
    private LocalDate departureDate;
}

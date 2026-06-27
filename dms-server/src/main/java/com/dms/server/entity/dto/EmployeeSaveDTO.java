package com.dms.server.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

/**
 * 社員新增/编辑入参 DTO（日本/中国社員共用）
 *
 * @author dms
 */
@Data
public class EmployeeSaveDTO {

    /** 工号 */
    @NotBlank(message = "工号不能为空")
    private String empId;

    /** 姓名 */
    @NotBlank(message = "姓名不能为空")
    private String name;

    /** 类型：jp / cn */
    @NotBlank(message = "社員类型不能为空")
    private String type;

    /** 性别：male / female */
    private String gender;

    // ==================== 日本社員字段 ====================

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

    // ==================== 中国社員字段 ====================

    /** 来源地：shenyang / dalian（cn） */
    private String origin;

    /** 出张类型：longterm / shortterm（cn） */
    private String bizType;

    /** 来日日期（cn） */
    private LocalDate arrivalDate;

    /** 预定回国日期（cn） */
    private LocalDate departureDate;
}

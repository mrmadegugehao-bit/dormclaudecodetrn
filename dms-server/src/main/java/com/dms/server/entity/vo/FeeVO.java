package com.dms.server.entity.vo;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 寮費记录响应 VO
 *
 * @author dms
 */
@Data
public class FeeVO {

    /** 记录ID */
    private Long id;

    /** 年份 */
    private Integer year;

    /** 月份 */
    private Integer month;

    /** 社員ID */
    private Long empId;

    /** 社員姓名 */
    private String empName;

    /** 部门（日本社員为课名，中国社員为瀋陽/大連） */
    private String empDept;

    /** 寮名称 */
    private String dormName;

    /** 部屋名称 */
    private String roomName;

    /** 面积（㎡） */
    private BigDecimal area;

    /** 入居天数 */
    private Integer days;

    /** 当月总天数 */
    private Integer totalDays;

    /** 单价（円/㎡） */
    private Integer unitPrice;

    /** 寮費金额（円） */
    private Integer amount;

    /** 状态：pending / confirmed */
    private String status;
}

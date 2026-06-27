package com.dms.server.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 寮費记录实体，对应表 fee_record
 *
 * @author dms
 */
@Data
public class FeeRecord {

    /** 主键ID */
    private Long id;

    /** 年份 */
    private Integer year;

    /** 月份（1-12） */
    private Integer month;

    /** 社員ID */
    private Long empId;

    /** 社員姓名（冗余） */
    private String empName;

    /** 部门（冗余，日本社員为课名，中国社員为瀋陽/大連） */
    private String empDept;

    /** 寮名称（冗余） */
    private String dormName;

    /** 部屋名称（冗余） */
    private String roomName;

    /** 面积（㎡） */
    private BigDecimal area;

    /** 入居天数 */
    private Integer days;

    /** 当月总天数 */
    private Integer totalDays;

    /** 单价（円/㎡），固定 2000 */
    private Integer unitPrice;

    /** 寮費金额（円） */
    private Integer amount;

    /** 状态：pending=待确定 / confirmed=已确定 */
    private String status;

    /** 逻辑删除：0=正常，1=已删除 */
    private Integer isDeleted;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}

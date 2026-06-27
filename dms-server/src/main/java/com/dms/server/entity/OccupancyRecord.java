package com.dms.server.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 入退寮记录实体，对应表 occupancy_record
 *
 * @author dms
 */
@Data
public class OccupancyRecord {

    /** 主键ID */
    private Long id;

    /** 社員ID */
    private Long empId;

    /** 社員姓名（冗余） */
    private String empName;

    /** 社員类型：jp / cn（冗余） */
    private String empType;

    /** 寮ID（冗余） */
    private Long dormId;

    /** 寮名称（冗余） */
    private String dormName;

    /** 部屋ID */
    private Long roomId;

    /** 部屋名称（冗余） */
    private String roomName;

    /** 入寮日期 */
    private LocalDate checkInDate;

    /** 退寮日期（null=入居中） */
    private LocalDate checkOutDate;

    /** 状态：active=入居中 / checked_out=已退寮 */
    private String status;

    /** 退寮事由（null=入居中） */
    private String reason;

    /** 逻辑删除：0=正常，1=已删除 */
    private Integer isDeleted;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}

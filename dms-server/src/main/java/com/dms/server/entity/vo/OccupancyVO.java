package com.dms.server.entity.vo;

import lombok.Data;
import java.time.LocalDate;

/**
 * 入退寮记录响应 VO
 *
 * @author dms
 */
@Data
public class OccupancyVO {

    /** 记录ID */
    private Long id;

    /** 社員ID */
    private Long empId;

    /** 社員姓名 */
    private String empName;

    /** 社員类型：jp / cn */
    private String empType;

    /** 寮ID */
    private Long dormId;

    /** 寮名称 */
    private String dormName;

    /** 部屋ID */
    private Long roomId;

    /** 部屋名称 */
    private String roomName;

    /** 入寮日期 */
    private LocalDate checkInDate;

    /** 退寮日期（null=入居中） */
    private LocalDate checkOutDate;

    /** 状态：active / checked_out */
    private String status;

    /** 退寮事由 */
    private String reason;
}

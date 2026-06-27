package com.dms.server.entity.dto;

import lombok.Data;

/**
 * 入退寮列表查询入参 DTO
 *
 * @author dms
 */
@Data
public class OccupancyQueryDTO {

    /** 社員姓名关键字（模糊匹配） */
    private String empName;

    /** 寮名称关键字（模糊匹配） */
    private String dormName;

    /** 状态：active / checked_out */
    private String status;

    /** 入寮日期起始（格式 yyyy-MM-dd） */
    private String checkInStart;

    /** 入寮日期结束（格式 yyyy-MM-dd） */
    private String checkInEnd;

    /** 页码，默认 1 */
    private Integer page = 1;

    /** 每页条数，默认 10 */
    private Integer pageSize = 10;

    /** 分页偏移量（由 Service 层计算并设置，XML 用 #{offset}） */
    private Integer offset = 0;
}

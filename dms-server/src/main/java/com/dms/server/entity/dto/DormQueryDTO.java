package com.dms.server.entity.dto;

import lombok.Data;

/**
 * 寮列表查询入参 DTO
 *
 * @author dms
 */
@Data
public class DormQueryDTO {

    /** 寮名称关键字（模糊匹配） */
    private String name;

    /** 类型：male / female / mixed */
    private String type;

    /** 地区：tokyo / osaka */
    private String region;

    /** 页码，默认 1 */
    private Integer page = 1;

    /** 每页条数，默认 10 */
    private Integer pageSize = 10;

    /** 分页偏移量（由 Service 层计算并设置，XML 用 #{offset}） */
    private Integer offset = 0;
}

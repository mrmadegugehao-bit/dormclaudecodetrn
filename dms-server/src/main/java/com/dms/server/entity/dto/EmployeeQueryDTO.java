package com.dms.server.entity.dto;

import lombok.Data;

/**
 * 社員列表查询入参 DTO
 *
 * @author dms
 */
@Data
public class EmployeeQueryDTO {

    /** 类型：jp=日本社員 / cn=中国出张社員 */
    private String type;

    /** 姓名关键字（模糊匹配） */
    private String name;

    /** 工号关键字（模糊匹配） */
    private String empId;

    /** 页码，默认 1 */
    private Integer page = 1;

    /** 每页条数，默认 10 */
    private Integer pageSize = 10;

    /** 分页偏移量（由 Service 层计算并设置，XML 用 #{offset}） */
    private Integer offset = 0;
}

package com.dms.server.entity.dto;

import lombok.Data;
import java.util.List;

/**
 * 空き室列表查询入参 DTO
 *
 * @author dms
 */
@Data
public class VacancyQueryDTO {

    /** 性别：male / female（不传或 all 则不过滤） */
    private String gender;

    /** 地区列表：["tokyo","osaka"]（不传则不过滤） */
    private List<String> region;

    /** 部屋状态：vacant / occupied（不传则不过滤） */
    private String status;

    /** 页码，默认 1 */
    private Integer page = 1;

    /** 每页条数，默认 20 */
    private Integer pageSize = 20;

    /** 分页偏移量（由 Service 层计算并设置，XML 用 #{offset}） */
    private Integer offset = 0;
}

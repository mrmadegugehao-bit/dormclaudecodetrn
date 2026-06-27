package com.dms.server.entity.vo;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 空き室（部屋）响应 VO
 *
 * @author dms
 */
@Data
public class VacancyVO {

    /** 部屋ID */
    private Long id;

    /** 所属寮ID */
    private Long dormId;

    /** 所属寮名称 */
    private String dormName;

    /** 部屋名称 */
    private String name;

    /** 面积（㎡） */
    private BigDecimal area;

    /** 状态：vacant / occupied */
    private String status;

    /** 适用性别：male / female */
    private String gender;

    /** 寮类型（由关联寮数据填充） */
    private String dormType;

    /** 寮地区（由关联寮数据填充） */
    private String dormRegion;

    /** 寮户型（由关联寮数据填充） */
    private String dormLayout;
}

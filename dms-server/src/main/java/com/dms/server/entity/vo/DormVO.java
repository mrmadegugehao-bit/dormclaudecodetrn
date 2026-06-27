package com.dms.server.entity.vo;

import lombok.Data;

/**
 * 寮列表响应 VO
 *
 * @author dms
 */
@Data
public class DormVO {

    /** 寮ID */
    private Long id;

    /** 寮名称 */
    private String name;

    /** 地址 */
    private String address;

    /** 类型：male / female / mixed */
    private String type;

    /** 户型 */
    private String layout;

    /** 地区：tokyo / osaka */
    private String region;

    /** 总部屋数 */
    private Integer totalRooms;

    /** 状态：1=启用 */
    private Integer status;

    /** 当前入居部屋数（聚合计算） */
    private Integer occupiedRooms;

    /** 部屋总数（冗余展示） */
    private Integer roomCount;
}

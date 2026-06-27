package com.dms.server.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 寮信息实体，对应表 dorm_info
 *
 * @author dms
 */
@Data
public class DormInfo {

    /** 主键ID */
    private Long id;

    /** 寮名称 */
    private String name;

    /** 地址 */
    private String address;

    /** 类型：male / female / mixed */
    private String type;

    /** 户型：1K / 1DK / 2DK / 3DK / 2LDK / 3LDK */
    private String layout;

    /** 地区：tokyo / osaka */
    private String region;

    /** 总部屋数 */
    private Integer totalRooms;

    /** 状态：1=启用，0=停用 */
    private Integer status;

    /** 逻辑删除：0=正常，1=已删除 */
    private Integer isDeleted;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}

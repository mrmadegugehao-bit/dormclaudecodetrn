package com.dms.server.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 部屋信息实体，对应表 room_info
 *
 * @author dms
 */
@Data
public class RoomInfo {

    /** 主键ID */
    private Long id;

    /** 所属寮ID */
    private Long dormId;

    /** 所属寮名称（冗余字段） */
    private String dormName;

    /** 部屋名称 */
    private String name;

    /** 面积（㎡） */
    private BigDecimal area;

    /** 状态：vacant=空き / occupied=入居中 */
    private String status;

    /** 适用性别：male / female */
    private String gender;

    /** 逻辑删除：0=正常，1=已删除 */
    private Integer isDeleted;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}

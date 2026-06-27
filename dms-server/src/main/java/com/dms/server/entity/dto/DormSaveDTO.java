package com.dms.server.entity.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 寮新增/编辑入参 DTO
 *
 * @author dms
 */
@Data
public class DormSaveDTO {

    /** 寮名称 */
    @NotBlank(message = "寮名称不能为空")
    private String name;

    /** 地址 */
    @NotBlank(message = "地址不能为空")
    private String address;

    /** 类型：male / female / mixed */
    @NotBlank(message = "类型不能为空")
    private String type;

    /** 户型：1K / 1DK / 2DK / 3DK / 2LDK / 3LDK */
    private String layout;

    /** 地区：tokyo / osaka */
    @NotBlank(message = "地区不能为空")
    private String region;

    /** 总部屋数 */
    @NotNull(message = "总部屋数不能为空")
    @Min(value = 1, message = "总部屋数必须大于0")
    private Integer totalRooms;
}

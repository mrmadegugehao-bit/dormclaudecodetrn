package com.dms.server.entity.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 全件确定寮費入参 DTO
 *
 * @author dms
 */
@Data
public class FeeConfirmAllDTO {

    /** 年份 */
    @NotNull(message = "年份不能为空")
    @Min(value = 2000, message = "年份无效")
    private Integer year;

    /** 月份（1-12） */
    @NotNull(message = "月份不能为空")
    @Min(value = 1, message = "月份必须在1-12之间")
    @Max(value = 12, message = "月份必须在1-12之间")
    private Integer month;
}

package com.dms.server.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 入寮登録入参 DTO
 *
 * @author dms
 */
@Data
public class CheckInDTO {

    /** 社員ID */
    @NotNull(message = "社員IDは必須です")
    private Long empId;

    /** 部屋ID（必须为 vacant 状态） */
    @NotNull(message = "部屋IDは必須です")
    private Long roomId;

    /** 入寮日期 */
    @NotNull(message = "入寮日付は必須です")
    private LocalDate checkInDate;
}

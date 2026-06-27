package com.dms.server.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 退寮登録入参 DTO
 *
 * @author dms
 */
@Data
public class CheckOutDTO {

    /** 退寮日期 */
    @NotNull(message = "退寮日付は必須です")
    private LocalDate checkOutDate;

    /** 退寮事由（如：退職 / 異動 / 出張終了） */
    private String reason;
}

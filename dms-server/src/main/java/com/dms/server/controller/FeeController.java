package com.dms.server.controller;

import com.dms.server.entity.common.Result;
import com.dms.server.entity.dto.FeeConfirmAllDTO;
import com.dms.server.entity.vo.FeeVO;
import com.dms.server.service.FeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 寮費管理 Controller
 *
 * @author dms
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api/fee")
public class FeeController {

    private final FeeService feeService;

    public FeeController(FeeService feeService) {
        this.feeService = feeService;
    }

    /**
     * 获取寮費列表 GET /api/fee/list?year=2026&month=6
     */
    @GetMapping("/list")
    public Result<List<FeeVO>> getFeeList(
            @NotNull(message = "年份不能为空") @RequestParam Integer year,
            @NotNull(message = "月份不能为空") @Min(1) @Max(12) @RequestParam Integer month) {
        log.info("获取寮費列表，year={}，month={}", year, month);
        return feeService.getFeeList(year, month);
    }

    /**
     * 计算寮費 POST /api/fee/calculate
     */
    @PostMapping("/calculate")
    public Result<List<FeeVO>> calculateFees(@Valid @RequestBody FeeConfirmAllDTO dto) {
        log.info("计算寮費，year={}，month={}", dto.getYear(), dto.getMonth());
        return feeService.calculateFees(dto.getYear(), dto.getMonth());
    }

    /**
     * 单件确定寮費 PUT /api/fee/{id}/confirm
     */
    @PutMapping("/{id}/confirm")
    public Result<Boolean> confirmFee(@PathVariable Long id) {
        log.info("单件确定寮費，id={}", id);
        return feeService.confirmFee(id);
    }

    /**
     * 全件确定寮費 PUT /api/fee/confirm-all
     */
    @PutMapping("/confirm-all")
    public Result<Boolean> confirmAllFees(@Valid @RequestBody FeeConfirmAllDTO dto) {
        log.info("全件确定寮費，year={}，month={}", dto.getYear(), dto.getMonth());
        return feeService.confirmAllFees(dto.getYear(), dto.getMonth());
    }
}

package com.dms.server.controller;

import com.dms.server.entity.common.Result;
import com.dms.server.entity.dto.CheckInDTO;
import com.dms.server.entity.dto.CheckOutDTO;
import com.dms.server.entity.dto.OccupancyQueryDTO;
import com.dms.server.entity.vo.OccupancyVO;
import com.dms.server.service.OccupancyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 入退寮管理 Controller
 *
 * @author dms
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api/occupancy")
public class OccupancyController {

    private final OccupancyService occupancyService;

    public OccupancyController(OccupancyService occupancyService) {
        this.occupancyService = occupancyService;
    }

    /**
     * 获取入退寮列表 GET /api/occupancy/list
     */
    @GetMapping("/list")
    public Result<List<OccupancyVO>> getOccupancyList(OccupancyQueryDTO query) {
        log.info("获取入退寮列表，参数：{}", query);
        return occupancyService.getOccupancyList(query);
    }

    /**
     * 入寮登録 POST /api/occupancy
     */
    @PostMapping
    public Result<OccupancyVO> addOccupancy(@Valid @RequestBody CheckInDTO dto) {
        log.info("入寮登録，参数：{}", dto);
        return occupancyService.addOccupancy(dto);
    }

    /**
     * 退寮処理 PUT /api/occupancy/{id}/checkout
     */
    @PutMapping("/{id}/checkout")
    public Result<Boolean> checkOut(@PathVariable Long id, @Valid @RequestBody CheckOutDTO dto) {
        log.info("退寮処理，id={}，参数：{}", id, dto);
        return occupancyService.checkOut(id, dto);
    }
}

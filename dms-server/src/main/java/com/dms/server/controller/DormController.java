package com.dms.server.controller;

import com.dms.server.entity.common.Result;
import com.dms.server.entity.dto.DormQueryDTO;
import com.dms.server.entity.dto.DormSaveDTO;
import com.dms.server.entity.vo.DormVO;
import com.dms.server.service.DormService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 寮管理 Controller
 *
 * @author dms
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api/dorm")
public class DormController {

    private final DormService dormService;

    public DormController(DormService dormService) {
        this.dormService = dormService;
    }

    /**
     * 获取寮列表 GET /api/dorm/list
     */
    @GetMapping("/list")
    public Result<List<DormVO>> getDormList(DormQueryDTO query) {
        log.info("获取寮列表，参数：{}", query);
        return dormService.getDormList(query);
    }

    /**
     * 新增寮 POST /api/dorm
     */
    @PostMapping
    public Result<DormVO> addDorm(@Valid @RequestBody DormSaveDTO dto) {
        log.info("新增寮，参数：{}", dto);
        return dormService.addDorm(dto);
    }

    /**
     * 更新寮 PUT /api/dorm/{id}
     */
    @PutMapping("/{id}")
    public Result<DormVO> updateDorm(@PathVariable Long id, @Valid @RequestBody DormSaveDTO dto) {
        log.info("更新寮，id={}，参数：{}", id, dto);
        return dormService.updateDorm(id, dto);
    }

    /**
     * 删除寮 DELETE /api/dorm/{id}
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteDorm(@PathVariable Long id) {
        log.info("删除寮，id={}", id);
        return dormService.deleteDorm(id);
    }
}

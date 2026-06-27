package com.dms.server.controller;

import com.dms.server.entity.common.Result;
import com.dms.server.entity.dto.VacancyQueryDTO;
import com.dms.server.entity.vo.VacancyVO;
import com.dms.server.service.VacancyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 空き室管理 Controller
 *
 * @author dms
 */
@Slf4j
@RestController
@RequestMapping("/api/vacancy")
public class VacancyController {

    private final VacancyService vacancyService;

    public VacancyController(VacancyService vacancyService) {
        this.vacancyService = vacancyService;
    }

    /**
     * 获取空き室列表 GET /api/vacancy/list
     */
    @GetMapping("/list")
    public Result<List<VacancyVO>> getVacancyList(VacancyQueryDTO query) {
        log.info("获取空き室列表，参数：{}", query);
        return vacancyService.getVacancyList(query);
    }
}

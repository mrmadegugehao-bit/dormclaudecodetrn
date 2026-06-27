package com.dms.server.controller;

import com.dms.server.entity.common.Result;
import com.dms.server.entity.vo.DashboardVO;
import com.dms.server.service.DashboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 仪表盘 Controller
 *
 * @author dms
 */
@Slf4j
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * 获取仪表盘统计数据 GET /api/dashboard/stats
     */
    @GetMapping("/stats")
    public Result<DashboardVO> getDashboardStats() {
        log.info("获取仪表盘统计数据");
        return dashboardService.getDashboardStats();
    }
}

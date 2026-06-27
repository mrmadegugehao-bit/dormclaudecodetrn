package com.dms.server.service;

import com.dms.server.entity.common.Result;
import com.dms.server.entity.vo.DashboardVO;

/**
 * 仪表盘 Service 接口
 *
 * @author dms
 */
public interface DashboardService {

    /**
     * 获取仪表盘统计数据
     */
    Result<DashboardVO> getDashboardStats();
}

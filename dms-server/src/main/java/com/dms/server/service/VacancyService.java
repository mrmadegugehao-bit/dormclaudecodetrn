package com.dms.server.service;

import com.dms.server.entity.common.Result;
import com.dms.server.entity.dto.VacancyQueryDTO;
import com.dms.server.entity.vo.VacancyVO;

import java.util.List;

/**
 * 空き室管理 Service 接口
 *
 * @author dms
 */
public interface VacancyService {

    /**
     * 查询空き室列表（分页）
     */
    Result<List<VacancyVO>> getVacancyList(VacancyQueryDTO query);
}

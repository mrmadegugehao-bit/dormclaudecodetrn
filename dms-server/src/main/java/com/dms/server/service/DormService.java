package com.dms.server.service;

import com.dms.server.entity.common.Result;
import com.dms.server.entity.dto.DormQueryDTO;
import com.dms.server.entity.dto.DormSaveDTO;
import com.dms.server.entity.vo.DormVO;

import java.util.List;

/**
 * 寮管理 Service 接口
 *
 * @author dms
 */
public interface DormService {

    /**
     * 查询寮列表（分页）
     */
    Result<List<DormVO>> getDormList(DormQueryDTO query);

    /**
     * 新增寮
     */
    Result<DormVO> addDorm(DormSaveDTO dto);

    /**
     * 更新寮
     */
    Result<DormVO> updateDorm(Long id, DormSaveDTO dto);

    /**
     * 删除寮（逻辑删除）
     */
    Result<Boolean> deleteDorm(Long id);
}

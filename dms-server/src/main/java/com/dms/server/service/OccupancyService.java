package com.dms.server.service;

import com.dms.server.entity.common.Result;
import com.dms.server.entity.dto.CheckInDTO;
import com.dms.server.entity.dto.CheckOutDTO;
import com.dms.server.entity.dto.OccupancyQueryDTO;
import com.dms.server.entity.vo.OccupancyVO;

import java.util.List;

/**
 * 入退寮管理 Service 接口
 *
 * @author dms
 */
public interface OccupancyService {

    /**
     * 查询入退寮列表（分页）
     */
    Result<List<OccupancyVO>> getOccupancyList(OccupancyQueryDTO query);

    /**
     * 入寮登録
     */
    Result<OccupancyVO> addOccupancy(CheckInDTO dto);

    /**
     * 退寮処理
     */
    Result<Boolean> checkOut(Long id, CheckOutDTO dto);
}

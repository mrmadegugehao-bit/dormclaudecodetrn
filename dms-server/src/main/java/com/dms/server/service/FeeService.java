package com.dms.server.service;

import com.dms.server.entity.common.Result;
import com.dms.server.entity.vo.FeeVO;

import java.util.List;

/**
 * 寮費管理 Service 接口
 *
 * @author dms
 */
public interface FeeService {

    /**
     * 查询指定年月的寮費列表
     */
    Result<List<FeeVO>> getFeeList(Integer year, Integer month);

    /**
     * 计算指定年月的寮費（重新生成）
     */
    Result<List<FeeVO>> calculateFees(Integer year, Integer month);

    /**
     * 单件确定寮費
     */
    Result<Boolean> confirmFee(Long id);

    /**
     * 全件确定指定年月的寮費
     */
    Result<Boolean> confirmAllFees(Integer year, Integer month);
}

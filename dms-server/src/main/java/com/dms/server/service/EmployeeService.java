package com.dms.server.service;

import com.dms.server.entity.common.Result;
import com.dms.server.entity.dto.EmployeeQueryDTO;
import com.dms.server.entity.dto.EmployeeSaveDTO;
import com.dms.server.entity.vo.EmployeeVO;

import java.util.List;

/**
 * 社員管理 Service 接口
 *
 * @author dms
 */
public interface EmployeeService {

    /**
     * 查询社員列表（分页）
     */
    Result<List<EmployeeVO>> getEmployeeList(EmployeeQueryDTO query);

    /**
     * 新增社員
     */
    Result<EmployeeVO> addEmployee(EmployeeSaveDTO dto);

    /**
     * 更新社員
     */
    Result<EmployeeVO> updateEmployee(Long id, EmployeeSaveDTO dto);

    /**
     * 删除社員（逻辑删除）
     */
    Result<Boolean> deleteEmployee(Long id);
}

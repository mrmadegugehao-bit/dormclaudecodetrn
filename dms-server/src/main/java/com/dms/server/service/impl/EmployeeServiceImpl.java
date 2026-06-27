package com.dms.server.service.impl;

import com.dms.server.constant.CommonConstants;
import com.dms.server.entity.EmployeeInfo;
import com.dms.server.entity.common.Result;
import com.dms.server.entity.dto.EmployeeQueryDTO;
import com.dms.server.entity.dto.EmployeeSaveDTO;
import com.dms.server.entity.vo.EmployeeVO;
import com.dms.server.exception.BusinessException;
import com.dms.server.mapper.EmployeeMapper;
import com.dms.server.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 社員管理 Service 实现
 *
 * @author dms
 */
@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeMapper employeeMapper;

    public EmployeeServiceImpl(EmployeeMapper employeeMapper) {
        this.employeeMapper = employeeMapper;
    }

    @Override
    public Result<List<EmployeeVO>> getEmployeeList(EmployeeQueryDTO query) {
        int offset = (query.getPage() - 1) * query.getPageSize();
        query.setOffset(offset);

        List<EmployeeVO> list = employeeMapper.selectEmployeeList(query);
        Long total = employeeMapper.countEmployeeList(query);

        if (list == null) {
            list = new ArrayList<>(0);
        }
        return Result.success(list, total);
    }

    @Override
    public Result<EmployeeVO> addEmployee(EmployeeSaveDTO dto) {
        // 工号全局唯一性校验（DB 有唯一索引，此处提前给出友好提示）
        if (employeeMapper.selectByEmpId(dto.getEmpId()) != null) {
            throw new BusinessException(CommonConstants.MSG_EMP_ID_EXISTS);
        }

        EmployeeInfo employeeInfo = new EmployeeInfo();
        BeanUtils.copyProperties(dto, employeeInfo);

        employeeMapper.insertEmployee(employeeInfo);

        EmployeeVO vo = buildEmployeeVO(employeeInfo);
        return Result.success(vo);
    }

    @Override
    public Result<EmployeeVO> updateEmployee(Long id, EmployeeSaveDTO dto) {
        EmployeeInfo exist = employeeMapper.selectById(id);
        if (exist == null) {
            throw new BusinessException(CommonConstants.MSG_EMP_NOT_FOUND);
        }

        EmployeeInfo employeeInfo = new EmployeeInfo();
        BeanUtils.copyProperties(dto, employeeInfo);
        employeeInfo.setId(id);

        employeeMapper.updateEmployee(employeeInfo);

        EmployeeVO vo = buildEmployeeVO(employeeMapper.selectById(id));
        return Result.success(vo);
    }

    @Override
    public Result<Boolean> deleteEmployee(Long id) {
        EmployeeInfo exist = employeeMapper.selectById(id);
        if (exist == null) {
            throw new BusinessException(CommonConstants.MSG_EMP_NOT_FOUND);
        }

        employeeMapper.deleteEmployeeById(id);

        return Result.success(Boolean.TRUE);
    }

    /**
     * 将 EmployeeInfo 转换为 EmployeeVO
     */
    private EmployeeVO buildEmployeeVO(EmployeeInfo info) {
        if (info == null) {
            return null;
        }
        EmployeeVO vo = new EmployeeVO();
        BeanUtils.copyProperties(info, vo);
        return vo;
    }
}

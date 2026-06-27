package com.dms.server.mapper;

import com.dms.server.entity.EmployeeInfo;
import com.dms.server.entity.dto.EmployeeQueryDTO;
import com.dms.server.entity.vo.EmployeeVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 社員持久层接口
 *
 * @author dms
 */
public interface EmployeeMapper {

    /**
     * 查询社員列表（带条件过滤）
     */
    List<EmployeeVO> selectEmployeeList(EmployeeQueryDTO query);

    /**
     * 查询社員总数
     */
    Long countEmployeeList(EmployeeQueryDTO query);

    /**
     * 根据ID查询社員
     */
    EmployeeInfo selectById(@Param("id") Long id);

    /**
     * 新增社員
     */
    int insertEmployee(EmployeeInfo employeeInfo);

    /**
     * 更新社員
     */
    int updateEmployee(EmployeeInfo employeeInfo);

    /**
     * 根据工号查询社員（用于唯一性校验）
     */
    EmployeeInfo selectByEmpId(@Param("empId") String empId);

    /**
     * 逻辑删除社員
     */
    int deleteEmployeeById(@Param("id") Long id);
}

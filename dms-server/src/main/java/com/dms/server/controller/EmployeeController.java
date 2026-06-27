package com.dms.server.controller;

import com.dms.server.entity.common.Result;
import com.dms.server.entity.dto.EmployeeQueryDTO;
import com.dms.server.entity.dto.EmployeeSaveDTO;
import com.dms.server.entity.vo.EmployeeVO;
import com.dms.server.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 社員管理 Controller
 *
 * @author dms
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * 获取社員列表 GET /api/employee/list
     */
    @GetMapping("/list")
    public Result<List<EmployeeVO>> getEmployeeList(EmployeeQueryDTO query) {
        log.info("获取社員列表，参数：{}", query);
        return employeeService.getEmployeeList(query);
    }

    /**
     * 新增社員 POST /api/employee
     */
    @PostMapping
    public Result<EmployeeVO> addEmployee(@Valid @RequestBody EmployeeSaveDTO dto) {
        log.info("新增社員，参数：{}", dto);
        return employeeService.addEmployee(dto);
    }

    /**
     * 更新社員 PUT /api/employee/{id}
     */
    @PutMapping("/{id}")
    public Result<EmployeeVO> updateEmployee(@PathVariable Long id, @Valid @RequestBody EmployeeSaveDTO dto) {
        log.info("更新社員，id={}，参数：{}", id, dto);
        return employeeService.updateEmployee(id, dto);
    }

    /**
     * 删除社員 DELETE /api/employee/{id}
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteEmployee(@PathVariable Long id) {
        log.info("删除社員，id={}", id);
        return employeeService.deleteEmployee(id);
    }
}

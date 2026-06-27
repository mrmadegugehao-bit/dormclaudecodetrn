package com.dms.server.service.impl;

import com.dms.server.constant.CommonConstants;
import com.dms.server.entity.EmployeeInfo;
import com.dms.server.entity.FeeRecord;
import com.dms.server.entity.OccupancyRecord;
import com.dms.server.entity.RoomInfo;
import com.dms.server.entity.common.Result;
import com.dms.server.entity.vo.FeeVO;
import com.dms.server.exception.BusinessException;
import com.dms.server.mapper.EmployeeMapper;
import com.dms.server.mapper.FeeMapper;
import com.dms.server.mapper.OccupancyMapper;
import com.dms.server.mapper.RoomMapper;
import com.dms.server.service.FeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * 寮費管理 Service 实现
 *
 * @author dms
 */
@Slf4j
@Service
public class FeeServiceImpl implements FeeService {

    private final FeeMapper feeMapper;
    private final OccupancyMapper occupancyMapper;
    private final RoomMapper roomMapper;
    private final EmployeeMapper employeeMapper;

    public FeeServiceImpl(FeeMapper feeMapper,
                          OccupancyMapper occupancyMapper,
                          RoomMapper roomMapper,
                          EmployeeMapper employeeMapper) {
        this.feeMapper = feeMapper;
        this.occupancyMapper = occupancyMapper;
        this.roomMapper = roomMapper;
        this.employeeMapper = employeeMapper;
    }

    @Override
    public Result<List<FeeVO>> getFeeList(Integer year, Integer month) {
        List<FeeVO> list = feeMapper.selectFeeList(year, month);
        if (list == null) {
            list = new ArrayList<>(0);
        }
        return Result.success(list, (long) list.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<List<FeeVO>> calculateFees(Integer year, Integer month) {
        if (year == null || month == null) {
            throw new BusinessException(CommonConstants.MSG_YEAR_MONTH_REQUIRED);
        }

        // 当月总天数
        int totalDays = LocalDate.of(year, month, 1).lengthOfMonth();

        // 查询当前入居中的所有记录
        List<OccupancyRecord> activeList = occupancyMapper.selectActiveOccupancyList();

        // 删除已有的该月寮費记录（重新计算）
        feeMapper.deleteFeeByYearMonth(year, month);

        List<FeeRecord> feeList = new ArrayList<>(activeList.size());

        for (OccupancyRecord occ : activeList) {
            RoomInfo room = roomMapper.selectById(occ.getRoomId());
            if (room == null) {
                log.warn("部屋不存在，跳过计算 roomId={}", occ.getRoomId());
                continue;
            }
            EmployeeInfo emp = employeeMapper.selectById(occ.getEmpId());

            LocalDate monthStart = LocalDate.of(year, month, 1);
            LocalDate monthEnd   = LocalDate.of(year, month, totalDays);
            LocalDate checkIn    = occ.getCheckInDate();

            // 有效入寮日期：入寮日期晚于月初则取入寮日期，否则取月初
            LocalDate effectiveStart = (checkIn != null && checkIn.isAfter(monthStart)) ? checkIn : monthStart;

            // 计算入居天数（含首尾）
            long days = Math.max(0, ChronoUnit.DAYS.between(effectiveStart, monthEnd) + 1);

            // 寮費 = 面積 × 单价(2000円/㎡) × 天数 / 当月总天数
            int amount = (int) Math.round(
                    room.getArea().doubleValue() * CommonConstants.FEE_UNIT_PRICE * days / totalDays
            );

            FeeRecord fee = new FeeRecord();
            fee.setYear(year);
            fee.setMonth(month);
            fee.setEmpId(occ.getEmpId());
            fee.setEmpName(occ.getEmpName());
            fee.setEmpDept(buildEmpDept(emp));
            fee.setDormName(occ.getDormName());
            fee.setRoomName(occ.getRoomName());
            fee.setArea(room.getArea());
            fee.setDays((int) days);
            fee.setTotalDays(totalDays);
            fee.setUnitPrice(CommonConstants.FEE_UNIT_PRICE);
            fee.setAmount(amount);
            fee.setStatus(CommonConstants.FEE_STATUS_PENDING);
            feeList.add(fee);
        }

        if (!feeList.isEmpty()) {
            feeMapper.insertFeeBatch(feeList);
        }

        // 查询并返回本月寮費列表
        List<FeeVO> result = feeMapper.selectFeeList(year, month);
        if (result == null) {
            result = new ArrayList<>(0);
        }
        return Result.success(result, (long) result.size());
    }

    @Override
    public Result<Boolean> confirmFee(Long id) {
        FeeRecord fee = feeMapper.selectById(id);
        if (fee == null) {
            throw new BusinessException(CommonConstants.MSG_FEE_NOT_FOUND);
        }
        if (CommonConstants.FEE_STATUS_CONFIRMED.equals(fee.getStatus())) {
            throw new BusinessException(CommonConstants.MSG_FEE_CONFIRMED);
        }

        feeMapper.confirmFeeById(id);
        return Result.success(Boolean.TRUE);
    }

    @Override
    public Result<Boolean> confirmAllFees(Integer year, Integer month) {
        if (year == null || month == null) {
            throw new BusinessException(CommonConstants.MSG_YEAR_MONTH_REQUIRED);
        }
        feeMapper.confirmAllFees(year, month);
        return Result.success(Boolean.TRUE);
    }

    /**
     * 根据社員类型构建部门标识
     * - jp：返回 division（所属课）
     * - cn origin=shenyang：返回"瀋陽"
     * - cn 其他来源：返回"大連"
     */
    private String buildEmpDept(EmployeeInfo emp) {
        if (emp == null) {
            return "";
        }
        if (CommonConstants.EMP_TYPE_JP.equals(emp.getType())) {
            return emp.getDivision() != null ? emp.getDivision() : "";
        }
        // cn 类型：按来源地映射部门显示名
        if (CommonConstants.EMP_ORIGIN_SHENYANG.equals(emp.getOrigin())) {
            return CommonConstants.EMP_DEPT_SHENYANG;
        }
        return CommonConstants.EMP_DEPT_DALIAN;
    }
}

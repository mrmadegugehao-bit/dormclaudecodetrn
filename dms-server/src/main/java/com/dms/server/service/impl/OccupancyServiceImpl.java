package com.dms.server.service.impl;

import com.dms.server.constant.CommonConstants;
import com.dms.server.entity.EmployeeInfo;
import com.dms.server.entity.OccupancyRecord;
import com.dms.server.entity.RoomInfo;
import com.dms.server.entity.common.Result;
import com.dms.server.entity.dto.CheckInDTO;
import com.dms.server.entity.dto.CheckOutDTO;
import com.dms.server.entity.dto.OccupancyQueryDTO;
import com.dms.server.entity.vo.OccupancyVO;
import com.dms.server.exception.BusinessException;
import com.dms.server.mapper.EmployeeMapper;
import com.dms.server.mapper.OccupancyMapper;
import com.dms.server.mapper.RoomMapper;
import com.dms.server.service.OccupancyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 入退寮管理 Service 实现
 *
 * @author dms
 */
@Slf4j
@Service
public class OccupancyServiceImpl implements OccupancyService {

    private final OccupancyMapper occupancyMapper;
    private final RoomMapper roomMapper;
    private final EmployeeMapper employeeMapper;

    public OccupancyServiceImpl(OccupancyMapper occupancyMapper,
                                RoomMapper roomMapper,
                                EmployeeMapper employeeMapper) {
        this.occupancyMapper = occupancyMapper;
        this.roomMapper = roomMapper;
        this.employeeMapper = employeeMapper;
    }

    @Override
    public Result<List<OccupancyVO>> getOccupancyList(OccupancyQueryDTO query) {
        int offset = (query.getPage() - 1) * query.getPageSize();
        query.setOffset(offset);

        List<OccupancyVO> list = occupancyMapper.selectOccupancyList(query);
        Long total = occupancyMapper.countOccupancyList(query);

        if (list == null) {
            list = new ArrayList<>(0);
        }
        return Result.success(list, total);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<OccupancyVO> addOccupancy(CheckInDTO dto) {
        // 校验社員是否存在
        EmployeeInfo emp = employeeMapper.selectById(dto.getEmpId());
        if (emp == null) {
            throw new BusinessException(CommonConstants.MSG_EMP_NOT_FOUND);
        }

        // 校验部屋是否存在
        RoomInfo room = roomMapper.selectById(dto.getRoomId());
        if (room == null) {
            throw new BusinessException(CommonConstants.MSG_ROOM_NOT_FOUND);
        }

        // 校验部屋状态是否为空き
        if (!CommonConstants.ROOM_STATUS_VACANT.equals(room.getStatus())) {
            throw new BusinessException(CommonConstants.MSG_ROOM_OCCUPIED);
        }

        // 构建入寮记录
        OccupancyRecord record = new OccupancyRecord();
        record.setEmpId(emp.getId());
        record.setEmpName(emp.getName());
        record.setEmpType(emp.getType());
        record.setDormId(room.getDormId());
        record.setDormName(room.getDormName());
        record.setRoomId(room.getId());
        record.setRoomName(room.getName());
        record.setCheckInDate(dto.getCheckInDate());
        record.setStatus(CommonConstants.OCCUPANCY_STATUS_ACTIVE);

        // 插入入寮记录
        occupancyMapper.insertOccupancy(record);

        // 更新部屋状态为 occupied
        roomMapper.updateRoomStatus(room.getId(), CommonConstants.ROOM_STATUS_OCCUPIED);

        // 构建响应 VO
        OccupancyVO vo = new OccupancyVO();
        BeanUtils.copyProperties(record, vo);
        return Result.success(vo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> checkOut(Long id, CheckOutDTO dto) {
        // 校验记录是否存在
        OccupancyRecord record = occupancyMapper.selectById(id);
        if (record == null) {
            throw new BusinessException(CommonConstants.MSG_OCCUPANCY_NOT_FOUND);
        }

        // 校验状态是否为 active
        if (!CommonConstants.OCCUPANCY_STATUS_ACTIVE.equals(record.getStatus())) {
            throw new BusinessException(CommonConstants.MSG_NOT_ACTIVE);
        }

        // 更新退寮信息
        occupancyMapper.checkOut(id, dto);

        // 更新部屋状态为 vacant
        roomMapper.updateRoomStatus(record.getRoomId(), CommonConstants.ROOM_STATUS_VACANT);

        return Result.success(Boolean.TRUE);
    }
}

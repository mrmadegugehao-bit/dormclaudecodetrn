package com.dms.server.service.impl;

import com.dms.server.constant.CommonConstants;
import com.dms.server.constant.RedisKeyConstants;
import com.dms.server.entity.DormInfo;
import com.dms.server.entity.common.Result;
import com.dms.server.entity.dto.DormQueryDTO;
import com.dms.server.entity.dto.DormSaveDTO;
import com.dms.server.entity.vo.DormVO;
import com.dms.server.exception.BusinessException;
import com.dms.server.mapper.DormMapper;
import com.dms.server.service.DormService;
import com.dms.server.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 寮管理 Service 实现
 *
 * @author dms
 */
@Slf4j
@Service
public class DormServiceImpl implements DormService {

    private final DormMapper dormMapper;
    private final RedisUtil redisUtil;

    public DormServiceImpl(DormMapper dormMapper, RedisUtil redisUtil) {
        this.dormMapper = dormMapper;
        this.redisUtil = redisUtil;
    }

    @Override
    public Result<List<DormVO>> getDormList(DormQueryDTO query) {
        // 计算分页偏移量，存入 offset 字段，XML 中使用 #{offset}
        int offset = (query.getPage() - 1) * query.getPageSize();
        query.setOffset(offset);

        List<DormVO> list = dormMapper.selectDormList(query);
        Long total = dormMapper.countDormList(query);

        if (list == null) {
            list = new ArrayList<>(0);
        }
        return Result.success(list, total);
    }

    @Override
    public Result<DormVO> addDorm(DormSaveDTO dto) {
        DormInfo dormInfo = new DormInfo();
        BeanUtils.copyProperties(dto, dormInfo);

        dormMapper.insertDorm(dormInfo);

        // 清除相关缓存
        redisUtil.delete(RedisKeyConstants.DORM_LIST);

        // 构建返回 VO
        DormVO vo = buildDormVO(dormInfo);
        return Result.success(vo);
    }

    @Override
    public Result<DormVO> updateDorm(Long id, DormSaveDTO dto) {
        DormInfo exist = dormMapper.selectById(id);
        if (exist == null) {
            throw new BusinessException(CommonConstants.MSG_DORM_NOT_FOUND);
        }

        DormInfo dormInfo = new DormInfo();
        BeanUtils.copyProperties(dto, dormInfo);
        dormInfo.setId(id);

        dormMapper.updateDorm(dormInfo);

        // 清除相关缓存
        redisUtil.delete(RedisKeyConstants.DORM_LIST);

        DormVO vo = buildDormVO(dormMapper.selectById(id));
        return Result.success(vo);
    }

    @Override
    public Result<Boolean> deleteDorm(Long id) {
        DormInfo exist = dormMapper.selectById(id);
        if (exist == null) {
            throw new BusinessException(CommonConstants.MSG_DORM_NOT_FOUND);
        }

        dormMapper.deleteDormById(id);

        // 清除相关缓存
        redisUtil.delete(RedisKeyConstants.DORM_LIST);

        return Result.success(Boolean.TRUE);
    }

    /**
     * 将 DormInfo 转换为 DormVO
     */
    private DormVO buildDormVO(DormInfo info) {
        if (info == null) {
            return null;
        }
        DormVO vo = new DormVO();
        BeanUtils.copyProperties(info, vo);
        vo.setOccupiedRooms(0);
        vo.setRoomCount(info.getTotalRooms());
        return vo;
    }
}

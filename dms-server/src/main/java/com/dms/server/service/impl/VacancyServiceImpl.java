package com.dms.server.service.impl;

import com.dms.server.constant.RedisKeyConstants;
import com.dms.server.entity.common.Result;
import com.dms.server.entity.dto.VacancyQueryDTO;
import com.dms.server.entity.vo.VacancyVO;
import com.dms.server.mapper.RoomMapper;
import com.dms.server.service.VacancyService;
import com.dms.server.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 空き室管理 Service 实现
 *
 * @author dms
 */
@Slf4j
@Service
public class VacancyServiceImpl implements VacancyService {

    private final RoomMapper roomMapper;
    private final RedisUtil redisUtil;

    public VacancyServiceImpl(RoomMapper roomMapper, RedisUtil redisUtil) {
        this.roomMapper = roomMapper;
        this.redisUtil = redisUtil;
    }

    @Override
    public Result<List<VacancyVO>> getVacancyList(VacancyQueryDTO query) {
        // 计算分页偏移量，存入 offset 字段，XML 中使用 #{offset}
        int offset = (query.getPage() - 1) * query.getPageSize();
        query.setOffset(offset);

        List<VacancyVO> list = roomMapper.selectVacancyList(query);
        Long total = roomMapper.countVacancyList(query);

        if (list == null) {
            list = new ArrayList<>(0);
        }
        return Result.success(list, total);
    }
}

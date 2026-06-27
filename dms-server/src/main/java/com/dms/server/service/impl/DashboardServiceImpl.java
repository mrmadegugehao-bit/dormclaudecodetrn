package com.dms.server.service.impl;

import com.dms.server.constant.RedisKeyConstants;
import com.dms.server.entity.DormInfo;
import com.dms.server.entity.common.Result;
import com.dms.server.entity.dto.DormQueryDTO;
import com.dms.server.entity.vo.DashboardVO;
import com.dms.server.entity.vo.OccupancyVO;
import com.dms.server.mapper.DormMapper;
import com.dms.server.mapper.OccupancyMapper;
import com.dms.server.mapper.RoomMapper;
import com.dms.server.service.DashboardService;
import com.dms.server.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 仪表盘 Service 实现
 *
 * @author dms
 */
@Slf4j
@Service
public class DashboardServiceImpl implements DashboardService {

    private final RoomMapper roomMapper;
    private final DormMapper dormMapper;
    private final OccupancyMapper occupancyMapper;
    private final RedisUtil redisUtil;

    public DashboardServiceImpl(RoomMapper roomMapper,
                                DormMapper dormMapper,
                                OccupancyMapper occupancyMapper,
                                RedisUtil redisUtil) {
        this.roomMapper = roomMapper;
        this.dormMapper = dormMapper;
        this.occupancyMapper = occupancyMapper;
        this.redisUtil = redisUtil;
    }

    @Override
    public Result<DashboardVO> getDashboardStats() {
        // 优先从 Redis 缓存获取
        Object cached = redisUtil.get(RedisKeyConstants.DASHBOARD_STATS);
        if (cached instanceof DashboardVO) {
            log.debug("仪表盘数据命中缓存");
            return Result.success((DashboardVO) cached);
        }

        DashboardVO vo = new DashboardVO();

        // ===== 1. 整体房间统计 =====
        Integer totalRooms = roomMapper.countTotalRooms();
        Integer occupiedRooms = roomMapper.countTotalOccupied();
        totalRooms = totalRooms != null ? totalRooms : 0;
        occupiedRooms = occupiedRooms != null ? occupiedRooms : 0;
        int vacantRooms = totalRooms - occupiedRooms;
        int occupancyRate = totalRooms > 0
                ? (int) Math.round(occupiedRooms * 100.0 / totalRooms)
                : 0;

        vo.setTotalRooms(totalRooms);
        vo.setOccupiedRooms(occupiedRooms);
        vo.setVacantRooms(vacantRooms);
        vo.setOccupancyRate(occupancyRate);

        // ===== 2. 各寮入居情况（饼图数据） =====
        DormQueryDTO dormQuery = new DormQueryDTO();
        dormQuery.setPage(1);
        dormQuery.setPageSize(9999);
        dormQuery.setOffset(0);
        List<com.dms.server.entity.vo.DormVO> dormVOList = dormMapper.selectDormList(dormQuery);
        List<DashboardVO.DormStatVO> dormStats = new ArrayList<>();
        if (dormVOList != null) {
            for (com.dms.server.entity.vo.DormVO dorm : dormVOList) {
                Integer dormOccupied = roomMapper.countOccupiedByDormId(dorm.getId());
                Integer dormTotal = roomMapper.countRoomsByDormId(dorm.getId());
                dormOccupied = dormOccupied != null ? dormOccupied : 0;
                dormTotal = dormTotal != null ? dormTotal : 0;

                DashboardVO.DormStatVO stat = new DashboardVO.DormStatVO();
                // 图表用换行名称（超过4字时折行）
                stat.setName(formatDormName(dorm.getName()));
                stat.setFullName(dorm.getName());
                stat.setValue(dormOccupied);
                stat.setTotal(dormTotal);
                dormStats.add(stat);
            }
        }
        vo.setDormStats(dormStats);

        // ===== 3. 近10条入退寮记录 =====
        List<OccupancyVO> recentOccupancy = occupancyMapper.selectRecentOccupancy(10);
        vo.setRecentOccupancy(recentOccupancy != null ? recentOccupancy : new ArrayList<>());

        // ===== 4. 近6个月入退寮件数（柱状图） =====
        List<DashboardVO.MonthlyDataVO> monthlyData = occupancyMapper.selectMonthlyStats(6);
        vo.setMonthlyData(monthlyData != null ? monthlyData : new ArrayList<>());

        // ===== 5. 近6个月入居率趋势（折线图） =====
        List<DashboardVO.RateDataVO> rateData = occupancyMapper.selectMonthlyOccupancyRate(6);
        vo.setRateData(rateData != null ? rateData : new ArrayList<>());

        // ===== 6. 长期入居警告（5年以上） =====
        List<DashboardVO.LongTermWarningVO> longTermWarnings = occupancyMapper.selectLongTermWarnings();
        vo.setLongTermWarnings(longTermWarnings != null ? longTermWarnings : new ArrayList<>());

        // ===== 写入 Redis 缓存，TTL=5分钟 =====
        redisUtil.set(RedisKeyConstants.DASHBOARD_STATS, vo, RedisKeyConstants.CACHE_TTL_SHORT);

        return Result.success(vo);
    }

    /**
     * 超过4字的寮名称插入换行符，便于图表显示
     */
    private String formatDormName(String name) {
        if (name == null || name.length() <= 4) {
            return name;
        }
        return name.substring(0, 4) + "\n" + name.substring(4);
    }
}

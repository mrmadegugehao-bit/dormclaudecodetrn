package com.dms.server.mapper;

import com.dms.server.entity.OccupancyRecord;
import com.dms.server.entity.dto.CheckOutDTO;
import com.dms.server.entity.dto.OccupancyQueryDTO;
import com.dms.server.entity.vo.DashboardVO;
import com.dms.server.entity.vo.OccupancyVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 入退寮持久层接口
 *
 * @author dms
 */
public interface OccupancyMapper {

    /**
     * 查询入退寮列表（按ID降序，带条件过滤）
     */
    List<OccupancyVO> selectOccupancyList(OccupancyQueryDTO query);

    /**
     * 查询入退寮总数
     */
    Long countOccupancyList(OccupancyQueryDTO query);

    /**
     * 根据ID查询入退寮记录
     */
    OccupancyRecord selectById(@Param("id") Long id);

    /**
     * 新增入寮记录
     */
    int insertOccupancy(OccupancyRecord occupancyRecord);

    /**
     * 退寮：更新状态、退寮日期和事由
     */
    int checkOut(@Param("id") Long id, @Param("dto") CheckOutDTO dto);

    /**
     * 查询最新N条入退寮记录（仪表盘用）
     */
    List<OccupancyVO> selectRecentOccupancy(@Param("limit") Integer limit);

    /**
     * 查询当前入居中的所有记录（用于计算寮費）
     */
    List<OccupancyRecord> selectActiveOccupancyList();

    /**
     * 查询长期入居警告列表（入居超过5年）
     */
    List<DashboardVO.LongTermWarningVO> selectLongTermWarnings();

    /**
     * 按月统计入退寮件数（近N个月，仪表盘柱状图）
     */
    List<DashboardVO.MonthlyDataVO> selectMonthlyStats(@Param("months") Integer months);

    /**
     * 按月统计入居率（近N个月，仪表盘折线图）
     * 以每月最后一天为时间点，统计当时入居中的部屋数 / 总部屋数
     */
    List<DashboardVO.RateDataVO> selectMonthlyOccupancyRate(@Param("months") int months);
}

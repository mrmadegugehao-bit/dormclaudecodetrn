package com.dms.server.mapper;

import com.dms.server.entity.RoomInfo;
import com.dms.server.entity.dto.VacancyQueryDTO;
import com.dms.server.entity.vo.VacancyVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 部屋持久层接口
 *
 * @author dms
 */
public interface RoomMapper {

    /**
     * 查询部屋列表（关联寮信息，支持筛选）
     */
    List<VacancyVO> selectVacancyList(VacancyQueryDTO query);

    /**
     * 查询部屋总数
     */
    Long countVacancyList(VacancyQueryDTO query);

    /**
     * 根据ID查询部屋
     */
    RoomInfo selectById(@Param("id") Long id);

    /**
     * 更新部屋状态
     */
    int updateRoomStatus(@Param("id") Long id, @Param("status") String status);

    /**
     * 统计指定寮的入居部屋数
     */
    Integer countOccupiedByDormId(@Param("dormId") Long dormId);

    /**
     * 统计指定寮的总部屋数
     */
    Integer countRoomsByDormId(@Param("dormId") Long dormId);

    /**
     * 统计全体总部屋数（仪表盘用）
     */
    Integer countTotalRooms();

    /**
     * 统计全体入居部屋数（仪表盘用）
     */
    Integer countTotalOccupied();
}

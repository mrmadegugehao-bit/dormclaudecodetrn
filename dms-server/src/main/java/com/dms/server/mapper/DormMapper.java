package com.dms.server.mapper;

import com.dms.server.entity.DormInfo;
import com.dms.server.entity.dto.DormQueryDTO;
import com.dms.server.entity.vo.DormVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 寮持久层接口
 *
 * @author dms
 */
public interface DormMapper {

    /**
     * 查询寮列表（带条件过滤，含入居部屋数聚合）
     */
    List<DormVO> selectDormList(DormQueryDTO query);

    /**
     * 查询寮总数（用于分页）
     */
    Long countDormList(DormQueryDTO query);

    /**
     * 根据ID查询寮
     */
    DormInfo selectById(@Param("id") Long id);

    /**
     * 新增寮
     */
    int insertDorm(DormInfo dormInfo);

    /**
     * 更新寮
     */
    int updateDorm(DormInfo dormInfo);

    /**
     * 逻辑删除寮
     */
    int deleteDormById(@Param("id") Long id);
}

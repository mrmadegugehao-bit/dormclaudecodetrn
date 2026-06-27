package com.dms.server.mapper;

import com.dms.server.entity.FeeRecord;
import com.dms.server.entity.vo.FeeVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 寮費持久层接口
 *
 * @author dms
 */
public interface FeeMapper {

    /**
     * 按年月查询寮費列表
     */
    List<FeeVO> selectFeeList(@Param("year") Integer year, @Param("month") Integer month);

    /**
     * 根据ID查询寮費记录
     */
    FeeRecord selectById(@Param("id") Long id);

    /**
     * 批量新增寮費记录
     */
    int insertFeeBatch(@Param("list") List<FeeRecord> list);

    /**
     * 删除指定年月的所有寮費记录（计算前清空）
     */
    int deleteFeeByYearMonth(@Param("year") Integer year, @Param("month") Integer month);

    /**
     * 单件确定：更新状态为 confirmed
     */
    int confirmFeeById(@Param("id") Long id);

    /**
     * 全件确定：批量更新指定年月状态为 confirmed
     */
    int confirmAllFees(@Param("year") Integer year, @Param("month") Integer month);
}

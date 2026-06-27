package com.dms.server.entity.vo;

import lombok.Data;
import java.util.List;

/**
 * 仪表盘统计数据响应 VO
 *
 * @author dms
 */
@Data
public class DashboardVO {

    /** 总部屋数 */
    private Integer totalRooms;

    /** 入居部屋数 */
    private Integer occupiedRooms;

    /** 空き室数 */
    private Integer vacantRooms;

    /** 整体入居率（%） */
    private Integer occupancyRate;

    /** 各寮入居情况（饼图数据） */
    private List<DormStatVO> dormStats;

    /** 近6个月入退寮件数（柱状图数据） */
    private List<MonthlyDataVO> monthlyData;

    /** 近6个月入居率趋势（折线图数据） */
    private List<RateDataVO> rateData;

    /** 最新10条入退寮记录 */
    private List<OccupancyVO> recentOccupancy;

    /** 长期利用警告（5年以上） */
    private List<LongTermWarningVO> longTermWarnings;

    // ==================== 内嵌 VO ====================

    @Data
    public static class DormStatVO {
        /** 寮名称（含换行符，用于图表） */
        private String name;
        /** 寮完整名称 */
        private String fullName;
        /** 当前入居部屋数 */
        private Integer value;
        /** 总部屋数 */
        private Integer total;
    }

    @Data
    public static class MonthlyDataVO {
        /** 月份标签（如 2026/05） */
        private String month;
        /** 入寮件数 */
        private Integer checkIn;
        /** 退寮件数 */
        private Integer checkOut;
    }

    @Data
    public static class RateDataVO {
        /** 月份标签 */
        private String month;
        /** 入居率（%） */
        private Integer rate;
    }

    @Data
    public static class LongTermWarningVO {
        /** 社員姓名 */
        private String name;
        /** 入居年数 */
        private Integer years;
        /** 所属部署 */
        private String dept;
        /** 寮名称 */
        private String dormName;
        /** 部屋名称 */
        private String roomName;
    }
}

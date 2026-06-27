package com.dms.server.constant;

/**
 * Redis Key 常量，命名规范：项目名:模块名:业务名
 *
 * @author dms
 */
public final class RedisKeyConstants {

    private RedisKeyConstants() {}

    /** 寮列表缓存 */
    public static final String DORM_LIST = "dms:dorm:list";

    /** 仪表盘统计数据缓存 */
    public static final String DASHBOARD_STATS = "dms:dashboard:stats";

    /** 缓存过期时间（秒） */
    public static final long CACHE_TTL_SHORT  = 300L;   // 5分钟
    public static final long CACHE_TTL_MEDIUM = 1800L;  // 30分钟
    public static final long CACHE_TTL_LONG   = 86400L; // 24小时
}

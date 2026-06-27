// 后端 API 调用层（dms-server，统一返回 { code, msg, data, total }）
import request from "@/utils/request.js";

// ==================== 寮API ====================

/** 获取寮列表（支持筛选+分页+计算入居数） */
export const getDormList = (params = {}) => {
  return request.get("/dorm/list", { params });
};

/** 新增寮 */
export const addDorm = (data) => {
  return request.post("/dorm", data);
};

/** 更新寮 */
export const updateDorm = (id, data) => {
  return request.put(`/dorm/${id}`, data);
};

/** 删除寮 */
export const deleteDorm = (id) => {
  return request.delete(`/dorm/${id}`);
};

// ==================== 社員API ====================

/** 获取社员列表（支持筛选+分页） */
export const getEmployeeList = (params = {}) => {
  return request.get("/employee/list", { params });
};

/** 新增社员 */
export const addEmployee = (data) => {
  return request.post("/employee", data);
};

/** 更新社员 */
export const updateEmployee = (id, data) => {
  return request.put(`/employee/${id}`, data);
};

/** 删除社员 */
export const deleteEmployee = (id) => {
  return request.delete(`/employee/${id}`);
};

// ==================== 入退寮API ====================

/** 获取入退寮列表（支持筛选+分页） */
export const getOccupancyList = (params = {}) => {
  return request.get("/occupancy/list", { params });
};

/** 入寮登録 */
export const addOccupancy = (data) => {
  return request.post("/occupancy", data);
};

/** 退寮登録 */
export const checkOut = (id, data) => {
  return request.put(`/occupancy/${id}/checkout`, data);
};

// ==================== 空き室API ====================

/** 获取空き室列表（支持筛选，region为数组） */
export const getVacancyList = (params = {}) => {
  const { region, gender, ...rest } = params;
  const searchParams = new URLSearchParams();

  Object.entries(rest).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== "") {
      searchParams.append(key, value);
    }
  });

  if (gender && gender !== "all") {
    searchParams.append("gender", gender);
  }

  if (Array.isArray(region)) {
    region.forEach((r) => searchParams.append("region", r));
  } else if (region) {
    searchParams.append("region", region);
  }

  return request.get("/vacancy/list", { params: searchParams });
};

// ==================== 寮費API ====================

/** 按年月获取寮費一覧 */
export const getFeeList = (year, month) => {
  return request.get("/fee/list", { params: { year, month } });
};

/** 计算并更新寮費 */
export const calculateFees = (year, month) => {
  return request.post("/fee/calculate", { year, month });
};

/** 単件確定 */
export const confirmFee = (id) => {
  return request.put(`/fee/${id}/confirm`);
};

/** 全件確定 */
export const confirmAllFees = (year, month) => {
  return request.put("/fee/confirm-all", { year, month });
};

// ==================== ダッシュボードAPI ====================

/** 获取仪表盘统计数据 */
export const getDashboardStats = () => {
  return request.get("/dashboard/stats");
};

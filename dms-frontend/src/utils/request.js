import axios from "axios";
import { ElMessage } from "element-plus";

const request = axios.create({
  baseURL: "/api",
  timeout: 10000,
});

// 响应拦截：后端统一返回 { code, msg, data, total }，code !== 200 时提示错误
request.interceptors.response.use(
  (response) => {
    const res = response.data;
    if (res.code !== 200) {
      ElMessage.error(res.msg || "リクエストに失敗しました");
    }
    return res;
  },
  (error) => {
    ElMessage.error(error.message || "ネットワークエラーが発生しました");
    return Promise.reject(error);
  },
);

export default request;

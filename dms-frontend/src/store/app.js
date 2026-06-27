import { defineStore } from "pinia";
import { ref } from "vue";

export const useAppStore = defineStore("app", () => {
  // 侧边栏折叠状态
  const isCollapsed = ref(false);

  // 当前登录用户信息
  const userInfo = ref({
    name: "田中さくら",
    role: "総務担当者",
  });

  // 切换侧边栏
  const toggleSidebar = () => {
    isCollapsed.value = !isCollapsed.value;
  };

  return {
    isCollapsed,
    userInfo,
    toggleSidebar,
  };
});

<template>
  <el-container class="layout-wrapper">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapsed ? '64px' : '220px'" class="sidebar">
      <!-- Logo区域 -->
      <div class="logo-area">
        <div class="logo-icon-wrap">🏠</div>
        <span v-if="!isCollapsed" class="logo-text">社員寮管理</span>
      </div>

      <!-- 导航菜单 -->
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapsed"
        :collapse-transition="false"
        router
        class="sidebar-menu"
      >
        <el-menu-item index="/dashboard" class="menu-item">
          <el-icon class="menu-icon icon-indigo"><DataAnalysis /></el-icon>
          <template #title>ダッシュボード</template>
        </el-menu-item>

        <el-sub-menu index="master">
          <template #title>
            <el-icon class="menu-icon icon-violet"><Setting /></el-icon>
            <span>マスタ管理</span>
          </template>
          <el-menu-item index="/dorm">
            <el-icon class="menu-icon icon-blue"><OfficeBuilding /></el-icon>
            <template #title>寮管理</template>
          </el-menu-item>
          <el-menu-item index="/employee">
            <el-icon class="menu-icon icon-teal"><User /></el-icon>
            <template #title>社員管理</template>
          </el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="business">
          <template #title>
            <el-icon class="menu-icon icon-pink"><Management /></el-icon>
            <span>業務管理</span>
          </template>
          <el-menu-item index="/occupancy">
            <el-icon class="menu-icon icon-amber"><SwitchButton /></el-icon>
            <template #title>入退寮管理</template>
          </el-menu-item>
          <el-menu-item index="/vacancy">
            <el-icon class="menu-icon icon-green"><Key /></el-icon>
            <template #title>空き室管理</template>
          </el-menu-item>
          <el-menu-item index="/fee">
            <el-icon class="menu-icon icon-rose"><Money /></el-icon>
            <template #title>寮費管理</template>
          </el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <!-- 右侧主体 -->
    <el-container class="main-container">
      <!-- 顶部导航栏 -->
      <el-header class="header">
        <div class="header-left">
          <div class="collapse-btn" @click="toggleSidebar">
            <el-icon size="18"><Fold v-if="!isCollapsed" /><Expand v-else /></el-icon>
          </div>
          <el-breadcrumb separator="›" class="breadcrumb">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">ホーム</el-breadcrumb-item>
            <el-breadcrumb-item v-for="item in currentBreadcrumb" :key="item">{{ item }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="header-right">
          <el-dropdown trigger="click">
            <div class="user-info">
              <div class="user-avatar">{{ userInfo.name.charAt(0) }}</div>
              <span class="user-name">{{ userInfo.name }}</span>
              <el-icon size="12" style="color:#a5b4fc"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item disabled>
                  <el-icon><UserFilled /></el-icon>{{ userInfo.role }}
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon>ログアウト
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 主内容区 -->
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import { useAppStore } from '@/store/app.js'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()
const { isCollapsed, userInfo } = storeToRefs(appStore)
const { toggleSidebar } = appStore

const activeMenu = computed(() => route.path)
const currentBreadcrumb = computed(() => route.meta?.breadcrumb || [])

const handleLogout = () => { router.push('/login') }
</script>

<style scoped>
.layout-wrapper { height: 100vh; overflow: hidden; }

/* ── 侧边栏 ── */
.sidebar {
  background: linear-gradient(180deg, #4f46e5 0%, #7c3aed 60%, #9333ea 100%);
  transition: width 0.3s ease;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  box-shadow: 4px 0 20px rgba(79,70,229,0.25);
}

/* Logo */
.logo-area {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  flex-shrink: 0;
  background: rgba(0,0,0,0.12);
  overflow: hidden;
}

.logo-icon-wrap {
  width: 36px; height: 36px;
  border-radius: 10px;
  background: rgba(255,255,255,0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
}

.logo-text {
  color: #fff;
  font-size: 16px;
  font-weight: 700;
  white-space: nowrap;
  letter-spacing: 2px;
}

/* Menu */
.sidebar-menu {
  border-right: none !important;
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  background: transparent !important;
}
.sidebar-menu:not(.el-menu--collapse) { width: 220px; }

/* 覆盖 el-menu 默认色 */
:deep(.el-menu) { background: transparent !important; }
:deep(.el-menu-item),
:deep(.el-sub-menu__title) {
  color: rgba(255,255,255,0.75) !important;
  font-size: 13.5px !important;
  height: 46px !important;
  line-height: 46px !important;
  margin: 2px 8px !important;
  border-radius: 10px !important;
  transition: background 0.2s, color 0.2s !important;
}
:deep(.el-menu-item:hover),
:deep(.el-sub-menu__title:hover) {
  background: rgba(255,255,255,0.12) !important;
  color: #fff !important;
}
:deep(.el-menu-item.is-active) {
  background: rgba(255,255,255,0.22) !important;
  color: #fff !important;
  font-weight: 600 !important;
  box-shadow: 0 2px 12px rgba(0,0,0,0.15);
}
/* sub-menu 子项缩进 */
:deep(.el-sub-menu .el-menu-item) {
  padding-left: 48px !important;
  height: 42px !important;
  line-height: 42px !important;
}
/* sub-menu 展开箭头颜色 */
:deep(.el-sub-menu__icon-arrow) { color: rgba(255,255,255,0.6) !important; }
/* el-menu collapse popup 背景 */
:deep(.el-menu--popup) {
  background: #4f46e5 !important;
  border-radius: 10px !important;
  border: none !important;
}

/* 彩色图标 */
.menu-icon {
  width: 28px !important; height: 28px !important;
  border-radius: 7px !important;
  padding: 5px !important;
  margin-right: 8px !important;
  flex-shrink: 0;
}
.icon-indigo { background: rgba(99,102,241,0.35); color: #a5b4fc !important; }
.icon-violet { background: rgba(139,92,246,0.35); color: #c4b5fd !important; }
.icon-blue   { background: rgba(14,165,233,0.30); color: #7dd3fc !important; }
.icon-teal   { background: rgba(16,185,129,0.30); color: #6ee7b7 !important; }
.icon-pink   { background: rgba(236,72,153,0.30); color: #f9a8d4 !important; }
.icon-amber  { background: rgba(245,158,11,0.30); color: #fcd34d !important; }
.icon-green  { background: rgba(16,185,129,0.30); color: #6ee7b7 !important; }
.icon-rose   { background: rgba(244,63,94,0.30);  color: #fda4af !important; }

/* ── Header ── */
.main-container { flex: 1; overflow: hidden; flex-direction: column; }

.header {
  height: 60px;
  background: #fff;
  border-bottom: 1px solid #ede9fe;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 2px 12px rgba(99,102,241,0.07);
  flex-shrink: 0;
}

.header-left { display: flex; align-items: center; gap: 14px; }

.collapse-btn {
  width: 34px; height: 34px;
  border-radius: 8px;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer;
  color: #6366f1;
  background: #f5f3ff;
  transition: background 0.2s;
}
.collapse-btn:hover { background: #ede9fe; }

.breadcrumb :deep(.el-breadcrumb__item .el-breadcrumb__inner) {
  color: #8b5cf6 !important;
  font-size: 13px;
}
.breadcrumb :deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) {
  color: #4f46e5 !important;
  font-weight: 600;
}
.breadcrumb :deep(.el-breadcrumb__separator) { color: #c4b5fd !important; }

/* 右侧用户 */
.header-right { display: flex; align-items: center; }

.user-info {
  display: flex; align-items: center; gap: 8px;
  cursor: pointer;
  padding: 5px 12px;
  border-radius: 20px;
  background: #f5f3ff;
  border: 1px solid #ede9fe;
  transition: background 0.2s, border-color 0.2s;
}
.user-info:hover { background: #ede9fe; border-color: #c4b5fd; }

.user-avatar {
  width: 28px; height: 28px;
  border-radius: 50%;
  background: linear-gradient(135deg, #6366f1, #a78bfa);
  color: #fff;
  font-size: 13px;
  font-weight: 700;
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 2px 8px rgba(99,102,241,0.35);
}

.user-name { font-size: 13px; color: #4f46e5; font-weight: 500; }

/* ── 主内容区 ── */
.main-content {
  background: #f5f3ff;
  padding: 20px;
  overflow-y: auto;
  flex: 1;
}
</style>

import { createRouter, createWebHistory } from "vue-router";

const routes = [
  {
    path: "/login",
    name: "Login",
    component: () => import("@/views/login/index.vue"),
    meta: { title: "ログイン", breadcrumb: [] },
  },
  {
    path: "/",
    name: "Layout",
    component: () => import("@/layout/index.vue"),
    redirect: "/dashboard",
    children: [
      {
        path: "dashboard",
        name: "Dashboard",
        component: () => import("@/views/dashboard/index.vue"),
        meta: { title: "ダッシュボード", breadcrumb: ["ダッシュボード"] },
      },
      {
        path: "dorm",
        name: "Dorm",
        component: () => import("@/views/dorm/index.vue"),
        meta: { title: "寮管理", breadcrumb: ["マスタ管理", "寮管理"] },
      },
      {
        path: "employee",
        name: "Employee",
        component: () => import("@/views/employee/index.vue"),
        meta: { title: "社員管理", breadcrumb: ["マスタ管理", "社員管理"] },
      },
      {
        path: "occupancy",
        name: "Occupancy",
        component: () => import("@/views/occupancy/index.vue"),
        meta: { title: "入退寮管理", breadcrumb: ["業務管理", "入退寮管理"] },
      },
      {
        path: "vacancy",
        name: "Vacancy",
        component: () => import("@/views/vacancy/index.vue"),
        meta: { title: "空き室管理", breadcrumb: ["業務管理", "空き室管理"] },
      },
      {
        path: "fee",
        name: "Fee",
        component: () => import("@/views/fee/index.vue"),
        meta: { title: "寮費管理", breadcrumb: ["業務管理", "寮費管理"] },
      },
    ],
  },
  {
    path: "/:pathMatch(.*)*",
    redirect: "/login",
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// 路由守卫：设置页面标题
router.beforeEach((to, from, next) => {
  if (to.meta?.title) {
    document.title = `${to.meta.title} | 社員寮管理システム`;
  }
  next();
});

export default router;

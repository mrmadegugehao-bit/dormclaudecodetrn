<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20">
      <el-col :span="6" v-for="card in statCards" :key="card.title">
        <div class="stat-card" :style="{ background: card.gradient }">
          <div class="stat-body">
            <div>
              <div class="stat-value">{{ card.value }}</div>
              <div class="stat-label">{{ card.title }}</div>
            </div>
            <div class="stat-emoji">{{ card.emoji }}</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表第一行 -->
    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="14">
        <el-card shadow="hover">
          <template #header><span class="card-title">月別入退寮件数（過去6ヶ月）</span></template>
          <div ref="barRef" style="height:300px" />
        </el-card>
      </el-col>
      <el-col :span="10">
        <el-card shadow="hover">
          <template #header><span class="card-title">寮別入居状況</span></template>
          <div ref="pieRef" style="height:300px" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表第二行 -->
    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="14">
        <el-card shadow="hover">
          <template #header><span class="card-title">月別入居率推移（過去6ヶ月）</span></template>
          <div ref="lineRef" style="height:260px" />
        </el-card>
      </el-col>
      <el-col :span="10">
        <el-card shadow="hover">
          <template #header>
            <div style="display:flex;align-items:center;gap:8px">
              <span class="card-title">長期利用警告</span>
              <el-badge :value="stats.longTermWarnings?.length || 0" type="danger" />
            </div>
          </template>
          <el-table :data="stats.longTermWarnings || []" size="small" :max-height="210">
            <el-table-column prop="name" label="氏名" width="90" />
            <el-table-column prop="dept" label="所属" show-overflow-tooltip />
            <el-table-column label="利用年数" width="76" align="center">
              <template #default="{ row }">
                <el-tag type="danger" size="small">{{ row.years }}年</el-tag>
              </template>
            </el-table-column>
          </el-table>
          <div style="margin-top:8px">
            <el-button type="primary" link size="small" @click="$router.push('/occupancy')">全件表示 →</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 直近入退寮 -->
    <el-row style="margin-top:20px">
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header>
            <div style="display:flex;align-items:center;justify-content:space-between">
              <span class="card-title">直近の入退寮</span>
              <el-button type="primary" link @click="$router.push('/occupancy')">全履歴を見る →</el-button>
            </div>
          </template>
          <el-table :data="stats.recentOccupancy || []" size="small">
            <el-table-column type="index" label="No." width="55" align="center" />
            <el-table-column prop="empName" label="社員名" width="110" />
            <el-table-column prop="dormName" label="寮名" show-overflow-tooltip />
            <el-table-column prop="roomName" label="部屋名" width="130" />
            <el-table-column prop="checkInDate" label="入寮日" width="115" />
            <el-table-column label="退寮日" width="115">
              <template #default="{ row }">{{ row.checkOutDate || '—' }}</template>
            </el-table-column>
            <el-table-column label="ステータス" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="row.status === 'active' ? 'success' : 'info'" size="small">
                  {{ row.status === 'active' ? '入居中' : '退寮済' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getDashboardStats } from '@/api/index.js'

const barRef = ref(null)
const pieRef = ref(null)
const lineRef = ref(null)

const stats = ref({
  totalRooms: 0, occupiedRooms: 0, vacantRooms: 0, occupancyRate: 0,
  dormStats: [], monthlyData: [], rateData: [],
  recentOccupancy: [], longTermWarnings: []
})

// 统计卡片配置
const statCards = computed(() => [
  { title: '総部屋数', value: stats.value.totalRooms, gradient: 'linear-gradient(135deg,#6366f1,#818cf8)', icon: 'House', emoji: '🏢' },
  { title: '入居中', value: stats.value.occupiedRooms, gradient: 'linear-gradient(135deg,#10b981,#34d399)', icon: 'User', emoji: '👤' },
  { title: '空き室', value: stats.value.vacantRooms, gradient: 'linear-gradient(135deg,#f59e0b,#fbbf24)', icon: 'Key', emoji: '🔑' },
  { title: '入居率', value: stats.value.occupancyRate + '%', gradient: 'linear-gradient(135deg,#ec4899,#f472b6)', icon: 'DataAnalysis', emoji: '📊' },
])

// 初始化月別入退寮棒グラフ
const initBar = (data) => {
  const chart = echarts.init(barRef.value)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['入寮', '退寮'], bottom: 0 },
    grid: { left: '3%', right: '4%', bottom: '14%', containLabel: true },
    xAxis: { type: 'category', data: data.map(d => d.month) },
    yAxis: { type: 'value', name: '件数', minInterval: 1 },
    series: [
      {
        name: '入寮', type: 'bar', barMaxWidth: 32,
        data: data.map(d => d.checkIn),
        itemStyle: { color: '#409EFF', borderRadius: [4, 4, 0, 0] }
      },
      {
        name: '退寮', type: 'bar', barMaxWidth: 32,
        data: data.map(d => d.checkOut),
        itemStyle: { color: '#F56C6C', borderRadius: [4, 4, 0, 0] }
      }
    ]
  })
}

// 初始化寮別入居率円グラフ
const initPie = (data) => {
  const chart = echarts.init(pieRef.value)
  const colors = ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C']
  chart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c}室 ({d}%)' },
    legend: { orient: 'vertical', right: '4%', top: 'center', textStyle: { fontSize: 11 } },
    series: [{
      type: 'pie', radius: ['38%', '65%'], center: ['38%', '50%'],
      label: { show: false },
      data: data.map((d, i) => ({
        name: d.fullName, value: d.value,
        itemStyle: { color: colors[i % colors.length] }
      }))
    }]
  })
}

// 初始化月別入居率折れ線グラフ
const initLine = (data) => {
  const chart = echarts.init(lineRef.value)
  chart.setOption({
    tooltip: {
      trigger: 'axis',
      formatter: params => `${params[0].name}<br/>入居率: ${params[0].value}%`
    },
    grid: { left: '3%', right: '4%', bottom: '14%', containLabel: true },
    xAxis: { type: 'category', data: data.map(d => d.month) },
    yAxis: {
      type: 'value', name: '入居率(%)', min: 0, max: 100,
      axisLabel: { formatter: '{value}%' }
    },
    series: [{
      name: '入居率', type: 'line', smooth: true,
      data: data.map(d => d.rate),
      lineStyle: { color: '#409EFF', width: 2 },
      areaStyle: {
        color: {
          type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(64,158,255,0.28)' },
            { offset: 1, color: 'rgba(64,158,255,0)' }
          ]
        }
      },
      symbol: 'circle', symbolSize: 8,
      itemStyle: { color: '#409EFF' }
    }]
  })
}

onMounted(async () => {
  const res = await getDashboardStats()
  stats.value = res.data
  await nextTick()
  initBar(res.data.monthlyData)
  initPie(res.data.dormStats)
  initLine(res.data.rateData)
})
</script>

<style scoped>
.stat-card {
  border-radius: 16px;
  padding: 22px 24px;
  color: #fff;
  cursor: default;
  transition: transform 0.25s, box-shadow 0.25s;
  box-shadow: 0 6px 20px rgba(0,0,0,0.12);
}
.stat-card:hover { transform: translateY(-4px); box-shadow: 0 12px 32px rgba(0,0,0,0.18); }
.stat-body { display: flex; align-items: center; justify-content: space-between; }
.stat-value { font-size: 38px; font-weight: 800; line-height: 1; letter-spacing: -1px; }
.stat-label { font-size: 13px; color: rgba(255,255,255,0.8); margin-top: 8px; font-weight: 500; }
.stat-emoji { font-size: 42px; opacity: 0.8; }
.card-title { font-size: 15px; font-weight: 600; color: #303133; }
</style>

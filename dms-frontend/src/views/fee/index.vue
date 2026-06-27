<template>
  <div>
    <!-- 操作バー -->
    <el-card shadow="never">
      <div style="display:flex;align-items:center;gap:16px;flex-wrap:wrap">
        <div style="display:flex;align-items:center;gap:8px">
          <span style="font-size:14px;color:#606266">対象年月：</span>
          <el-date-picker
            v-model="targetMonth"
            type="month"
            placeholder="年月を選択"
            value-format="YYYY-M"
            style="width:150px"
            @change="onMonthChange"
          />
        </div>
        <el-button type="primary" :icon="Memo" :loading="calculating" @click="onCalculate">
          一括計算
        </el-button>
        <el-button type="success" :icon="Check" :loading="confirming" @click="onConfirmAll"
          :disabled="!feeList.length || allConfirmed">
          全件確定
        </el-button>
        <el-button :icon="Download" @click="onExport" :disabled="!feeList.length">
          CSVエクスポート
        </el-button>
        <el-tag v-if="allConfirmed && feeList.length" type="success" style="margin-left:auto">
          全件確定済
        </el-tag>
        <el-tag v-else-if="feeList.length" type="warning" style="margin-left:auto">
          未確定 {{ pendingCount }} 件
        </el-tag>
      </div>
    </el-card>

    <!-- データテーブル -->
    <el-card shadow="never" style="margin-top:16px">
      <template #header>
        <div style="display:flex;align-items:center;justify-content:space-between">
          <span style="font-size:15px;font-weight:600;color:#303133">
            寮費一覧 — {{ displayMonth }}
          </span>
          <div style="font-size:13px;color:#606266" v-if="feeList.length">
            合計：<strong style="font-size:16px;color:#303133">¥{{ totalAmount.toLocaleString() }}</strong>
          </div>
        </div>
      </template>

      <el-table v-loading="loading" :data="feeList" stripe border show-summary :summary-method="summaryMethod">
        <el-table-column type="index" label="No." width="65" align="center" />
        <el-table-column prop="empName" label="社員名" width="110" />
        <el-table-column prop="empDept" label="所属" width="120" show-overflow-tooltip />
        <el-table-column prop="dormName" label="寮名" min-width="140" show-overflow-tooltip />
        <el-table-column prop="roomName" label="部屋名" width="120" />
        <el-table-column prop="area" label="面積(㎡)" width="90" align="right" />
        <el-table-column label="入居日数" width="90" align="right">
          <template #default="{ row }">{{ row.days }} / {{ row.totalDays }}日</template>
        </el-table-column>
        <el-table-column label="単価(円/㎡)" width="105" align="right">
          <template #default="{ row }">{{ row.unitPrice.toLocaleString() }}</template>
        </el-table-column>
        <el-table-column label="請求金額(円)" width="120" align="right">
          <template #default="{ row }">
            <strong style="color:#303133">{{ row.amount.toLocaleString() }}</strong>
          </template>
        </el-table-column>
        <el-table-column label="ステータス" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 'confirmed' ? 'success' : 'warning'" size="small">
              {{ row.status === 'confirmed' ? '確定済' : '未確定' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" align="center" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 'pending'" type="success" link size="small" @click="onConfirmOne(row)">
              確定する
            </el-button>
            <span v-else style="color:#909399;font-size:12px">—</span>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!loading && !feeList.length" description="データがありません。「一括計算」ボタンで計算してください。" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Memo, Check, Download } from '@element-plus/icons-vue'
import { getFeeList, calculateFees, confirmFee, confirmAllFees } from '@/api/index.js'

const loading = ref(false)
const calculating = ref(false)
const confirming = ref(false)
const feeList = ref([])

// デフォルトは今月（2026-05）
const now = new Date()
const targetMonth = ref(`${now.getFullYear()}-${now.getMonth() + 1}`)

const parseYM = (ym) => {
  const [y, m] = ym.split('-').map(Number)
  return { year: y, month: m }
}

const displayMonth = computed(() => {
  const { year, month } = parseYM(targetMonth.value)
  return `${year}年${month}月`
})

const totalAmount = computed(() => feeList.value.reduce((s, f) => s + f.amount, 0))
const pendingCount = computed(() => feeList.value.filter(f => f.status === 'pending').length)
const allConfirmed = computed(() => feeList.value.length > 0 && pendingCount.value === 0)

const summaryMethod = ({ columns, data }) => {
  return columns.map((col, i) => {
    if (i === 0) return '合計'
    if (col.property === 'amount') {
      const sum = data.reduce((s, r) => s + r.amount, 0)
      return `¥${sum.toLocaleString()}`
    }
    return ''
  })
}

const loadData = async () => {
  loading.value = true
  try {
    const { year, month } = parseYM(targetMonth.value)
    const res = await getFeeList(year, month)
    feeList.value = res.data
  } finally {
    loading.value = false
  }
}

const onMonthChange = () => { loadData() }

const onCalculate = async () => {
  const { year, month } = parseYM(targetMonth.value)
  await ElMessageBox.confirm(
    `${year}年${month}月の寮費を計算します。既存データは上書きされます。`,
    '一括計算確認', {
      type: 'warning', confirmButtonText: '計算する', cancelButtonText: 'キャンセル'
    }
  ).catch(() => { throw new Error('cancel') })

  calculating.value = true
  try {
    const res = await calculateFees(year, month)
    feeList.value = res.data
    ElMessage.success(`${res.data.length} 件計算しました`)
  } finally {
    calculating.value = false
  }
}

const onConfirmOne = async (row) => {
  await confirmFee(row.id)
  row.status = 'confirmed'
  ElMessage.success('確定しました')
}

const onConfirmAll = async () => {
  await ElMessageBox.confirm(
    `${pendingCount.value} 件の未確定データを全て確定します。`,
    '全件確定確認', {
      type: 'warning', confirmButtonText: '全件確定', cancelButtonText: 'キャンセル'
    }
  ).catch(() => { throw new Error('cancel') })

  confirming.value = true
  try {
    const { year, month } = parseYM(targetMonth.value)
    await confirmAllFees(year, month)
    feeList.value.forEach(f => { f.status = 'confirmed' })
    ElMessage.success('全件確定しました')
  } finally {
    confirming.value = false
  }
}

const onExport = () => {
  const { year, month } = parseYM(targetMonth.value)
  const headers = ['No.', '社員名', '所属', '寮名', '部屋名', '面積(㎡)', '入居日数', '月暦日数', '単価', '請求金額', 'ステータス']
  const rows = feeList.value.map((f, i) => [
    i + 1, f.empName, f.empDept, f.dormName, f.roomName,
    f.area, f.days, f.totalDays, f.unitPrice, f.amount,
    f.status === 'confirmed' ? '確定済' : '未確定'
  ])
  const csv = [headers, ...rows].map(r => r.join(',')).join('\n')
  const bom = '﻿'
  const blob = new Blob([bom + csv], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `寮費_${year}年${month}月.csv`
  a.click()
  URL.revokeObjectURL(url)
  ElMessage.success('CSVエクスポートしました')
}

onMounted(loadData)
</script>

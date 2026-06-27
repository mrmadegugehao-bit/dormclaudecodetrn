<template>
  <div>
    <!-- 検索エリア -->
    <el-card shadow="never">
      <el-form :model="search" inline>
        <el-form-item label="性別区分">
          <el-radio-group v-model="search.gender">
            <el-radio-button value="all">全て</el-radio-button>
            <el-radio-button value="male">男性</el-radio-button>
            <el-radio-button value="female">女性</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="地域">
          <el-checkbox-group v-model="search.region">
            <el-checkbox-button label="東京" value="tokyo" />
            <el-checkbox-button label="大阪" value="osaka" />
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="稼動状況">
          <el-select v-model="search.status" placeholder="全て" clearable style="width:120px">
            <el-option label="空き室" value="vacant" />
            <el-option label="入居中" value="occupied" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="onSearch">検索</el-button>
          <el-button :icon="Refresh" @click="onReset">リセット</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 統計バナー -->
    <el-row :gutter="16" style="margin-top:16px">
      <el-col :span="6">
        <el-card shadow="never" style="text-align:center;padding:8px 0">
          <div style="font-size:28px;font-weight:bold;color:#409EFF">{{ stats.total }}</div>
          <div style="font-size:13px;color:#909399;margin-top:4px">総部屋数</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never" style="text-align:center;padding:8px 0">
          <div style="font-size:28px;font-weight:bold;color:#67C23A">{{ stats.vacant }}</div>
          <div style="font-size:13px;color:#909399;margin-top:4px">空き室</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never" style="text-align:center;padding:8px 0">
          <div style="font-size:28px;font-weight:bold;color:#F56C6C">{{ stats.occupied }}</div>
          <div style="font-size:13px;color:#909399;margin-top:4px">入居中</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never" style="text-align:center;padding:8px 0">
          <div style="font-size:28px;font-weight:bold;color:#E6A23C">{{ stats.rate }}%</div>
          <div style="font-size:13px;color:#909399;margin-top:4px">入居率</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- データテーブル -->
    <el-card shadow="never" style="margin-top:16px">
      <template #header>
        <span style="font-size:15px;font-weight:600;color:#303133">部屋一覧</span>
      </template>

      <el-table v-loading="loading" :data="tableData" stripe border>
        <el-table-column type="index" label="No." width="65" align="center" />
        <el-table-column prop="dormName" label="寮名" min-width="150" show-overflow-tooltip />
        <el-table-column prop="name" label="部屋名" width="130" />
        <el-table-column label="地域" width="80" align="center">
          <template #default="{ row }">{{ row.dormRegion === 'tokyo' ? '東京' : '大阪' }}</template>
        </el-table-column>
        <el-table-column label="性別" width="80" align="center">
          <template #default="{ row }">
            <span v-if="row.gender === 'male'" style="color:#409EFF">男性</span>
            <span v-else-if="row.gender === 'female'" style="color:#F56C6C">女性</span>
            <span v-else style="color:#909399">—</span>
          </template>
        </el-table-column>
        <el-table-column label="間取り" width="90" align="center">
          <template #default="{ row }">{{ row.dormLayout }}</template>
        </el-table-column>
        <el-table-column prop="area" label="面積(㎡)" width="100" align="center" />
        <el-table-column label="稼動状況" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 'vacant' ? 'success' : 'danger'" size="small">
              {{ row.status === 'vacant' ? '空き室' : '入居中' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 'vacant'" type="primary" size="small" @click="onQuickCheckIn(row)">
              入寮登録
            </el-button>
            <span v-else style="color:#909399;font-size:12px">入居中</span>
          </template>
        </el-table-column>
      </el-table>

      <div style="display:flex;justify-content:flex-end;margin-top:16px">
        <el-pagination
          v-model:current-page="page.current"
          v-model:page-size="page.size"
          :total="page.total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          background
          @change="loadData"
        />
      </div>
    </el-card>

    <!-- クイック入寮登録ダイアログ -->
    <el-dialog v-model="dlgVisible" title="入寮登録" width="480px" :close-on-click-modal="false" @close="onDlgClose">
      <el-descriptions :column="2" border size="small" style="margin-bottom:20px">
        <el-descriptions-item label="寮名">{{ targetRoom.dormName }}</el-descriptions-item>
        <el-descriptions-item label="部屋名">{{ targetRoom.name }}</el-descriptions-item>
        <el-descriptions-item label="面積">{{ targetRoom.area }}㎡</el-descriptions-item>
        <el-descriptions-item label="性別">
          {{ targetRoom.gender === 'male' ? '男性' : targetRoom.gender === 'female' ? '女性' : '—' }}
        </el-descriptions-item>
      </el-descriptions>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="社員" prop="empId">
          <el-select v-model="form.empId" placeholder="社員を選択してください" filterable style="width:100%">
            <el-option v-for="e in allEmployees" :key="e.id" :label="`${e.empId} ${e.name}`" :value="e.id">
              <span>{{ e.empId }} {{ e.name }}</span>
              <span style="float:right;color:#909399;font-size:12px;margin-left:16px">
                {{ e.type === 'jp' ? '日本社員' : '中国出張' }} / {{ e.gender === 'male' ? '男性' : '女性' }}
              </span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="入寮日" prop="checkInDate">
          <el-date-picker v-model="form.checkInDate" type="date" placeholder="入寮日を選択"
            value-format="YYYY-MM-DD" style="width:100%" />
        </el-form-item>
        <el-alert v-if="genderMismatch" type="warning" :closable="false"
          title="選択した社員の性別と部屋区分が一致していません。" />
      </el-form>
      <template #footer>
        <el-button @click="dlgVisible = false">キャンセル</el-button>
        <el-button type="primary" :loading="saving" @click="onSave">登録する</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import { getVacancyList, addOccupancy, getEmployeeList } from '@/api/index.js'

const loading = ref(false)
const saving = ref(false)
const dlgVisible = ref(false)
const tableData = ref([])
const allEmployees = ref([])
const formRef = ref(null)
const targetRoom = reactive({ id: null, dormName: '', name: '', area: 0, gender: '' })

const search = reactive({ gender: 'all', region: [], status: '' })
const page = reactive({ current: 1, size: 20, total: 0 })
const form = reactive({ empId: null, checkInDate: '', roomId: null })

const stats = computed(() => {
  const total = page.total
  const occupied = tableData.value.filter(r => r.status === 'occupied').length
  const vacant = tableData.value.filter(r => r.status === 'vacant').length
  const rate = total ? Math.round((occupied / total) * 100) : 0
  return { total, occupied, vacant, rate }
})

const genderMismatch = computed(() => {
  if (!form.empId || !targetRoom.gender || targetRoom.gender === 'both') return false
  const emp = allEmployees.value.find(e => e.id === form.empId)
  return emp && emp.gender !== targetRoom.gender
})

const rules = {
  empId: [{ required: true, message: '社員を選択してください', trigger: 'change' }],
  checkInDate: [{ required: true, message: '入寮日は必須です', trigger: 'change' }],
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      gender: search.gender,
      region: search.region,
      status: search.status,
      page: page.current,
      pageSize: page.size
    }
    const res = await getVacancyList(params)
    tableData.value = res.data
    page.total = res.total
  } finally {
    loading.value = false
  }
}

const onSearch = () => { page.current = 1; loadData() }
const onReset = () => { Object.assign(search, { gender: 'all', region: [], status: '' }); onSearch() }

const loadAllEmployees = async () => {
  if (allEmployees.value.length) return
  const [jp, cn] = await Promise.all([
    getEmployeeList({ type: 'jp', pageSize: 100 }),
    getEmployeeList({ type: 'cn', pageSize: 100 }),
  ])
  allEmployees.value = [...jp.data, ...cn.data]
}

const onQuickCheckIn = async (row) => {
  await loadAllEmployees()
  Object.assign(targetRoom, { id: row.id, dormName: row.dormName, name: row.name, area: row.area, gender: row.gender })
  Object.assign(form, { empId: null, checkInDate: '', roomId: row.id })
  dlgVisible.value = true
}

const onDlgClose = () => { formRef.value?.resetFields() }

const onSave = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    await addOccupancy({ empId: form.empId, roomId: form.roomId, checkInDate: form.checkInDate })
    ElMessage.success('入寮登録しました')
    dlgVisible.value = false
    loadData()
  } finally {
    saving.value = false
  }
}

onMounted(loadData)
</script>

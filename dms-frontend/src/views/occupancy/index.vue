<template>
  <div>
    <!-- 検索エリア -->
    <el-card shadow="never">
      <el-form :model="search" inline>
        <el-form-item label="社員名">
          <el-input v-model="search.empName" placeholder="社員名で検索" clearable style="width:150px" />
        </el-form-item>
        <el-form-item label="寮名">
          <el-input v-model="search.dormName" placeholder="寮名で検索" clearable style="width:150px" />
        </el-form-item>
        <el-form-item label="ステータス">
          <el-select v-model="search.status" placeholder="全て" clearable style="width:120px">
            <el-option label="入居中" value="active" />
            <el-option label="退寮済" value="checked_out" />
          </el-select>
        </el-form-item>
        <el-form-item label="入寮日">
          <el-date-picker v-model="search.checkInStart" type="date" placeholder="開始日"
            value-format="YYYY-MM-DD" style="width:140px" />
          <span style="margin:0 6px;color:#606266">〜</span>
          <el-date-picker v-model="search.checkInEnd" type="date" placeholder="終了日"
            value-format="YYYY-MM-DD" style="width:140px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="onSearch">検索</el-button>
          <el-button :icon="Refresh" @click="onReset">リセット</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- データテーブル -->
    <el-card shadow="never" style="margin-top:16px">
      <template #header>
        <div style="display:flex;align-items:center;justify-content:space-between">
          <span style="font-size:15px;font-weight:600;color:#303133">入退寮管理</span>
          <el-button type="primary" :icon="Plus" @click="onCheckIn">入寮登録</el-button>
        </div>
      </template>

      <el-table v-loading="loading" :data="tableData" stripe border>
        <el-table-column type="index" label="No." width="65" align="center" />
        <el-table-column prop="empName" label="社員名" width="110" />
        <el-table-column label="社員区分" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="row.empType === 'jp' ? '' : 'warning'" size="small">
              {{ row.empType === 'jp' ? '日本社員' : '中国出張' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="dormName" label="寮名" min-width="150" show-overflow-tooltip />
        <el-table-column prop="roomName" label="部屋名" width="130" />
        <el-table-column prop="checkInDate" label="入寮日" width="115" align="center" />
        <el-table-column label="退寮日" width="115" align="center">
          <template #default="{ row }">{{ row.checkOutDate || '—' }}</template>
        </el-table-column>
        <el-table-column label="退寮理由" min-width="130" show-overflow-tooltip>
          <template #default="{ row }">{{ row.reason || '—' }}</template>
        </el-table-column>
        <el-table-column label="ステータス" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 'active' ? 'success' : 'info'" size="small">
              {{ row.status === 'active' ? '入居中' : '退寮済' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" align="center" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 'active'" type="warning" link size="small" @click="onCheckOut(row)">
              退寮登録
            </el-button>
            <span v-else style="color:#909399;font-size:12px">—</span>
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

    <!-- 入寮登録ダイアログ -->
    <el-dialog v-model="checkInDlg" title="入寮登録" width="500px" :close-on-click-modal="false" @close="onCheckInClose">
      <el-form ref="checkInFormRef" :model="checkInForm" :rules="checkInRules" label-width="100px">
        <el-form-item label="社員" prop="empId">
          <el-select v-model="checkInForm.empId" placeholder="社員を選択してください" filterable style="width:100%"
            @change="onEmpChange">
            <el-option v-for="e in allEmployees" :key="e.id" :label="`${e.empId} ${e.name}`" :value="e.id">
              <span>{{ e.empId }} {{ e.name }}</span>
              <span style="float:right;color:#909399;font-size:12px;margin-left:16px">
                {{ e.type === 'jp' ? '日本社員' : '中国出張' }}
              </span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="部屋" prop="roomId">
          <el-select v-model="checkInForm.roomId" placeholder="部屋を選択してください" filterable style="width:100%">
            <el-option v-for="r in vacantRooms" :key="r.id" :label="`${r.dormName} ${r.name}`" :value="r.id">
              <span>{{ r.dormName }} - {{ r.name }}</span>
              <span style="float:right;color:#909399;font-size:12px;margin-left:16px">
                {{ r.area }}㎡ / {{ r.gender === 'male' ? '男性' : r.gender === 'female' ? '女性' : '—' }}
              </span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="入寮日" prop="checkInDate">
          <el-date-picker v-model="checkInForm.checkInDate" type="date" placeholder="入寮日を選択"
            value-format="YYYY-MM-DD" style="width:100%" />
        </el-form-item>
        <el-alert v-if="genderWarning" type="warning" :closable="false" style="margin-bottom:12px"
          title="性別と部屋の区分が一致しない可能性があります。確認してください。" />
      </el-form>
      <template #footer>
        <el-button @click="checkInDlg = false">キャンセル</el-button>
        <el-button type="primary" :loading="saving" @click="onCheckInSave">登録する</el-button>
      </template>
    </el-dialog>

    <!-- 退寮登録ダイアログ -->
    <el-dialog v-model="checkOutDlg" title="退寮登録" width="460px" :close-on-click-modal="false">
      <el-descriptions :column="2" border size="small" style="margin-bottom:20px">
        <el-descriptions-item label="社員名">{{ checkOutTarget.empName }}</el-descriptions-item>
        <el-descriptions-item label="寮名">{{ checkOutTarget.dormName }}</el-descriptions-item>
        <el-descriptions-item label="部屋名">{{ checkOutTarget.roomName }}</el-descriptions-item>
        <el-descriptions-item label="入寮日">{{ checkOutTarget.checkInDate }}</el-descriptions-item>
      </el-descriptions>
      <el-form ref="checkOutFormRef" :model="checkOutForm" :rules="checkOutRules" label-width="100px">
        <el-form-item label="退寮日" prop="checkOutDate">
          <el-date-picker v-model="checkOutForm.checkOutDate" type="date" placeholder="退寮日を選択"
            value-format="YYYY-MM-DD" style="width:100%" />
        </el-form-item>
        <el-form-item label="退寮理由" prop="reason">
          <el-select v-model="checkOutForm.reason" placeholder="退寮理由を選択" style="width:100%">
            <el-option label="自己都合退寮" value="自己都合退寮" />
            <el-option label="転勤・異動" value="転勤・異動" />
            <el-option label="退職" value="退職" />
            <el-option label="帰国" value="帰国" />
            <el-option label="その他" value="その他" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="checkOutDlg = false">キャンセル</el-button>
        <el-button type="warning" :loading="saving" @click="onCheckOutSave">退寮を確定する</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import { getOccupancyList, addOccupancy, checkOut, getVacancyList, getEmployeeList } from '@/api/index.js'

const loading = ref(false)
const saving = ref(false)
const checkInDlg = ref(false)
const checkOutDlg = ref(false)
const tableData = ref([])
const checkInFormRef = ref(null)
const checkOutFormRef = ref(null)
const allEmployees = ref([])
const vacantRooms = ref([])

const search = reactive({ empName: '', dormName: '', status: '', checkInStart: '', checkInEnd: '' })
const page = reactive({ current: 1, size: 10, total: 0 })
const checkInForm = reactive({ empId: null, roomId: null, checkInDate: '' })
const checkOutForm = reactive({ checkOutDate: '', reason: '' })
const checkOutTarget = reactive({ id: null, empName: '', dormName: '', roomName: '', checkInDate: '' })

const checkInRules = {
  empId: [{ required: true, message: '社員を選択してください', trigger: 'change' }],
  roomId: [{ required: true, message: '部屋を選択してください', trigger: 'change' }],
  checkInDate: [{ required: true, message: '入寮日は必須です', trigger: 'change' }],
}
const checkOutRules = {
  checkOutDate: [{ required: true, message: '退寮日は必須です', trigger: 'change' }],
  reason: [{ required: true, message: '退寮理由を選択してください', trigger: 'change' }],
}

const genderWarning = computed(() => {
  if (!checkInForm.empId || !checkInForm.roomId) return false
  const emp = allEmployees.value.find(e => e.id === checkInForm.empId)
  const room = vacantRooms.value.find(r => r.id === checkInForm.roomId)
  if (!emp || !room || !room.gender || room.gender === 'both') return false
  return emp.gender !== room.gender
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await getOccupancyList({ ...search, page: page.current, pageSize: page.size })
    tableData.value = res.data
    page.total = res.total
  } finally {
    loading.value = false
  }
}

const loadVacantRooms = async () => {
  const res = await getVacancyList({ status: 'vacant', pageSize: 100 })
  vacantRooms.value = res.data
}

const loadAllEmployees = async () => {
  const [jp, cn] = await Promise.all([
    getEmployeeList({ type: 'jp', pageSize: 100 }),
    getEmployeeList({ type: 'cn', pageSize: 100 }),
  ])
  allEmployees.value = [...jp.data, ...cn.data]
}

const onSearch = () => { page.current = 1; loadData() }
const onReset = () => {
  Object.assign(search, { empName: '', dormName: '', status: '', checkInStart: '', checkInEnd: '' })
  onSearch()
}
const onEmpChange = () => {}

const onCheckIn = async () => {
  await Promise.all([loadVacantRooms(), loadAllEmployees()])
  Object.assign(checkInForm, { empId: null, roomId: null, checkInDate: '' })
  checkInDlg.value = true
}

const onCheckInClose = () => { checkInFormRef.value?.resetFields() }

const onCheckInSave = async () => {
  const valid = await checkInFormRef.value?.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    await addOccupancy({ ...checkInForm })
    ElMessage.success('入寮登録しました')
    checkInDlg.value = false
    loadData()
  } finally {
    saving.value = false
  }
}

const onCheckOut = (row) => {
  Object.assign(checkOutTarget, {
    id: row.id, empName: row.empName, dormName: row.dormName,
    roomName: row.roomName, checkInDate: row.checkInDate
  })
  Object.assign(checkOutForm, { checkOutDate: '', reason: '' })
  checkOutDlg.value = true
}

const onCheckOutSave = async () => {
  const valid = await checkOutFormRef.value?.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    await checkOut(checkOutTarget.id, { ...checkOutForm })
    ElMessage.success('退寮登録しました')
    checkOutDlg.value = false
    loadData()
  } finally {
    saving.value = false
  }
}

onMounted(loadData)
</script>

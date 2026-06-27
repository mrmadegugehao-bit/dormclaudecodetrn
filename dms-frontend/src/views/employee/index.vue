<template>
  <div>
    <el-tabs v-model="activeTab" @tab-change="onTabChange">
      <el-tab-pane label="日本社員" name="jp" />
      <el-tab-pane label="中国出張社員" name="cn" />
    </el-tabs>

    <!-- 検索エリア -->
    <el-card shadow="never" style="margin-top:0">
      <el-form :model="search" inline>
        <el-form-item label="氏名">
          <el-input v-model="search.name" placeholder="氏名で検索" clearable style="width:160px" />
        </el-form-item>
        <el-form-item label="社員ID">
          <el-input v-model="search.empId" placeholder="社員IDで検索" clearable style="width:140px" />
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
          <span style="font-size:15px;font-weight:600;color:#303133">
            {{ activeTab === 'jp' ? '日本社員一覧' : '中国出張社員一覧' }}
          </span>
          <el-button type="primary" :icon="Plus" @click="onAdd">新規登録</el-button>
        </div>
      </template>

      <!-- 日本社員テーブル -->
      <el-table v-if="activeTab === 'jp'" v-loading="loading" :data="tableData" stripe border @selection-change="onSelect">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column type="index" label="No." width="65" align="center" />
        <el-table-column prop="empId" label="社員ID" width="100" align="center" />
        <el-table-column prop="name" label="氏名" width="110" />
        <el-table-column prop="dept" label="部署" width="120" />
        <el-table-column prop="division" label="課" min-width="120" show-overflow-tooltip />
        <el-table-column label="性別" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.gender === 'male' ? '' : 'danger'" size="small">
              {{ row.gender === 'male' ? '男性' : '女性' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="雇用形態" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.empType === 'regular' ? 'success' : 'warning'" size="small">
              {{ row.empType === 'regular' ? '正社員' : '契約社員' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="mobile" label="携帯電話" width="130" />
        <el-table-column prop="email" label="メール" min-width="180" show-overflow-tooltip />
        <el-table-column label="操作" width="130" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="onEdit(row)">編集</el-button>
            <el-button type="danger" link size="small" @click="onDelete(row)">削除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 中国出張社員テーブル -->
      <el-table v-if="activeTab === 'cn'" v-loading="loading" :data="tableData" stripe border @selection-change="onSelect">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column type="index" label="No." width="65" align="center" />
        <el-table-column prop="empId" label="社員ID" width="100" align="center" />
        <el-table-column prop="name" label="氏名" width="110" />
        <el-table-column label="出身拠点" width="100" align="center">
          <template #default="{ row }">
            <el-tag size="small">{{ row.origin === 'shenyang' ? '瀋陽' : '大連' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="性別" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.gender === 'male' ? '' : 'danger'" size="small">
              {{ row.gender === 'male' ? '男性' : '女性' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="出張区分" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="row.bizType === 'longterm' ? 'warning' : 'info'" size="small">
              {{ row.bizType === 'longterm' ? '長期出張' : '短期出張' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="arrivalDate" label="来日日" width="115" />
        <el-table-column label="帰国予定日" width="115">
          <template #default="{ row }">{{ row.departureDate || '—' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="130" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="onEdit(row)">編集</el-button>
            <el-button type="danger" link size="small" @click="onDelete(row)">削除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div style="display:flex;align-items:center;justify-content:space-between;margin-top:16px">
        <el-button type="danger" plain :disabled="!selected.length" @click="onBatchDelete">
          一括削除（{{ selected.length }}）
        </el-button>
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

    <!-- 日本社員 ダイアログ -->
    <el-dialog v-if="activeTab === 'jp'" v-model="dlgVisible" :title="isEdit ? '日本社員 編集' : '日本社員 新規登録'" width="560px"
      :close-on-click-modal="false" @close="onDlgClose">
      <el-form ref="formRef" :model="form" :rules="jpRules" label-width="100px">
        <el-form-item label="社員ID" prop="empId">
          <el-input v-model="form.empId" placeholder="例: E006" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="氏名" prop="name">
          <el-input v-model="form.name" placeholder="例: 山本太郎" />
        </el-form-item>
        <el-form-item label="部署" prop="dept">
          <el-select v-model="form.dept" placeholder="選択してください" style="width:100%">
            <el-option label="総務部" value="総務部" />
            <el-option label="システム部" value="システム部" />
            <el-option label="営業部" value="営業部" />
            <el-option label="経理部" value="経理部" />
          </el-select>
        </el-form-item>
        <el-form-item label="課" prop="division">
          <el-input v-model="form.division" placeholder="例: 総務第一課" />
        </el-form-item>
        <el-form-item label="性別" prop="gender">
          <el-radio-group v-model="form.gender">
            <el-radio value="male">男性</el-radio>
            <el-radio value="female">女性</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="雇用形態" prop="empType">
          <el-radio-group v-model="form.empType">
            <el-radio value="regular">正社員</el-radio>
            <el-radio value="contract">契約社員</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="携帯電話" prop="mobile">
          <el-input v-model="form.mobile" placeholder="例: 090-1234-5678" />
        </el-form-item>
        <el-form-item label="メール" prop="email">
          <el-input v-model="form.email" placeholder="例: yamamoto@example.co.jp" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dlgVisible = false">キャンセル</el-button>
        <el-button type="primary" :loading="saving" @click="onSave">保存する</el-button>
      </template>
    </el-dialog>

    <!-- 中国出張社員 ダイアログ -->
    <el-dialog v-if="activeTab === 'cn'" v-model="dlgVisible" :title="isEdit ? '中国社員 編集' : '中国社員 新規登録'" width="520px"
      :close-on-click-modal="false" @close="onDlgClose">
      <el-form ref="formRef" :model="form" :rules="cnRules" label-width="110px">
        <el-form-item label="社員ID" prop="empId">
          <el-input v-model="form.empId" placeholder="例: C005" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="氏名" prop="name">
          <el-input v-model="form.name" placeholder="例: 李小龍" />
        </el-form-item>
        <el-form-item label="出身拠点" prop="origin">
          <el-radio-group v-model="form.origin">
            <el-radio value="shenyang">瀋陽</el-radio>
            <el-radio value="dalian">大連</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="性別" prop="gender">
          <el-radio-group v-model="form.gender">
            <el-radio value="male">男性</el-radio>
            <el-radio value="female">女性</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="出張区分" prop="bizType">
          <el-radio-group v-model="form.bizType">
            <el-radio value="longterm">長期出張</el-radio>
            <el-radio value="shortterm">短期出張</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="来日日" prop="arrivalDate">
          <el-date-picker v-model="form.arrivalDate" type="date" placeholder="来日日を選択"
            value-format="YYYY-MM-DD" style="width:100%" />
        </el-form-item>
        <el-form-item label="帰国予定日" prop="departureDate">
          <el-date-picker v-model="form.departureDate" type="date" placeholder="帰国予定日を選択（任意）"
            value-format="YYYY-MM-DD" style="width:100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dlgVisible = false">キャンセル</el-button>
        <el-button type="primary" :loading="saving" @click="onSave">保存する</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import { getEmployeeList, addEmployee, updateEmployee, deleteEmployee } from '@/api/index.js'

const loading = ref(false)
const saving = ref(false)
const dlgVisible = ref(false)
const isEdit = ref(false)
const selected = ref([])
const tableData = ref([])
const formRef = ref(null)
const activeTab = ref('jp')

const search = reactive({ name: '', empId: '' })
const page = reactive({ current: 1, size: 10, total: 0 })

const blankJpForm = () => ({
  id: null, empId: '', name: '', dept: '', division: '',
  gender: 'male', empType: 'regular', mobile: '', email: '', type: 'jp'
})
const blankCnForm = () => ({
  id: null, empId: '', name: '', origin: 'shenyang',
  gender: 'male', bizType: 'longterm', arrivalDate: '', departureDate: '', type: 'cn'
})

const form = reactive(blankJpForm())

const jpRules = {
  empId: [{ required: true, message: '社員IDは必須です', trigger: 'blur' }],
  name: [{ required: true, message: '氏名は必須です', trigger: 'blur' }],
  dept: [{ required: true, message: '部署を選択してください', trigger: 'change' }],
  division: [{ required: true, message: '課名は必須です', trigger: 'blur' }],
  gender: [{ required: true, trigger: 'change' }],
  empType: [{ required: true, trigger: 'change' }],
}

const cnRules = {
  empId: [{ required: true, message: '社員IDは必須です', trigger: 'blur' }],
  name: [{ required: true, message: '氏名は必須です', trigger: 'blur' }],
  origin: [{ required: true, trigger: 'change' }],
  gender: [{ required: true, trigger: 'change' }],
  bizType: [{ required: true, trigger: 'change' }],
  arrivalDate: [{ required: true, message: '来日日は必須です', trigger: 'change' }],
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getEmployeeList({ ...search, type: activeTab.value, page: page.current, pageSize: page.size })
    tableData.value = res.data
    page.total = res.total
  } finally {
    loading.value = false
  }
}

const onTabChange = () => {
  page.current = 1
  Object.assign(search, { name: '', empId: '' })
  loadData()
}

const onSearch = () => { page.current = 1; loadData() }
const onReset = () => { Object.assign(search, { name: '', empId: '' }); onSearch() }
const onSelect = (rows) => { selected.value = rows }

const onAdd = () => {
  isEdit.value = false
  const blank = activeTab.value === 'jp' ? blankJpForm() : blankCnForm()
  Object.assign(form, blank)
  dlgVisible.value = true
}

const onEdit = (row) => {
  isEdit.value = true
  Object.assign(form, { ...row })
  dlgVisible.value = true
}

const onDlgClose = () => {
  formRef.value?.resetFields()
  const blank = activeTab.value === 'jp' ? blankJpForm() : blankCnForm()
  Object.assign(form, blank)
}

const onSave = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    if (isEdit.value) {
      await updateEmployee(form.id, { ...form })
      ElMessage.success('更新しました')
    } else {
      await addEmployee({ ...form })
      ElMessage.success('登録しました')
    }
    dlgVisible.value = false
    loadData()
  } finally {
    saving.value = false
  }
}

const onDelete = (row) => {
  ElMessageBox.confirm(`「${row.name}」を削除してもよろしいですか？`, '削除確認', {
    type: 'warning', confirmButtonText: '削除する', cancelButtonText: 'キャンセル'
  }).then(async () => {
    await deleteEmployee(row.id)
    ElMessage.success('削除しました')
    loadData()
  }).catch(() => {})
}

const onBatchDelete = () => {
  ElMessageBox.confirm(`選択した ${selected.value.length} 件を削除してもよろしいですか？`, '一括削除', {
    type: 'warning', confirmButtonText: '削除する', cancelButtonText: 'キャンセル'
  }).then(async () => {
    for (const row of selected.value) await deleteEmployee(row.id)
    ElMessage.success(`${selected.value.length} 件削除しました`)
    loadData()
  }).catch(() => {})
}

onMounted(loadData)
</script>

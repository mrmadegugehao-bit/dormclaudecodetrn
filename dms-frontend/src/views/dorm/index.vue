<template>
  <div>
    <!-- 検索エリア -->
    <el-card shadow="never">
      <el-form :model="search" inline>
        <el-form-item label="寮名">
          <el-input v-model="search.name" placeholder="寮名で検索" clearable style="width:200px" />
        </el-form-item>
        <el-form-item label="寮種別">
          <el-select v-model="search.type" placeholder="全て" clearable style="width:140px">
            <el-option label="男性専用" value="male" />
            <el-option label="女性専用" value="female" />
            <el-option label="混合" value="mixed" />
          </el-select>
        </el-form-item>
        <el-form-item label="地域">
          <el-select v-model="search.region" placeholder="全て" clearable style="width:120px">
            <el-option label="東京" value="tokyo" />
            <el-option label="大阪" value="osaka" />
          </el-select>
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
          <span style="font-size:15px;font-weight:600;color:#303133">寮一覧</span>
          <el-button type="primary" :icon="Plus" @click="onAdd">新規登録</el-button>
        </div>
      </template>

      <el-table v-loading="loading" :data="tableData" stripe border @selection-change="onSelect">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column type="index" label="No." width="65" align="center" />
        <el-table-column prop="name" label="寮名" min-width="150" />
        <el-table-column prop="address" label="住所" min-width="220" show-overflow-tooltip />
        <el-table-column label="寮種別" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="typeMap[row.type]?.tagType" size="small">{{ typeMap[row.type]?.label }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="layout" label="間取り" width="90" align="center" />
        <el-table-column label="地域" width="120" align="center">
          <template #default="{ row }">{{ row.region === 'tokyo' ? '東京' : '大阪' }}</template>
        </el-table-column>
        <el-table-column label="入居状況" width="110" align="center">
          <template #default="{ row }">{{ row.occupiedRooms }} / {{ row.totalRooms }} 室</template>
        </el-table-column>
        <el-table-column label="入居率" width="150" align="center">
          <template #default="{ row }">
            <el-progress
              :percentage="row.totalRooms ? Math.round((row.occupiedRooms / row.totalRooms) * 100) : 0"
              :stroke-width="10"
              :color="progressColor(row.occupiedRooms, row.totalRooms)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center" fixed="right">
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

    <!-- 新增/編集ダイアログ -->
    <el-dialog v-model="dlgVisible" :title="isEdit ? '寮情報 編集' : '新規寮登録'" width="540px"
      :close-on-click-modal="false" @close="onDlgClose">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="寮名称" prop="name">
          <el-input v-model="form.name" placeholder="例: 江東区大島寮" />
        </el-form-item>
        <el-form-item label="住所" prop="address">
          <el-input v-model="form.address" placeholder="例: 東京都江東区大島3-1-1" />
        </el-form-item>
        <el-form-item label="寮種別" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio value="male">男性専用</el-radio>
            <el-radio value="female">女性専用</el-radio>
            <el-radio value="mixed">混合</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="間取り" prop="layout">
          <el-select v-model="form.layout" placeholder="選択してください">
            <el-option v-for="l in layouts" :key="l" :label="l" :value="l" />
          </el-select>
        </el-form-item>
        <el-form-item label="地域" prop="region">
          <el-radio-group v-model="form.region">
            <el-radio value="tokyo">東京</el-radio>
            <el-radio value="osaka">大阪</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="総部屋数" prop="totalRooms">
          <el-input-number v-model="form.totalRooms" :min="1" :max="30" />
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
import { getDormList, addDorm, updateDorm, deleteDorm } from '@/api/index.js'

const loading = ref(false)
const saving = ref(false)
const dlgVisible = ref(false)
const isEdit = ref(false)
const selected = ref([])
const tableData = ref([])
const formRef = ref(null)

const search = reactive({ name: '', type: '', region: '' })
const page = reactive({ current: 1, size: 10, total: 0 })

const typeMap = {
  male: { label: '男性専用', tagType: '' },
  female: { label: '女性専用', tagType: 'danger' },
  mixed: { label: '混合', tagType: 'warning' }
}
const layouts = ['1K', '1DK', '2DK', '3DK', '2LDK', '3LDK']

const blankForm = () => ({ id: null, name: '', address: '', type: 'male', layout: '2DK', region: 'tokyo', totalRooms: 1 })
const form = reactive(blankForm())

const rules = {
  name: [{ required: true, message: '寮名称は必須です', trigger: 'blur' }],
  address: [{ required: true, message: '住所は必須です', trigger: 'blur' }],
  type: [{ required: true, trigger: 'change' }],
  layout: [{ required: true, message: '間取りを選択してください', trigger: 'change' }],
  region: [{ required: true, trigger: 'change' }],
  totalRooms: [{ required: true, trigger: 'change' }],
}

const progressColor = (occupied, total) => {
  if (!total) return '#67C23A'
  const r = occupied / total
  if (r >= 0.9) return '#F56C6C'
  if (r >= 0.6) return '#E6A23C'
  return '#67C23A'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getDormList({ ...search, page: page.current, pageSize: page.size })
    tableData.value = res.data
    page.total = res.total
  } finally {
    loading.value = false
  }
}

const onSearch = () => { page.current = 1; loadData() }
const onReset = () => { Object.assign(search, { name: '', type: '', region: '' }); onSearch() }
const onSelect = (rows) => { selected.value = rows }

const onAdd = () => {
  isEdit.value = false
  Object.assign(form, blankForm())
  dlgVisible.value = true
}
const onEdit = (row) => {
  isEdit.value = true
  Object.assign(form, { ...row })
  dlgVisible.value = true
}
const onDlgClose = () => { formRef.value?.resetFields(); Object.assign(form, blankForm()) }

const onSave = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    if (isEdit.value) {
      await updateDorm(form.id, { ...form })
      ElMessage.success('更新しました')
    } else {
      await addDorm({ ...form })
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
    await deleteDorm(row.id)
    ElMessage.success('削除しました')
    loadData()
  }).catch(() => {})
}

const onBatchDelete = () => {
  ElMessageBox.confirm(`選択した ${selected.value.length} 件を削除してもよろしいですか？`, '一括削除', {
    type: 'warning', confirmButtonText: '削除する', cancelButtonText: 'キャンセル'
  }).then(async () => {
    for (const row of selected.value) await deleteDorm(row.id)
    ElMessage.success(`${selected.value.length} 件削除しました`)
    loadData()
  }).catch(() => {})
}

onMounted(loadData)
</script>

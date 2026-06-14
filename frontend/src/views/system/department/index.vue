<template>
  <div class="page-container">
    <div class="page-header">
      <div class="page-title">部门管理</div>
    </div>

    <div class="search-bar">
      <el-form :inline="true" :model="searchForm" @submit.prevent>
        <el-form-item label="部门名称">
          <el-input v-model="searchForm.deptName" placeholder="请输入部门名称" clearable style="width: 200px;" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 150px;">
            <el-option label="正常" :value="1" />
            <el-option label="停用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch"><el-icon><Search /></el-icon>搜索</el-button>
          <el-button @click="handleReset"><el-icon><Refresh /></el-icon>重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-container">
      <div class="toolbar">
        <el-button type="primary" v-if="userStore.checkPermission('dept:add')" @click="handleAdd">
          <el-icon><Plus /></el-icon>新增部门
        </el-button>
        <el-button type="success" v-if="sortChanged" :loading="sortSubmitting" @click="handleSaveSort">
          <el-icon><Check /></el-icon>保存排序
        </el-button>
        <el-button v-if="sortChanged" @click="handleResetSort">
          <el-icon><Close /></el-icon>取消排序修改
        </el-button>
      </div>

      <el-table :data="tableData" border stripe row-key="id" v-loading="loading" style="width: 100%"
        :tree-props="{ children: 'children' }" default-expand-all>
        <el-table-column prop="deptName" label="部门名称" min-width="280">
          <template #default="{ row }">
            <span>{{ row.deptName }}</span>
          </template>
        </el-table-column>
        <el-table-column label="排序" width="160" align="center">
          <template #default="{ row }">
            <div class="sort-input-wrapper">
              <el-input-number v-model="row.sortOrder" :min="0" :max="9999" size="small" controls-position="right"
                @change="() => handleSortChange(row)" />
            </div>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'primary' : 'info'" effect="light">
              {{ row.status === 1 ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180" align="center">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link size="small" v-if="userStore.checkPermission('dept:edit')"
              @click="handleEdit(row)">修改</el-button>
            <el-button type="success" link size="small" v-if="userStore.checkPermission('dept:add')"
              :disabled="row.status === 0" @click="handleAddChild(row)">新增</el-button>
            <el-button type="danger" link size="small" v-if="userStore.checkPermission('dept:delete')"
              @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="560px" @close="handleDialogClose">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="部门名称" prop="deptName">
          <el-input v-model="formData.deptName" placeholder="请输入部门名称" maxlength="50" show-word-limit />
        </el-form-item>
        <el-form-item label="上级部门" prop="parentId">
          <el-tree-select v-model="formData.parentId" :data="deptOptions" :props="{ label: 'deptName', value: 'id' }"
            :disabled="isEdit" check-strictly placeholder="请选择上级部门" style="width: 100%" clearable>
            <template #default="{ node, data }">
              <span :style="{ color: data.status === 0 ? '#909399' : '' }">{{ node.label }}</span>
              <el-tag v-if="data.status === 0" size="small" type="info" effect="plain" style="margin-left: 8px;">停用</el-tag>
            </template>
          </el-tree-select>
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="formData.sortOrder" :min="0" :max="9999" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, nextTick } from 'vue'
import { useUserStore } from '@/store/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getDepartmentTree, createDepartment, updateDepartment, deleteDepartment,
  getDepartmentOptions, batchUpdateSortOrder
} from '@/api/department'

const userStore = useUserStore()

const loading = ref(false)
const submitting = ref(false)
const sortSubmitting = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const tableData = ref([])
const originalTableData = ref([])
const deptOptions = ref([])
const sortChanged = ref(false)
const sortBackupMap = ref(new Map())

const searchForm = reactive({
  deptName: '',
  status: null
})

const dialogTitle = computed(() => isEdit.value ? '编辑部门' : (formData.parentId ? '新增子部门' : '新增部门'))

const formData = reactive({
  id: null,
  deptName: '',
  parentId: 0,
  sortOrder: 0,
  status: 1
})

const validateDeptName = (rule, value, callback) => {
  if (!value || !value.trim()) {
    callback(new Error('请输入部门名称'))
  } else if (value.trim().length > 50) {
    callback(new Error('部门名称最多50个字符'))
  } else {
    callback()
  }
}

const formRules = {
  deptName: [{ required: true, validator: validateDeptName, trigger: 'blur' }],
  sortOrder: [{ type: 'number', min: 0, max: 9999, message: '排序值范围0-9999', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  const d = new Date(dateTime)
  if (isNaN(d.getTime())) return dateTime
  const pad = (n) => n.toString().padStart(2, '0')
  return `${d.getFullYear()}${pad(d.getMonth() + 1)}${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}

const flattenTree = (tree, result = []) => {
  for (const node of tree) {
    result.push(node)
    if (node.children && node.children.length) {
      flattenTree(node.children, result)
    }
  }
  return result
}

const backupSortOrder = (tree) => {
  sortBackupMap.value = new Map()
  const flat = flattenTree(tree)
  for (const item of flat) {
    sortBackupMap.value.set(item.id, item.sortOrder)
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const params = { ...searchForm }
    const res = await getDepartmentTree(params)
    tableData.value = res.data || []
    originalTableData.value = JSON.parse(JSON.stringify(tableData.value))
    backupSortOrder(tableData.value)
    sortChanged.value = false
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const loadDeptOptions = async () => {
  try {
    const res = await getDepartmentOptions()
    deptOptions.value = [{ id: 0, deptName: '顶级部门', status: 1, children: [] }, ...(res.data || [])]
  } catch (e) {
    console.error(e)
  }
}

const handleSearch = () => {
  loadData()
}

const handleReset = () => {
  searchForm.deptName = ''
  searchForm.status = null
  loadData()
}

const handleSortChange = (row) => {
  const original = sortBackupMap.value.get(row.id)
  if (original !== undefined && original !== row.sortOrder) {
    sortChanged.value = true
  } else {
    const flat = flattenTree(tableData.value)
    let hasChange = false
    for (const item of flat) {
      const orig = sortBackupMap.value.get(item.id)
      if (orig !== undefined && orig !== item.sortOrder) {
        hasChange = true
        break
      }
    }
    sortChanged.value = hasChange
  }
}

const handleResetSort = () => {
  const restoreSort = (tree) => {
    for (const node of tree) {
      const orig = sortBackupMap.value.get(node.id)
      if (orig !== undefined) {
        node.sortOrder = orig
      }
      if (node.children && node.children.length) {
        restoreSort(node.children)
      }
    }
  }
  restoreSort(tableData.value)
  sortChanged.value = false
}

const handleSaveSort = async () => {
  const flat = flattenTree(tableData.value)
  const sortData = flat.map(item => ({ id: item.id, sortOrder: item.sortOrder }))
  sortSubmitting.value = true
  try {
    await batchUpdateSortOrder(sortData)
    ElMessage.success('排序保存成功')
    backupSortOrder(tableData.value)
    sortChanged.value = false
  } catch (e) {
    console.error(e)
  } finally {
    sortSubmitting.value = false
  }
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(formData, { id: null, deptName: '', parentId: 0, sortOrder: 0, status: 1 })
  dialogVisible.value = true
  nextTick(() => loadDeptOptions())
}

const handleAddChild = (row) => {
  if (row.status === 0) {
    ElMessage.warning('停用的部门不可新增子部门')
    return
  }
  isEdit.value = false
  Object.assign(formData, {
    id: null,
    deptName: '',
    parentId: row.id,
    sortOrder: 0,
    status: 1
  })
  dialogVisible.value = true
  nextTick(() => loadDeptOptions())
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(formData, {
    id: row.id,
    deptName: row.deptName,
    parentId: row.parentId || 0,
    sortOrder: row.sortOrder,
    status: row.status
  })
  dialogVisible.value = true
  nextTick(() => loadDeptOptions())
}

const handleDialogClose = () => {
  formRef.value?.resetFields()
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    submitting.value = true
    if (isEdit.value) {
      await updateDepartment(formData.id, {
        deptName: formData.deptName.trim(),
        sortOrder: formData.sortOrder,
        status: formData.status
      })
      ElMessage.success('更新成功')
    } else {
      await createDepartment({
        deptName: formData.deptName.trim(),
        parentId: formData.parentId,
        sortOrder: formData.sortOrder,
        status: formData.status
      })
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (e) {
    console.error(e)
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (row) => {
  if (row.children && row.children.length > 0) {
    ElMessage.error('该部门下存在子部门，无法删除，请先删除子部门')
    return
  }
  try {
    await ElMessageBox.confirm('确认删除该部门吗？删除后不可恢复', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteDepartment(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel' && !(e.message?.includes('cancel'))) {
      console.error(e)
    }
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.page-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.search-bar {
  background: #fff;
  padding: 18px 20px;
  border-radius: 4px;
  margin-bottom: 20px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
}

.table-container {
  background: #fff;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
}

.toolbar {
  margin-bottom: 16px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.sort-input-wrapper {
  display: flex;
  justify-content: center;
}
</style>

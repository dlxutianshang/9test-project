<template>
  <div class="page-container">
    <div class="page-header">
      <div class="page-title">岗位管理</div>
    </div>

    <div class="search-bar">
      <el-form :inline="true" :model="searchForm" @submit.prevent>
        <el-form-item label="岗位编码">
          <el-input v-model="searchForm.postCode" placeholder="请输入岗位编码" clearable style="width: 180px;" />
        </el-form-item>
        <el-form-item label="岗位名称">
          <el-input v-model="searchForm.postName" placeholder="请输入岗位名称" clearable style="width: 180px;" />
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
        <el-button type="primary" v-if="userStore.checkPermission('post:add')" @click="handleAdd">
          <el-icon><Plus /></el-icon>新增
        </el-button>
        <el-button type="primary" v-if="userStore.checkPermission('post:edit')" :disabled="selectedRows.length !== 1"
          @click="handleEditSelected">
          <el-icon><Edit /></el-icon>修改
        </el-button>
        <el-button type="danger" v-if="userStore.checkPermission('post:delete')" :disabled="selectedRows.length === 0"
          @click="handleBatchDelete">
          <el-icon><Delete /></el-icon>删除
        </el-button>
        <el-button type="warning" @click="handleExport" :loading="exporting">
          <el-icon><Download /></el-icon>导出
        </el-button>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading" style="width: 100%"
        @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column prop="id" label="岗位编号" width="100" align="center" />
        <el-table-column prop="postCode" label="岗位编码" width="160" />
        <el-table-column prop="postName" label="岗位名称" width="160" />
        <el-table-column prop="sortOrder" label="岗位排序" width="100" align="center" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="primary" effect="light">正常</el-tag>
            <el-tag v-else type="info" effect="plain" style="background-color:#f0f0f0;color:#909399;border-color:#dcdfe6;">停用</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="200" align="center">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link size="small" v-if="userStore.checkPermission('post:edit')"
              @click="handleEdit(row)">修改</el-button>
            <el-button type="danger" link size="small" v-if="userStore.checkPermission('post:delete')"
              @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" @close="handleDialogClose">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="岗位编号" v-if="isEdit">
          <el-input v-model="formData.id" disabled />
        </el-form-item>
        <el-form-item label="岗位编码" prop="postCode">
          <el-input v-model="formData.postCode" placeholder="请输入岗位编码" maxlength="50" show-word-limit />
        </el-form-item>
        <el-form-item label="岗位名称" prop="postName">
          <el-input v-model="formData.postName" placeholder="请输入岗位名称" maxlength="50" show-word-limit />
        </el-form-item>
        <el-form-item label="岗位排序" prop="sortOrder">
          <el-input-number v-model="formData.sortOrder" :min="0" controls-position="right" style="width: 100%" />
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
import { ref, reactive, onMounted, computed } from 'vue'
import { useUserStore } from '@/store/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPostPage, createPost, updatePost, deletePost, deletePosts, checkPostHasUsers, exportPosts } from '@/api/post'

const userStore = useUserStore()

const loading = ref(false)
const submitting = ref(false)
const exporting = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const tableData = ref([])
const selectedRows = ref([])

const searchForm = reactive({
  postCode: '',
  postName: '',
  status: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const dialogTitle = computed(() => isEdit.value ? '编辑岗位' : '新增岗位')

const formData = reactive({
  id: null,
  postCode: '',
  postName: '',
  sortOrder: 0,
  status: 1
})

const formRules = {
  postCode: [
    { required: true, message: '请输入岗位编码', trigger: 'blur' },
    { max: 50, message: '岗位编码最多50个字符', trigger: 'blur' }
  ],
  postName: [
    { required: true, message: '请输入岗位名称', trigger: 'blur' },
    { max: 50, message: '岗位名称最多50个字符', trigger: 'blur' }
  ],
  sortOrder: [{ type: 'number', min: 0, message: '排序值必须为非负整数', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  const d = new Date(dateTime)
  if (isNaN(d.getTime())) return dateTime
  const pad = (n) => n.toString().padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}

const padNum = (n) => n.toString().padStart(2, '0')
const formatFileName = () => {
  const d = new Date()
  return `岗位管理_${d.getFullYear()}${padNum(d.getMonth() + 1)}${padNum(d.getDate())}_${padNum(d.getHours())}${padNum(d.getMinutes())}${padNum(d.getSeconds())}.xlsx`
}

const loadData = async () => {
  loading.value = true
  try {
    const params = { ...searchForm, pageNum: pagination.pageNum, pageSize: pagination.pageSize }
    const res = await getPostPage(params)
    tableData.value = res.data.records || []
    pagination.total = res.data.total || 0
  } catch (e) {} finally {
    loading.value = false
  }
}

const handleSearch = () => { pagination.pageNum = 1; loadData() }
const handleReset = () => {
  searchForm.postCode = ''; searchForm.postName = ''; searchForm.status = null
  pagination.pageNum = 1; pagination.pageSize = 10; loadData()
}
const handlePageChange = (val) => { pagination.pageNum = val; loadData() }
const handleSizeChange = (val) => { pagination.pageSize = val; pagination.pageNum = 1; loadData() }

const handleSelectionChange = (rows) => { selectedRows.value = rows }

const handleAdd = () => {
  isEdit.value = false
  Object.assign(formData, { id: null, postCode: '', postName: '', sortOrder: 0, status: 1 })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(formData, JSON.parse(JSON.stringify(row)))
  dialogVisible.value = true
}

const handleEditSelected = () => {
  if (selectedRows.value.length === 1) {
    handleEdit(selectedRows.value[0])
  }
}

const handleDialogClose = () => { formRef.value?.resetFields() }

const handleSubmit = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    submitting.value = true
    if (isEdit.value) {
      if (formData.status === 0) {
        const hasRes = await checkPostHasUsers(formData.id)
        if (hasRes.data === true) {
          try {
            await ElMessageBox.confirm('该岗位已分配用户，停用后将影响用户权限，是否继续？', '提示', {
              confirmButtonText: '继续停用',
              cancelButtonText: '取消',
              type: 'warning'
            })
          } catch (_) {
            submitting.value = false
            return
          }
        }
      }
      await updatePost(formData.id, formData)
      ElMessage.success('更新成功')
    } else {
      await createPost(formData)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (e) {} finally { submitting.value = false }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除选中的岗位吗？删除后不可恢复', '删除确认', {
      confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
    })
    await deletePost(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel' && !(e.message?.includes('cancel'))) {}
  }
}

const handleBatchDelete = async () => {
  if (selectedRows.value.length === 0) return
  try {
    await ElMessageBox.confirm('确认删除选中的岗位吗？删除后不可恢复', '删除确认', {
      confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
    })
    const ids = selectedRows.value.map(row => row.id)
    await deletePosts(ids)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel' && !(e.message?.includes('cancel'))) {}
  }
}

const handleExport = async () => {
  exporting.value = true
  try {
    const params = { ...searchForm }
    const res = await exportPosts(params)
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const link = document.createElement('a')
    link.href = URL.createObjectURL(blob)
    link.download = formatFileName()
    link.click()
    URL.revokeObjectURL(link.href)
    ElMessage.success('导出成功')
  } catch (e) {} finally {
    exporting.value = false
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

.pagination-container {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>

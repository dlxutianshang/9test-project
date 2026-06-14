<template>
  <div class="page-container">
    <div class="page-header">
      <div class="page-title">通知公告管理</div>
    </div>

    <div class="search-bar">
      <el-form :inline="true" :model="searchForm" @submit.prevent>
        <el-form-item label="公告标题">
          <el-input v-model="searchForm.title" placeholder="请输入公告标题" clearable style="width: 180px;" />
        </el-form-item>
        <el-form-item label="发布人">
          <el-input v-model="searchForm.creator" placeholder="请输入发布人" clearable style="width: 180px;" />
        </el-form-item>
        <el-form-item label="公告类型">
          <el-select v-model="searchForm.type" placeholder="请选择类型" clearable style="width: 150px;">
            <el-option label="通知" value="notice" />
            <el-option label="公告" value="announcement" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-container">
      <div class="toolbar">
        <el-button type="primary" v-if="userStore.checkPermission('announcement:add')" @click="handleAdd">新增公告</el-button>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading" style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" :index="indexMethod" />
        <el-table-column prop="title" label="公告标题" min-width="200">
          <template #default="{ row }">
            <el-link type="primary" @click="handleViewDetail(row)">{{ row.title }}</el-link>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="公告类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.type === 'notice'" type="warning">通知</el-tag>
            <el-tag v-else type="success">公告</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="primary" effect="light">正常</el-tag>
            <el-tag v-else type="info" effect="plain">停用</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="creator" label="创建者" width="120" />
        <el-table-column prop="createTime" label="创建时间" width="120" align="center">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleViewReadUsers(row)">阅读用户</el-button>
            <el-button type="primary" link size="small" v-if="userStore.checkPermission('announcement:edit')" @click="handleEdit(row)">修改</el-button>
            <el-button type="danger" link size="small" v-if="userStore.checkPermission('announcement:delete')" @click="handleDelete(row)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" @close="handleDialogClose">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="公告标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入公告标题" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="公告类型" prop="type">
          <el-radio-group v-model="formData.type">
            <el-radio label="notice">通知</el-radio>
            <el-radio label="announcement">公告</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="公告内容" prop="content">
          <el-input v-model="formData.content" type="textarea" :rows="8" placeholder="请输入公告内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="readUsersVisible" title="阅读用户列表" width="400px">
      <div class="read-users-info">
        <span>共 {{ readUsers.length }} 人已阅读</span>
      </div>
      <el-table :data="readUsers" border stripe style="width: 100%; max-height: 300px; overflow-y: auto;">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="username" label="用户名" />
      </el-table>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="公告详情" width="600px">
      <div class="detail-header">
        <h2>{{ detailData.title }}</h2>
        <div class="detail-meta">
          <span>类型：<el-tag size="small" :type="detailData.type === 'notice' ? 'warning' : 'success'">{{ detailData.type === 'notice' ? '通知' : '公告' }}</el-tag></span>
          <span>状态：<el-tag size="small" :type="detailData.status === 1 ? 'primary' : 'info'">{{ detailData.status === 1 ? '正常' : '停用' }}</el-tag></span>
          <span>创建者：{{ detailData.creator }}</span>
          <span>创建时间：{{ formatDate(detailData.createTime) }}</span>
        </div>
      </div>
      <div class="detail-content">
        <p>{{ detailData.content }}</p>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useUserStore } from '@/store/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAnnouncementPage, getAnnouncementById, getReadUsers, createAnnouncement, updateAnnouncement, deleteAnnouncement } from '@/api/announcement'

const userStore = useUserStore()

const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const readUsersVisible = ref(false)
const detailVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const tableData = ref([])
const readUsers = ref([])
const detailData = ref({})

const searchForm = reactive({
  title: '',
  creator: '',
  type: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const dialogTitle = computed(() => isEdit.value ? '编辑公告' : '新增公告')

const formData = reactive({
  id: null,
  title: '',
  content: '',
  type: 'notice',
  status: 1
})

const formRules = {
  title: [
    { required: true, message: '请输入公告标题', trigger: 'blur' },
    { max: 200, message: '公告标题最多200个字符', trigger: 'blur' }
  ],
  type: [{ required: true, message: '请选择公告类型', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const formatDate = (dateTime) => {
  if (!dateTime) return ''
  const d = new Date(dateTime)
  if (isNaN(d.getTime())) return dateTime
  const pad = (n) => n.toString().padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`
}

const indexMethod = (index) => {
  return (pagination.pageNum - 1) * pagination.pageSize + index + 1
}

const loadData = async () => {
  loading.value = true
  try {
    const params = { ...searchForm, pageNum: pagination.pageNum, pageSize: pagination.pageSize }
    const res = await getAnnouncementPage(params)
    tableData.value = res.data.records || []
    pagination.total = res.data.total || 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => { pagination.pageNum = 1; loadData() }
const handleReset = () => {
  searchForm.title = ''; searchForm.creator = ''; searchForm.type = ''
  pagination.pageNum = 1; pagination.pageSize = 10; loadData()
}
const handlePageChange = (val) => { pagination.pageNum = val; loadData() }
const handleSizeChange = (val) => { pagination.pageSize = val; pagination.pageNum = 1; loadData() }

const handleAdd = () => {
  isEdit.value = false
  Object.assign(formData, { id: null, title: '', content: '', type: 'notice', status: 1 })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(formData, JSON.parse(JSON.stringify(row)))
  dialogVisible.value = true
}

const handleDialogClose = () => { formRef.value?.resetFields() }

const handleSubmit = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    submitting.value = true
    if (isEdit.value) {
      await updateAnnouncement(formData.id, formData)
      ElMessage.success('更新成功')
    } else {
      await createAnnouncement(formData)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } finally { submitting.value = false }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该公告吗？删除后不可恢复', '删除确认', {
      confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
    })
    await deleteAnnouncement(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {}
}

const handleViewReadUsers = async (row) => {
  try {
    const res = await getReadUsers(row.id)
    readUsers.value = (res.data || []).map(username => ({ username }))
    readUsersVisible.value = true
  } catch (e) {
    readUsers.value = []
    readUsersVisible.value = true
  }
}

const handleViewDetail = async (row) => {
  try {
    const res = await getAnnouncementById(row.id)
    detailData.value = res.data || {}
    detailVisible.value = true
  } catch (e) {}
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
}

.pagination-container {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

.read-users-info {
  margin-bottom: 16px;
  color: #606266;
}

.detail-header {
  text-align: center;
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 16px;
}

.detail-header h2 {
  margin: 0 0 12px 0;
  font-size: 18px;
  color: #303133;
}

.detail-meta {
  display: flex;
  justify-content: center;
  gap: 16px;
  font-size: 12px;
  color: #909399;
  flex-wrap: wrap;
}

.detail-content {
  padding: 16px 0;
  line-height: 1.8;
  color: #606266;
}
</style>

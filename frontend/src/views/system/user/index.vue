<template>
  <div class="page-container">
    <div class="page-header">
      <div class="page-title">用户管理</div>
    </div>

    <div class="search-bar">
      <el-form :inline="true" :model="searchForm" @submit.prevent>
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable style="width: 180px;" />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="searchForm.nickname" placeholder="请输入昵称" clearable style="width: 180px;" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 150px;">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
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
        <el-button type="primary" v-if="userStore.checkPermission('user:add')" @click="handleAdd">
          <el-icon><Plus /></el-icon>新增用户
        </el-button>
        <el-button type="success" v-if="userStore.checkPermission('import:data')" @click="handleImport">
          <el-icon><Upload /></el-icon>导入
        </el-button>
        <el-button type="warning" v-if="userStore.checkPermission('export:data')" @click="handleExport">
          <el-icon><Download /></el-icon>导出
        </el-button>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="username" label="用户名" width="140" />
        <el-table-column prop="nickname" label="昵称" width="140" />
        <el-table-column prop="email" label="邮箱" width="200" />
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-switch v-model="row.status" :active-value="1" :inactive-value="0"
              @change="(val) => handleStatusChange(row, val)"
              :disabled="!userStore.checkPermission('user:status') || row.id === userStore.userInfo?.id" />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="320" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link size="small" v-if="userStore.checkPermission('user:edit')" @click="handleEdit(row)">编辑</el-button>
            <el-button type="success" link size="small" v-if="userStore.checkPermission('user:edit')" @click="handleAssignRole(row)">分配角色</el-button>
            <el-button type="warning" link size="small" v-if="userStore.checkPermission('user:resetPassword')" @click="handleResetPassword(row)">重置密码</el-button>
            <el-button type="danger" link size="small" v-if="userStore.checkPermission('user:delete')" @click="handleDelete(row)"
              :disabled="row.id === userStore.userInfo?.id">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" @close="handleDialogClose">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" :disabled="isEdit" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input v-model="formData.password" type="password" show-password placeholder="请输入密码，默认123456" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="formData.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="状态" prop="status" v-if="isEdit">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="roleDialogVisible" title="分配角色" width="500px">
      <el-form label-width="80px">
        <el-form-item label="用户">
          <el-tag type="primary">{{ formData.username }}</el-tag>
        </el-form-item>
        <el-form-item label="选择角色">
          <el-checkbox-group v-model="selectedRoleIds">
            <el-checkbox v-for="role in allRoles" :key="role.id" :label="role.id">
              {{ role.roleName }}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleAssignRoleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="resetDialogVisible" title="重置密码" width="400px">
      <el-form :model="resetForm" :rules="resetRules" ref="resetFormRef" label-width="100px">
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="resetForm.newPassword" type="password" show-password placeholder="默认123456" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="resetForm.confirmPassword" type="password" show-password placeholder="请再次输入" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="resetDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleResetSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="importDialogVisible" title="导入用户" width="500px">
      <el-alert type="info" :closable="false" show-icon style="margin-bottom: 16px;">
        <template #title>
          <div>Excel文件格式：用户名、昵称、邮箱、手机号、状态（启用/禁用）</div>
        </template>
      </el-alert>
      <el-upload
        ref="uploadRef"
        :auto-upload="false"
        :limit="1"
        :on-change="handleFileChange"
        accept=".xlsx,.xls"
        drag
      >
        <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
        <div class="el-upload__text">拖拽文件到此处，或<em>点击上传</em></div>
        <template #tip>
          <div class="el-upload__tip">仅支持 xlsx/xls 格式文件</div>
        </template>
      </el-upload>
      <template #footer>
        <el-button @click="importDialogVisible = false">取消</el-button>
        <el-button type="primary" :disabled="!uploadFile" @click="handleImportSubmit">确定导入</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useUserStore } from '@/store/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getUserPage, createUser, updateUser, deleteUser,
  updateUserStatus, resetUserPassword, assignRoles,
  exportUsers, importUsers
} from '@/api/user'
import { getAllRoles } from '@/api/role'

const userStore = useUserStore()

const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const roleDialogVisible = ref(false)
const resetDialogVisible = ref(false)
const importDialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const resetFormRef = ref(null)
const uploadRef = ref(null)
const uploadFile = ref(null)
const allRoles = ref([])
const selectedRoleIds = ref([])
const tableData = ref([])

const searchForm = reactive({
  username: '',
  nickname: '',
  status: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const dialogTitle = computed(() => isEdit.value ? '编辑用户' : '新增用户')

const formData = reactive({
  id: null,
  username: '',
  password: '',
  nickname: '',
  email: '',
  phone: '',
  status: 1
})

const resetForm = reactive({
  newPassword: '123456',
  confirmPassword: '123456'
})

const resetUser = reactive({ id: null })

const validateConfirm = (rule, value, callback) => {
  if (value !== resetForm.newPassword) callback(new Error('两次输入不一致'))
  else callback()
}

const formRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '长度在3-50个字符', trigger: 'blur' }
  ],
  password: isEdit.value ? [] : [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const resetRules = {
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '至少6个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入', trigger: 'blur' },
    { validator: validateConfirm, trigger: 'blur' }
  ]
}

const loadData = async () => {
  loading.value = true
  try {
    const params = { ...searchForm, pageNum: pagination.pageNum, pageSize: pagination.pageSize }
    const res = await getUserPage(params)
    tableData.value = res.data.records || []
    pagination.total = res.data.total || 0
  } catch (e) {} finally {
    loading.value = false
  }
}

const loadAllRoles = async () => {
  try {
    const res = await getAllRoles()
    allRoles.value = res.data || []
  } catch (e) {}
}

const handleSearch = () => {
  pagination.pageNum = 1
  loadData()
}

const handleReset = () => {
  searchForm.username = ''
  searchForm.nickname = ''
  searchForm.status = null
  handleSearch()
}

const handlePageChange = (val) => {
  pagination.pageNum = val
  loadData()
}

const handleSizeChange = (val) => {
  pagination.pageSize = val
  pagination.pageNum = 1
  loadData()
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(formData, { id: null, username: '', password: '', nickname: '', email: '', phone: '', status: 1 })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(formData, row)
  formData.password = ''
  dialogVisible.value = true
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
      await updateUser(formData.id, formData)
      ElMessage.success('更新成功')
    } else {
      await createUser(formData)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (e) {} finally {
    submitting.value = false
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除用户"${row.username}"吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteUser(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {}
}

const handleStatusChange = async (row, val) => {
  try {
    await updateUserStatus(row.id, val)
    ElMessage.success('状态更新成功')
  } catch (e) {
    row.status = row.status === 1 ? 0 : 1
  }
}

const handleAssignRole = async (row) => {
  Object.assign(formData, row)
  selectedRoleIds.value = []
  try {
    const { getUserRoles } = await import('@/api/user')
    const res = await getUserRoles(row.id)
    selectedRoleIds.value = (res.data || []).map(r => r.id)
  } catch (e) {}
  roleDialogVisible.value = true
}

const handleAssignRoleSubmit = async () => {
  try {
    submitting.value = true
    await assignRoles({ userId: formData.id, roleIds: selectedRoleIds.value })
    ElMessage.success('分配成功')
    roleDialogVisible.value = false
    loadData()
  } catch (e) {} finally {
    submitting.value = false
  }
}

const handleResetPassword = (row) => {
  resetUser.id = row.id
  resetForm.newPassword = '123456'
  resetForm.confirmPassword = '123456'
  resetDialogVisible.value = true
}

const handleResetSubmit = async () => {
  if (!resetFormRef.value) return
  try {
    await resetFormRef.value.validate()
    await resetUserPassword(resetUser.id, resetForm.newPassword)
    ElMessage.success('密码重置成功')
    resetDialogVisible.value = false
  } catch (e) {}
}

const handleExport = async () => {
  try {
    const params = { ...searchForm }
    const res = await exportUsers(params)
    const blob = new Blob([res], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    })
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `用户列表_${Date.now()}.xlsx`
    a.click()
    URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (e) {}
}

const handleImport = () => {
  uploadFile.value = null
  uploadRef.value?.clearFiles()
  importDialogVisible.value = true
}

const handleFileChange = (file) => {
  uploadFile.value = file.raw
}

const handleImportSubmit = async () => {
  if (!uploadFile.value) return
  try {
    submitting.value = true
    await importUsers(uploadFile.value)
    ElMessage.success('导入成功')
    importDialogVisible.value = false
    loadData()
  } catch (e) {} finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadData()
  loadAllRoles()
})
</script>

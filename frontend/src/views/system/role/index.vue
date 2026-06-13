<template>
  <div class="page-container">
    <div class="page-header">
      <div class="page-title">角色管理</div>
    </div>

    <div class="search-bar">
      <el-form :inline="true" :model="searchForm" @submit.prevent>
        <el-form-item label="角色名称">
          <el-input v-model="searchForm.roleName" placeholder="请输入角色名称" clearable style="width: 180px;" />
        </el-form-item>
        <el-form-item label="角色编码">
          <el-input v-model="searchForm.roleCode" placeholder="请输入角色编码" clearable style="width: 180px;" />
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
        <el-button type="primary" v-if="userStore.checkPermission('role:add')" @click="handleAdd">
          <el-icon><Plus /></el-icon>新增角色
        </el-button>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="roleName" label="角色名称" width="160" />
        <el-table-column prop="roleCode" label="角色编码" width="200" />
        <el-table-column prop="description" label="描述" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="260" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link size="small" v-if="userStore.checkPermission('role:edit')" @click="handleEdit(row)">编辑</el-button>
            <el-button type="success" link size="small" v-if="userStore.checkPermission('role:assignPermission')" @click="handleAssignPermission(row)">分配权限</el-button>
            <el-button type="danger" link size="small" v-if="userStore.checkPermission('role:delete')" @click="handleDelete(row)"
              :disabled="row.roleCode === 'ROLE_ADMIN'">删除</el-button>
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
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="formData.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="formData.roleCode" :disabled="isEdit" placeholder="如：ROLE_XXX" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入角色描述" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
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

    <el-dialog v-model="permDialogVisible" title="分配权限" width="500px">
      <el-form label-width="80px">
        <el-form-item label="角色">
          <el-tag type="primary">{{ formData.roleName }}</el-tag>
        </el-form-item>
        <el-form-item label="权限">
          <el-tree
            ref="permTreeRef"
            :data="permissionTree"
            show-checkbox
            node-key="id"
            :default-checked-keys="selectedPermIds"
            :props="{ label: 'permissionName', children: 'children' }"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="permDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleAssignPermSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useUserStore } from '@/store/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getRolePage, createRole, updateRole, deleteRole,
  getRolePermissions, assignPermissions
} from '@/api/role'
import { getPermissionTree } from '@/api/permission'

const userStore = useUserStore()

const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const permDialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const permTreeRef = ref(null)
const tableData = ref([])
const permissionTree = ref([])
const selectedPermIds = ref([])

const searchForm = reactive({
  roleName: '',
  roleCode: '',
  status: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const dialogTitle = computed(() => isEdit.value ? '编辑角色' : '新增角色')

const formData = reactive({
  id: null,
  roleName: '',
  roleCode: '',
  description: '',
  status: 1
})

const formRules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const params = { ...searchForm, pageNum: pagination.pageNum, pageSize: pagination.pageSize }
    const res = await getRolePage(params)
    tableData.value = res.data.records || []
    pagination.total = res.data.total || 0
  } catch (e) {} finally {
    loading.value = false
  }
}

const loadPermTree = async () => {
  try {
    const res = await getPermissionTree()
    permissionTree.value = res.data || []
  } catch (e) {}
}

const handleSearch = () => { pagination.pageNum = 1; loadData() }
const handleReset = () => {
  searchForm.roleName = ''; searchForm.roleCode = ''; searchForm.status = null
  handleSearch()
}
const handlePageChange = (val) => { pagination.pageNum = val; loadData() }
const handleSizeChange = (val) => { pagination.pageSize = val; pagination.pageNum = 1; loadData() }

const handleAdd = () => {
  isEdit.value = false
  Object.assign(formData, { id: null, roleName: '', roleCode: '', description: '', status: 1 })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDialogClose = () => { formRef.value?.resetFields() }

const handleSubmit = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    submitting.value = true
    if (isEdit.value) {
      await updateRole(formData.id, formData)
      ElMessage.success('更新成功')
    } else {
      await createRole(formData)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (e) {} finally { submitting.value = false }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除角色"${row.roleName}"吗？`, '提示', {
      confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
    })
    await deleteRole(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {}
}

const handleAssignPermission = async (row) => {
  Object.assign(formData, row)
  selectedPermIds.value = []
  try {
    const res = await getRolePermissions(row.id)
    selectedPermIds.value = (res.data || []).map(p => p.id)
  } catch (e) {}
  permDialogVisible.value = true
}

const handleAssignPermSubmit = async () => {
  try {
    submitting.value = true
    const checkedKeys = permTreeRef.value?.getCheckedKeys(false) || []
    const halfCheckedKeys = permTreeRef.value?.getHalfCheckedKeys() || []
    const allIds = [...checkedKeys, ...halfCheckedKeys]
    await assignPermissions({ roleId: formData.id, permissionIds: allIds })
    ElMessage.success('分配成功')
    permDialogVisible.value = false
  } catch (e) {} finally { submitting.value = false }
}

onMounted(() => {
  loadData()
  loadPermTree()
})
</script>

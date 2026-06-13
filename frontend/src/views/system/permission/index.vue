<template>
  <div class="page-container">
    <div class="page-header">
      <div class="page-title">权限管理</div>
    </div>

    <div class="table-container">
      <div class="toolbar">
        <el-button type="primary" v-if="userStore.checkPermission('permission:add')" @click="handleAdd(0)">
          <el-icon><Plus /></el-icon>新增顶级权限
        </el-button>
        <el-button v-if="userStore.checkPermission('permission:list')" @click="expandAll = !expandAll">
          <el-icon><Expand v-if="!expandAll" /><Fold v-else /></el-icon>
          {{ expandAll ? '全部收起' : '全部展开' }}
        </el-button>
      </div>

      <el-table :data="permissionTree" border stripe row-key="id" v-loading="loading"
        :default-expand-all="expandAll" style="width: 100%">
        <el-table-column prop="permissionName" label="权限名称" min-width="200" />
        <el-table-column prop="permissionCode" label="权限编码" width="240" />
        <el-table-column label="类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.type === 1 ? 'primary' : row.type === 2 ? 'success' : 'warning'" size="small">
              {{ row.type === 1 ? '菜单' : row.type === 2 ? '按钮' : '接口' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" />
        <el-table-column label="操作" width="280" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link size="small" v-if="userStore.checkPermission('permission:add')" @click="handleAdd(row.id)">
              新增子级
            </el-button>
            <el-button type="success" link size="small" v-if="userStore.checkPermission('permission:edit')" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link size="small" v-if="userStore.checkPermission('permission:delete')" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" @close="handleDialogClose">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="上级权限">
          <el-tree-select
            v-model="formData.parentId"
            :data="selectTree"
            :props="{ label: 'permissionName', value: 'id', children: 'children' }"
            placeholder="顶级权限（不选则为顶级）"
            check-strictly
            clearable
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="权限名称" prop="permissionName">
          <el-input v-model="formData.permissionName" placeholder="请输入权限名称" />
        </el-form-item>
        <el-form-item label="权限编码" prop="permissionCode">
          <el-input v-model="formData.permissionCode" :disabled="isEdit" placeholder="如：user:add" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-radio-group v-model="formData.type">
            <el-radio :label="1">菜单</el-radio>
            <el-radio :label="2">按钮</el-radio>
            <el-radio :label="3">接口</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="formData.sortOrder" :min="0" :max="9999" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="formData.description" type="textarea" :rows="3" />
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
import {
  getPermissionTree, createPermission, updatePermission, deletePermission
} from '@/api/permission'

const userStore = useUserStore()

const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const permissionTree = ref([])
const selectTree = ref([])
const expandAll = ref(true)

const dialogTitle = computed(() => isEdit.value ? '编辑权限' : '新增权限')

const formData = reactive({
  id: null,
  permissionName: '',
  permissionCode: '',
  description: '',
  type: 2,
  parentId: 0,
  sortOrder: 0,
  status: 1
})

const formRules = {
  permissionName: [{ required: true, message: '请输入权限名称', trigger: 'blur' }],
  permissionCode: [{ required: true, message: '请输入权限编码', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getPermissionTree()
    permissionTree.value = res.data || []
    selectTree.value = JSON.parse(JSON.stringify(res.data || []))
  } catch (e) {} finally { loading.value = false }
}

const handleAdd = (parentId) => {
  isEdit.value = false
  Object.assign(formData, {
    id: null, permissionName: '', permissionCode: '', description: '',
    type: parentId === 0 ? 1 : 2, parentId: parentId || 0, sortOrder: 0, status: 1
  })
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
      await updatePermission(formData.id, formData)
      ElMessage.success('更新成功')
    } else {
      await createPermission(formData)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (e) {} finally { submitting.value = false }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除权限"${row.permissionName}"吗？`, '提示', {
      confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
    })
    await deletePermission(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {}
}

onMounted(() => { loadData() })
</script>

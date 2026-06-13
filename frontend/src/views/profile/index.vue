<template>
  <div class="page-container">
    <div style="display: flex; justify-content: center;">
      <el-card style="width: 600px;">
        <template #header>
          <div class="card-header">
            <span><strong>个人中心</strong></span>
          </div>
        </template>
        <el-tabs v-model="activeTab">
          <el-tab-pane label="基本信息" name="info">
            <el-form :model="profileForm" label-width="100px">
              <el-form-item label="用户名">
                <el-input v-model="profileForm.username" disabled />
              </el-form-item>
              <el-form-item label="昵称">
                <el-input v-model="profileForm.nickname" />
              </el-form-item>
              <el-form-item label="邮箱">
                <el-input v-model="profileForm.email" />
              </el-form-item>
              <el-form-item label="手机号">
                <el-input v-model="profileForm.phone" />
              </el-form-item>
              <el-form-item label="状态">
                <el-tag type="success" v-if="profileForm.status === 1">已启用</el-tag>
                <el-tag type="danger" v-else>已禁用</el-tag>
              </el-form-item>
              <el-form-item label="注册时间">
                <span>{{ profileForm.createTime }}</span>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleSaveProfile" :loading="saving">保存修改</el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>
          <el-tab-pane label="修改密码" name="password">
            <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="100px">
              <el-form-item label="原密码" prop="oldPassword">
                <el-input v-model="passwordForm.oldPassword" type="password" show-password />
              </el-form-item>
              <el-form-item label="新密码" prop="newPassword">
                <el-input v-model="passwordForm.newPassword" type="password" show-password />
              </el-form-item>
              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleChangePassword" :loading="changingPwd">确认修改</el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>
          <el-tab-pane label="角色权限" name="roles">
            <el-descriptions :column="1" border>
              <el-descriptions-item label="拥有角色">
                <el-tag v-for="role in roles" :key="role.id" style="margin-right: 8px;">{{ role.roleName }}</el-tag>
                <span v-if="!roles.length">暂无角色</span>
              </el-descriptions-item>
              <el-descriptions-item label="权限列表">
                <div style="display: flex; flex-wrap: wrap; gap: 8px;">
                  <el-tag v-for="p in permissions" :key="p.id" :type="p.type === 1 ? 'primary' : 'success'" size="small">
                    {{ p.permissionName }}
                  </el-tag>
                  <span v-if="!permissions.length">暂无权限</span>
                </div>
              </el-descriptions-item>
            </el-descriptions>
          </el-tab-pane>
        </el-tabs>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import { updateUser } from '@/api/user'
import { changePassword } from '@/api/auth'
import { getUserRoles, getUserPermissions } from '@/api/user'

const userStore = useUserStore()
const activeTab = ref('info')
const saving = ref(false)
const changingPwd = ref(false)
const passwordFormRef = ref(null)
const roles = ref([])
const permissions = ref([])

const profileForm = reactive({
  id: null,
  username: '',
  nickname: '',
  email: '',
  phone: '',
  status: 1,
  createTime: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirm = (rule, value, callback) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 100, message: '密码长度在6-100个字符之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirm, trigger: 'blur' }
  ]
}

const loadUserInfo = async () => {
  const info = userStore.userInfo
  if (info) {
    Object.assign(profileForm, info)
  }
  if (profileForm.id) {
    try {
      const [roleRes, permRes] = await Promise.all([
        getUserRoles(profileForm.id),
        getUserPermissions(profileForm.id)
      ])
      roles.value = roleRes.data || []
      permissions.value = permRes.data || []
    } catch (e) {}
  }
}

const handleSaveProfile = async () => {
  try {
    saving.value = true
    await updateUser(profileForm.id, profileForm)
    ElMessage.success('保存成功')
    await userStore.getUserInfo()
    loadUserInfo()
  } catch (e) {} finally {
    saving.value = false
  }
}

const handleChangePassword = async () => {
  if (!passwordFormRef.value) return
  try {
    await passwordFormRef.value.validate()
    changingPwd.value = true
    await changePassword(passwordForm)
    ElMessage.success('密码修改成功，请重新登录')
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
    setTimeout(async () => {
      await userStore.logout()
      window.location.reload()
    }, 1000)
  } catch (e) {} finally {
    changingPwd.value = false
  }
}

onMounted(() => {
  loadUserInfo()
})
</script>

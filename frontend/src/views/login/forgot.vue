<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h2>忘记密码</h2>
        <p>通过邮箱找回密码</p>
      </div>
      <el-form :model="forgotForm" :rules="forgotRules" ref="forgotFormRef" class="login-form">
        <el-form-item prop="email">
          <el-input v-model="forgotForm.email" placeholder="请输入注册邮箱" prefix-icon="Message" size="large" />
        </el-form-item>
        <el-button type="primary" size="large" class="login-btn" :loading="loading" @click="handleSubmit">发送重置链接</el-button>
        <div class="login-links">
          <router-link to="/login">返回登录</router-link>
        </div>
        <el-alert v-if="tokenTip" type="success" :closable="false" style="margin-top: 20px;">
          <template #title>
            <div>重置令牌：<code>{{ tokenTip }}</code><br/>请使用此令牌调用重置密码接口，或联系管理员。</div>
          </template>
        </el-alert>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { forgotPassword } from '@/api/auth'

const forgotFormRef = ref(null)
const loading = ref(false)
const tokenTip = ref('')

const forgotForm = reactive({
  email: ''
})

const validateEmail = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入邮箱'))
  } else {
    const emailReg = /^[\w.-]+@[\w.-]+\.\w+$/
    if (!emailReg.test(value)) {
      callback(new Error('邮箱格式不正确'))
    } else {
      callback()
    }
  }
}

const forgotRules = {
  email: [
    { required: true, validator: validateEmail, trigger: 'blur' }
  ]
}

const handleSubmit = async () => {
  if (!forgotFormRef.value) return
  try {
    await forgotFormRef.value.validate()
    loading.value = true
    const res = await forgotPassword(forgotForm)
    tokenTip.value = '（请查看后端日志获取resetToken）'
    ElMessage.success(res.message || '重置链接已生成')
  } catch (error) {
    if (error?.message) {
      ElMessage.error(error.message)
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  width: 420px;
  padding: 40px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h2 {
  margin: 0 0 8px;
  font-size: 24px;
  color: #303133;
}

.login-header p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.login-btn {
  width: 100%;
  margin-top: 10px;
}

.login-links {
  margin-top: 16px;
  text-align: center;
  font-size: 14px;
}

.login-links a {
  color: #409eff;
}

code {
  background: #f5f7fa;
  padding: 2px 6px;
  border-radius: 3px;
  color: #67c23a;
}
</style>

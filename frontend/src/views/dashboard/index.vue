<template>
  <div class="page-container">
    <el-row :gutter="20">
      <el-col :span="6" v-for="(card, index) in cards" :key="index">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" :style="{ background: card.bg }">
              <el-icon :size="28"><component :is="card.icon" /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ card.value }}</div>
              <div class="stat-label">{{ card.label }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="14">
        <el-card>
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <span><strong>系统信息</strong></span>
            </div>
          </template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="系统名称">用户管理系统</el-descriptions-item>
            <el-descriptions-item label="版本号">v1.0.0</el-descriptions-item>
            <el-descriptions-item label="后端框架">Spring Boot 2.2.x</el-descriptions-item>
            <el-descriptions-item label="前端框架">Vue 3 + Element Plus</el-descriptions-item>
            <el-descriptions-item label="数据库">MySQL 5.7+</el-descriptions-item>
            <el-descriptions-item label="缓存">Redis</el-descriptions-item>
            <el-descriptions-item label="安全框架">Spring Security + JWT</el-descriptions-item>
            <el-descriptions-item label="持久层">MyBatis + Druid</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
      <el-col :span="10">
        <el-card>
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <span><strong>快捷入口</strong></span>
            </div>
          </template>
          <div class="quick-links">
            <el-card v-for="link in quickLinks" :key="link.path" shadow="hover" class="quick-card" @click="$router.push(link.path)">
              <el-icon :size="32" :color="link.color"><component :is="link.icon" /></el-icon>
              <div class="quick-label">{{ link.label }}</div>
            </el-card>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getUserPage } from '@/api/user'
import { getRolePage } from '@/api/role'
import { getAllPermissions } from '@/api/permission'
import { getLogPage } from '@/api/log'

const cards = ref([
  { label: '用户总数', value: 0, icon: 'User', bg: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
  { label: '角色总数', value: 0, icon: 'Avatar', bg: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' },
  { label: '权限总数', value: 0, icon: 'Lock', bg: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' },
  { label: '日志总数', value: 0, icon: 'Document', bg: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)' }
])

const quickLinks = ref([
  { label: '用户管理', path: '/users', icon: 'User', color: '#667eea' },
  { label: '角色管理', path: '/roles', icon: 'Avatar', color: '#f5576c' },
  { label: '权限管理', path: '/permissions', icon: 'Lock', color: '#4facfe' },
  { label: '操作日志', path: '/logs', icon: 'Document', color: '#43e97b' }
])

const loadStats = async () => {
  try {
    const [userRes, roleRes, permRes, logRes] = await Promise.all([
      getUserPage({ pageNum: 1, pageSize: 1 }),
      getRolePage({ pageNum: 1, pageSize: 1 }),
      getAllPermissions(),
      getLogPage({ pageNum: 1, pageSize: 1 })
    ])
    cards.value[0].value = userRes.data.total
    cards.value[1].value = roleRes.data.total
    cards.value[2].value = permRes.data?.length || permRes.data?.total || 0
    cards.value[3].value = logRes.data.total
  } catch (e) {
  }
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.stat-card {
  margin-bottom: 0;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}

.quick-links {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.quick-card {
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.quick-card:hover {
  transform: translateY(-4px);
}

.quick-label {
  margin-top: 8px;
  font-size: 14px;
  color: #606266;
}
</style>

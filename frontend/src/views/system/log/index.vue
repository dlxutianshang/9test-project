<template>
  <div class="page-container">
    <div class="page-header">
      <div class="page-title">操作日志</div>
    </div>

    <div class="search-bar">
      <el-form :inline="true" :model="searchForm" @submit.prevent>
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable style="width: 180px;" />
        </el-form-item>
        <el-form-item label="操作">
          <el-input v-model="searchForm.operation" placeholder="请输入操作描述" clearable style="width: 200px;" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 140px;">
            <el-option label="成功" :value="1" />
            <el-option label="失败" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch"><el-icon><Search /></el-icon>搜索</el-button>
          <el-button @click="handleReset"><el-icon><Refresh /></el-icon>重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-container">
      <el-table :data="tableData" border stripe v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="username" label="操作人" width="120" />
        <el-table-column prop="operation" label="操作" width="180" />
        <el-table-column prop="method" label="方法" min-width="280" show-overflow-tooltip />
        <el-table-column prop="ip" label="IP" width="140" />
        <el-table-column label="耗时" width="100" align="center">
          <template #default="{ row }">{{ row.duration }}ms</template>
        </el-table-column>
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="errorMsg" label="错误信息" min-width="180" show-overflow-tooltip />
        <el-table-column prop="createTime" label="操作时间" width="180" />
        <el-table-column label="操作" width="120" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleView(row)">详情</el-button>
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

    <el-dialog v-model="detailVisible" title="日志详情" width="700px">
      <el-descriptions :column="2" border v-if="currentLog">
        <el-descriptions-item label="ID">{{ currentLog.id }}</el-descriptions-item>
        <el-descriptions-item label="操作人">{{ currentLog.username }}</el-descriptions-item>
        <el-descriptions-item label="操作">{{ currentLog.operation }}</el-descriptions-item>
        <el-descriptions-item label="IP">{{ currentLog.ip }}</el-descriptions-item>
        <el-descriptions-item label="耗时">{{ currentLog.duration }}ms</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentLog.status === 1 ? 'success' : 'danger'" size="small">
            {{ currentLog.status === 1 ? '成功' : '失败' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="时间" :span="2">{{ currentLog.createTime }}</el-descriptions-item>
        <el-descriptions-item label="方法" :span="2" style="word-break: break-all;">{{ currentLog.method }}</el-descriptions-item>
        <el-descriptions-item label="请求参数" :span="2">
          <div style="max-height: 200px; overflow-y: auto; word-break: break-all; background: #f5f7fa; padding: 10px; border-radius: 4px;">
            {{ currentLog.params || '（无）' }}
          </div>
        </el-descriptions-item>
        <el-descriptions-item v-if="currentLog.errorMsg" label="错误信息" :span="2">
          <div style="color: #f56c6c; max-height: 150px; overflow-y: auto;">{{ currentLog.errorMsg }}</div>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getLogPage } from '@/api/log'

const loading = ref(false)
const detailVisible = ref(false)
const currentLog = ref(null)
const tableData = ref([])

const searchForm = reactive({
  username: '',
  operation: '',
  status: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const loadData = async () => {
  loading.value = true
  try {
    const params = { ...searchForm, pageNum: pagination.pageNum, pageSize: pagination.pageSize }
    const res = await getLogPage(params)
    tableData.value = res.data.records || []
    pagination.total = res.data.total || 0
  } catch (e) {} finally { loading.value = false }
}

const handleSearch = () => { pagination.pageNum = 1; loadData() }
const handleReset = () => {
  searchForm.username = ''; searchForm.operation = ''; searchForm.status = null
  handleSearch()
}
const handlePageChange = (val) => { pagination.pageNum = val; loadData() }
const handleSizeChange = (val) => { pagination.pageSize = val; pagination.pageNum = 1; loadData() }
const handleView = (row) => {
  currentLog.value = row
  detailVisible.value = true
}

onMounted(() => { loadData() })
</script>

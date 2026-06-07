<template>
  <div class="page-container">
    <van-nav-bar title="报修投诉" fixed placeholder>
      <template #right>
        <van-icon v-if="!isAdmin" name="plus" size="22" @click="router.push('/complaint-create')" />
      </template>
    </van-nav-bar>

    <van-tabs v-model:active="activeStatus" @change="onTabChange">
      <van-tab title="全部" name="" />
      <van-tab title="待处理" name="pending" />
      <van-tab title="处理中" name="processing" />
      <van-tab title="已完成" name="done" />
    </van-tabs>

    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list v-model:loading="loading" :finished="finished" @load="onLoad">
        <div v-for="item in list" :key="item.id" class="cp-card" @click="router.push('/complaint-detail/' + item.id)">
          <div class="cp-top">
            <van-tag :type="typeColor(item.type)" size="small">{{ typeLabel(item.type) }}</van-tag>
            <van-tag :type="statusColor(item.status)" size="small" style="margin-left:6px">{{ statusLabel(item.status) }}</van-tag>
            <span class="cp-title">{{ item.title }}</span>
          </div>
          <div class="cp-sub" v-if="isAdmin">{{ item.userName }} · {{ item.location || '未填地点' }}</div>
          <div class="cp-meta">{{ formatTime(item.createTime) }}</div>
        </div>
      </van-list>
    </van-pull-refresh>

    
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getMyComplaints, getAdminComplaints } from '../api/complaint'
import { useUserStore } from '../stores/user'


const router = useRouter()
const userStore = useUserStore()
const list = ref([])
const activeStatus = ref('')
const page = ref(1)
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)







const isAdmin = computed(() => userStore.userInfo?.role === 'admin')

function formatTime(t) { return t ? new Date(t).toLocaleDateString('zh-CN') : '' }
function typeLabel(t) { return ({ repair: '报修', complaint: '投诉', suggest: '建议' })[t] || t }
function statusLabel(s) { return ({ pending: '待处理', processing: '处理中', done: '已完成', rejected: '已驳回' })[s] || s }
function typeColor(t) { return ({ repair: 'primary', complaint: 'danger', suggest: 'warning' })[t] || '' }
function statusColor(s) { return ({ pending: 'warning', processing: 'primary', done: 'success', rejected: 'danger' })[s] || '' }






async function fetchData() {
  loading.value = true
  try {
    const api = isAdmin.value ? getAdminComplaints : getMyComplaints
    const res = await api({ page: page.value, size: 10, status: activeStatus.value || undefined })
    const records = res.data?.records || []
    if (page.value === 1) list.value = records; else list.value.push(...records)
    finished.value = records.length < 10
  } catch {} finally { loading.value = false; refreshing.value = false }
}

function onLoad() { if (loading.value) return; if (page.value > 1) { page.value++; fetchData() } }
function onRefresh() { page.value = 1; finished.value = false; fetchData() }
function onTabChange() { page.value = 1; finished.value = false; fetchData() }
onMounted(() => fetchData())
</script>

<style scoped>
.cp-card { background: #fff; margin: 8px 16px; padding: 14px; border-radius: 10px; cursor: pointer; }
.cp-top { display: flex; align-items: center; gap: 6px; }
.cp-title { font-size: 15px; font-weight: 500; margin-left: 6px; }
.cp-sub { font-size: 12px; color: #999; margin-top: 6px; }
.cp-meta { margin-top: 4px; font-size: 11px; color: #bbb; }



</style>

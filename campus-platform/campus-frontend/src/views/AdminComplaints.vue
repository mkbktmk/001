<template>
  <div class="page-container">
    <van-nav-bar title="工单管理" left-arrow @click-left="$router.back()" fixed placeholder />

    <van-tabs v-model:active="activeStatus" @change="onTabChange">
      <van-tab title="全部" name="" />
      <van-tab title="待处理" name="pending" />
      <van-tab title="处理中" name="processing" />
      <van-tab title="已完成" name="done" />
    </van-tabs>

    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list v-model:loading="loading" :finished="finished" @load="onLoad">
        <div v-for="item in list" :key="item.id" class="cp-card" @click="showDetail(item)">
          <div class="cp-top">
            <van-tag :type="typeColor(item.type)" size="small">{{ typeLabel(item.type) }}</van-tag>
            <van-tag :type="statusColor(item.status)" size="small" style="margin-left:6px">
              {{ statusLabel(item.status) }}
            </van-tag>
            <span class="cp-title">{{ item.title }}</span>
          </div>
          <div class="cp-sub">{{ item.userName }} · {{ item.location || '未填地点' }}</div>
          <div class="cp-meta">{{ formatTime(item.createTime) }}</div>
        </div>
      </van-list>
    </van-pull-refresh>

    <!-- 处理弹窗 -->
    <van-dialog v-model:show="showDialog" :title="current?.title" show-cancel-button="false">
      <div v-if="current" class="dialog-body">
        <p><strong>类型：</strong>{{ typeLabel(current.type) }}</p>
        <p><strong>提交人：</strong>{{ current.userName }}</p>
        <p v-if="current.location"><strong>地点：</strong>{{ current.location }}</p>
        <p><strong>描述：</strong>{{ current.description }}</p>

        <div v-if="current.status !== 'done' && current.status !== 'rejected'" class="handle-area">
          <div class="handle-title">处理操作</div>
          <van-radio-group v-model="handleStatus" direction="horizontal">
            <van-radio name="processing">处理中</van-radio>
            <van-radio name="done">已完成</van-radio>
            <van-radio name="rejected">已驳回</van-radio>
          </van-radio-group>
          <van-field v-model="handleReply" type="textarea" rows="2" placeholder="回复内容（选填）" />
          <van-button type="primary" size="small" block @click="handleSubmit(current.id)">确认处理</van-button>
        </div>

        <div v-if="current.reply" class="reply-box">
          <strong>📩 上次回复：</strong>{{ current.reply }}
        </div>
        <p v-if="current.rating"><strong>评分：</strong>{{ '⭐'.repeat(current.rating) }}</p>
      </div>
    </van-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAdminComplaints, handleComplaint } from '../api/complaint'
import { useUserStore } from '../stores/user'
import { showToast } from 'vant'

const userStore = useUserStore()
const list = ref([])
const activeStatus = ref('')
const page = ref(1)
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)
const showDialog = ref(false)
const current = ref(null)
const handleStatus = ref('processing')
const handleReply = ref('')

function formatTime(t) { return t ? new Date(t).toLocaleDateString('zh-CN') : '' }
function typeLabel(t) { return ({ repair: '报修', complaint: '投诉', suggest: '建议' })[t] || t }
function statusLabel(s) { return ({ pending: '待处理', processing: '处理中', done: '已完成', rejected: '已驳回' })[s] || s }
function typeColor(t) { return ({ repair: 'primary', complaint: 'danger', suggest: 'warning' })[t] || '' }
function statusColor(s) { return ({ pending: 'warning', processing: 'primary', done: 'success', rejected: 'danger' })[s] || '' }

function showDetail(item) {
  current.value = item
  handleStatus.value = 'processing'
  handleReply.value = ''
  showDialog.value = true
}

async function fetchData() {
  if (!userStore.userInfo || userStore.userInfo.role !== 'admin') return
  loading.value = true
  try {
    const res = await getAdminComplaints({
      page: page.value, size: 10,
      status: activeStatus.value || undefined
    })
    const records = res.data?.records || []
    if (page.value === 1) list.value = records
    else list.value.push(...records)
    finished.value = records.length < 10
  } catch {} finally { loading.value = false; refreshing.value = false }
}

async function handleSubmit(id) {
  try {
    await handleComplaint(id, handleStatus.value, handleReply.value)
    current.value.status = handleStatus.value
    current.value.reply = handleReply.value
    showToast('处理成功')
    showDialog.value = false
    // 刷新列表
    const idx = list.value.findIndex(i => i.id === id)
    if (idx >= 0) list.value[idx] = { ...current.value }
  } catch {}
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
.dialog-body { padding: 16px; line-height: 1.8; font-size: 14px; max-height: 60vh; overflow-y: auto; }
.handle-area { margin-top: 16px; padding: 12px; background: #f7f8fa; border-radius: 8px; }
.handle-title { font-size: 14px; font-weight: 600; margin-bottom: 8px; }
.reply-box { margin-top: 12px; padding: 8px; background: #fffbe6; border-radius: 4px; }
</style>

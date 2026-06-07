<template>
  <div class="page-container">
    <van-nav-bar title="报修投诉" fixed placeholder>
      <template #right>
        <van-icon name="plus" size="22" @click="$router.push('/complaint-create')" />
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
        <div v-for="item in list" :key="item.id" class="cp-card" @click="showDetail(item)">
          <div class="cp-top">
            <van-tag :type="typeColor(item.type)" size="small">{{ typeLabel(item.type) }}</van-tag>
            <van-tag :type="statusColor(item.status)" size="small" style="margin-left:6px">
              {{ statusLabel(item.status) }}
            </van-tag>
            <span class="cp-title">{{ item.title }}</span>
          </div>
          <div class="cp-meta">{{ formatTime(item.createTime) }}</div>
        </div>
      </van-list>
    </van-pull-refresh>

    <!-- 详情弹窗 -->
    <van-dialog v-model:show="showDialog" :title="current?.title" show-cancel-button="false">
      <div v-if="current" class="dialog-body">
        <p><strong>类型：</strong>{{ typeLabel(current.type) }}</p>
        <p><strong>状态：</strong>{{ statusLabel(current.status) }}</p>
        <p v-if="current.location"><strong>地点：</strong>{{ current.location }}</p>
        <div v-if="complaintImages.length > 0" class="dialog-images">
          <img v-for="(url, i) in complaintImages" :key="i" :src="url" class="dialog-img" />
        </div>
        <p><strong>描述：</strong>{{ current.description }}</p>
        <p v-if="current.reply"><strong>📩 处理回复：</strong>{{ current.reply }}</p>
        <p v-if="current.handlerName"><strong>处理人：</strong>{{ current.handlerName }}</p>
        <p v-if="current.rating"><strong>评分：</strong>{{ '⭐'.repeat(current.rating) }}</p>
        <p v-if="current.feedback"><strong>评价：</strong>{{ current.feedback }}</p>

        <!-- 评分（已完成 + 未评分） -->
        <div v-if="current.status === 'done' && !current.rating" class="rate-area">
          <div class="rate-title">给这次处理打分：</div>
          <van-rate v-model="rating" :count="5" size="24" />
          <van-field v-model="feedback" placeholder="补充评价（选填）" />
          <van-button type="primary" size="small" block @click="handleRate(current.id)">提交评价</van-button>
        </div>
      </div>
    </van-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getMyComplaints, rateComplaint } from '../api/complaint'
import { showToast } from 'vant'

const list = ref([])
const activeStatus = ref('')
const page = ref(1)
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)
const showDialog = ref(false)
const current = ref(null)
const rating = ref(0)
const feedback = ref('')

const complaintImages = computed(() => {
  try {
    const imgs = current.value?.images
    if (!imgs) return []
    const parsed = typeof imgs === 'string' ? JSON.parse(imgs) : imgs
    return Array.isArray(parsed) ? parsed : []
  } catch { return [] }
})

function formatTime(t) { return t ? new Date(t).toLocaleDateString('zh-CN') : '' }
function typeLabel(t) { return ({ repair: '报修', complaint: '投诉', suggest: '建议' })[t] || t }
function statusLabel(s) { return ({ pending: '待处理', processing: '处理中', done: '已完成', rejected: '已驳回' })[s] || s }
function typeColor(t) { return ({ repair: 'primary', complaint: 'danger', suggest: 'warning' })[t] || '' }
function statusColor(s) { return ({ pending: 'warning', processing: 'primary', done: 'success', rejected: 'danger' })[s] || '' }

function showDetail(item) { current.value = item; rating.value = 0; feedback.value = ''; showDialog.value = true }

async function fetchData() {
  loading.value = true
  try {
    const res = await getMyComplaints({
      page: page.value, size: 10,
      status: activeStatus.value || undefined
    })
    const records = res.data?.records || []
    if (page.value === 1) list.value = records
    else list.value.push(...records)
    finished.value = records.length < 10
  } catch {} finally { loading.value = false; refreshing.value = false }
}

async function handleRate(id) {
  if (rating.value === 0) return showToast('请先打分')
  try {
    await rateComplaint(id, rating.value, feedback.value)
    current.value.rating = rating.value
    current.value.feedback = feedback.value
    showToast('评价成功，感谢反馈！')
  } catch {}
}

function onLoad() {
  if (loading.value) return
  if (page.value > 1) { page.value++; fetchData() }
}
function onRefresh() { page.value = 1; finished.value = false; fetchData() }
function onTabChange() { page.value = 1; finished.value = false; fetchData() }
onMounted(() => fetchData())
</script>

<style scoped>
.cp-card { background: #fff; margin: 8px 16px; padding: 14px; border-radius: 10px; cursor: pointer; }
.cp-top { display: flex; align-items: center; gap: 6px; }
.cp-title { font-size: 15px; font-weight: 500; margin-left: 6px; }
.cp-meta { margin-top: 8px; font-size: 11px; color: #999; }
.dialog-body { padding: 16px; line-height: 1.8; font-size: 14px; max-height: 50vh; overflow-y: auto; }
.rate-area { margin-top: 16px; padding: 12px; background: #f7f8fa; border-radius: 8px; }
.rate-title { font-size: 14px; margin-bottom: 8px; }
.dialog-images { display: flex; flex-wrap: wrap; gap: 6px; margin: 8px 0; }
.dialog-img { width: 72px; height: 72px; object-fit: cover; border-radius: 4px; }
</style>

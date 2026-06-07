<template>
  <div class="page-container">
    <van-nav-bar title="工单详情" left-arrow @click-left="router.back()" fixed placeholder />

    <div v-if="item" class="detail-card">
      <div class="detail-header">
        <van-tag :type="typeColor(item.type)" size="large">{{ typeLabel(item.type) }}</van-tag>
        <van-tag :type="statusColor(item.status)" size="large" style="margin-left:8px">{{ statusLabel(item.status) }}</van-tag>
      </div>

      <h2>{{ item.title }}</h2>
      <div class="detail-meta">
        <div class="meta-row"><span class="label">提交人</span><span>{{ item.userName }}</span></div>
        <div class="meta-row"><span class="label">时间</span><span>{{ formatTime(item.createTime) }}</span></div>
        <div class="meta-row" v-if="item.location"><span class="label">地点</span><span>{{ item.location }}</span></div>
      </div>

      <div class="detail-section">
        <div class="section-title">问题描述</div>
        <p class="desc">{{ item.description }}</p>
      </div>

      <div class="detail-section" v-if="complaintImages.length > 0">
        <div class="section-title">现场照片</div>
        <div class="img-grid">
          <img v-for="(url, i) in complaintImages" :key="i" :src="url" class="detail-img" />
        </div>
      </div>

      <div class="detail-section" v-if="item.reply">
        <div class="section-title">处理回复</div>
        <p class="desc">{{ item.reply }}</p>
        <p v-if="item.handlerName" class="handler">处理人：{{ item.handlerName }}</p>
      </div>

      <div class="detail-section" v-if="item.rating">
        <div class="section-title">用户评价</div>
        <p>{{ '⭐'.repeat(item.rating) }}</p>
        <p v-if="item.feedback">{{ item.feedback }}</p>
      </div>

      <!-- 管理员处理 -->
      <div v-if="isAdmin && item.status !== 'done' && item.status !== 'rejected'" class="handle-section">
        <div class="section-title">处理工单</div>
        <van-radio-group v-model="handleStatus" direction="horizontal">
          <van-radio name="processing">🔧 处理中</van-radio>
          <van-radio name="done">✅ 已完成</van-radio>
          <van-radio name="rejected">❌ 已驳回</van-radio>
        </van-radio-group>
        <van-field v-model="handleReply" type="textarea" rows="3" placeholder="回复内容（选填）" />
        <van-button type="primary" round block @click="handleSubmit">确认处理</van-button>
      </div>

      <!-- 用户评价 -->
      <div v-if="!isAdmin && item.status === 'done' && !item.rating" class="rate-section">
        <div class="section-title">评价服务</div>
        <van-rate v-model="rating" :count="5" size="24" />
        <van-field v-model="feedback" placeholder="补充评价" />
        <van-button type="primary" round block @click="handleRate">提交评价</van-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getAdminComplaints, getMyComplaints, handleComplaint, rateComplaint } from '../api/complaint'
import { useUserStore } from '../stores/user'
import { showToast } from 'vant'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const item = ref(null)
const handleStatus = ref('processing')
const handleReply = ref('')
const rating = ref(0)
const feedback = ref('')

const complaintImages = computed(() => {
  try { const imgs = item.value?.images; if (!imgs) return []; const p = typeof imgs === "string" ? JSON.parse(imgs) : imgs; return Array.isArray(p) ? p : [] } catch { return [] }
})

const isAdmin = computed(() => userStore.userInfo?.role === 'admin')

function typeLabel(t) { return ({ repair: '报修', complaint: '投诉', suggest: '建议' })[t] || t }
function statusLabel(s) { return ({ pending: '待处理', processing: '处理中', done: '已完成', rejected: '已驳回' })[s] || s }
function typeColor(t) { return ({ repair: 'primary', complaint: 'danger', suggest: 'warning' })[t] || '' }
function statusColor(s) { return ({ pending: 'warning', processing: 'primary', done: 'success', rejected: 'danger' })[s] || '' }
function formatTime(t) { return t ? new Date(t).toLocaleString('zh-CN') : '' }

async function handleSubmit() {
  try { await handleComplaint(item.value.id, handleStatus.value, handleReply.value); showToast('处理成功'); router.back() } catch {}
}
async function handleRate() {
  if (rating.value === 0) return showToast('请打分')
  try { await rateComplaint(item.value.id, rating.value, feedback.value); showToast('评价成功'); router.back() } catch {}
}

onMounted(async () => {
  const id = Number(route.params.id)
  try {
    const api = isAdmin.value ? getAdminComplaints : getMyComplaints
    const res = await api({ page: 1, size: 100 })
    item.value = (res.data?.records || res.data || []).find(r => r.id === id)
    if (!item.value) {
      // try direct lookup
      const r2 = await getMyComplaints({ page: 1, size: 100 })
      item.value = (r2.data?.records || []).find(r => r.id === id)
    }
  } catch {}
})
</script>

<style scoped>
.detail-card { background: #fff; padding: 20px; min-height: 100vh; }
.detail-header { margin-bottom: 16px; }
.detail-card h2 { font-size: 20px; margin-bottom: 16px; }
.detail-meta { margin-bottom: 16px; }
.meta-row { display: flex; gap: 8px; font-size: 14px; margin-bottom: 6px; }
.label { color: #999; min-width: 50px; }
.detail-section { margin-bottom: 20px; padding-top: 16px; border-top: 1px solid #f0f0f0; }
.section-title { font-size: 15px; font-weight: 600; margin-bottom: 10px; }
.desc { font-size: 14px; line-height: 1.8; color: #555; }
.handler { font-size: 12px; color: #999; margin-top: 6px; }
.handle-section, .rate-section { margin-top: 20px; padding: 16px; background: #f7f8fa; border-radius: 10px; }
.img-grid { display: flex; flex-wrap: wrap; gap: 8px; margin-top: 8px; }
.detail-img { width: 100px; height: 100px; object-fit: cover; border-radius: 6px; }
</style>

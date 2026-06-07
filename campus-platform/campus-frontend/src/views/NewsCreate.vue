<template>
  <div class="page-container">
    <van-nav-bar :title="isEdit ? '编辑资讯' : '发布资讯'" left-arrow @click-left="router.back()" />

    <van-form @submit="handleSubmit">
      <van-cell-group inset>
        <van-field v-model="form.title" label="标题" placeholder="资讯标题" :rules="[{ required: true }]" />
        <van-field name="category" label="分类">
          <template #input>
            <van-radio-group v-model="form.category" direction="horizontal">
              <van-radio name="notice">通知</van-radio>
              <van-radio name="lecture">讲座</van-radio>
              <van-radio name="activity">活动</van-radio>
              <van-radio name="job">就业</van-radio>
            </van-radio-group>
          </template>
        </van-field>
        <van-field v-model="form.summary" label="摘要" placeholder="一句话概述" type="textarea" rows="2" />
      </van-cell-group>

      <div class="upload-cell">
        <div class="upload-label">封面图</div>
        <van-uploader v-model="fileList" :max-count="1"
          :max-size="5 * 1024 * 1024" accept="image/*"
          :preview-full-image="false"
          :before-read="beforeRead" :after-read="afterRead"
          :deletable="!uploading" />
      </div>

      <van-cell-group inset style="margin-top:8px">
        <van-field v-model="form.content" label="正文" placeholder="支持 HTML 格式" type="textarea" rows="8" :rules="[{ required: true }]" />
      </van-cell-group>

      <div style="margin:24px 16px">
        <van-button round block type="primary" native-type="submit" :loading="loading">{{ isEdit ? '保存修改' : '立即发布' }}</van-button>
      </div>
    </van-form>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getNewsDetail, publishNews, updateNews } from '../api/news'
import { uploadImages } from '../api/goods'
import { showToast } from 'vant'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const uploading = ref(false)
const fileList = ref([])
const coverUrl = ref('')

watch(fileList, (nl) => { if (nl.length === 0) coverUrl.value = '' })

const isEdit = computed(() => !!route.params.id)

const form = reactive({
  title: '', category: 'notice', summary: '', content: ''
})

function beforeRead(file) {
  if (file.type && !file.type.startsWith('image/')) { showToast('请选择图片'); return false }
  return true
}

async function afterRead(item) {
  uploading.value = true
  try {
    const file = item.file || item
    const res = await uploadImages([file])
    if (res.code === 200) { coverUrl.value = res.data[0]; item.status = 'done' }
    else { showToast(res.message || '上传失败'); fileList.value = [] }
  } catch { showToast('上传失败'); fileList.value = [] }
  finally { uploading.value = false }
}

onMounted(async () => {
  if (!isEdit.value) return
  try {
    const n = (await getNewsDetail(route.params.id)).data
    form.title = n.title || ''; form.category = n.category || 'notice'
    form.summary = n.summary || ''; form.content = n.content || ''
    if (n.coverImage) {
      coverUrl.value = n.coverImage
      fileList.value = [{ url: n.coverImage, status: 'done', message: '' }]
    }
  } catch {}
})

async function handleSubmit() {
  if (!form.title || !form.content) return showToast('请完善标题和正文')
  if (uploading.value) return showToast('图片上传中，请稍候')
  loading.value = true
  try {
    const data = { ...form, coverImage: coverUrl.value || form.coverImage || '' }
    if (isEdit.value) { await updateNews(route.params.id, data); showToast('修改成功') }
    else { await publishNews(data); showToast('发布成功') }
    setTimeout(() => router.back(), 800)
  } catch {} finally { loading.value = false }
}
</script>

<style scoped>
.upload-cell { background: #fff; padding: 12px 16px; margin: 8px 0; }
.upload-label { font-size: 14px; color: #323233; margin-bottom: 8px; }
</style>

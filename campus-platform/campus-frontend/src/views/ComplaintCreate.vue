<template>
  <div class="page-container">
    <van-nav-bar title="提交工单" left-arrow @click-left="$router.back()">
      <template #right>
        <van-button type="primary" size="small" :loading="loading" @click="handleSubmit">提交</van-button>
      </template>
    </van-nav-bar>

    <van-form class="form">
      <van-field name="type" label="类型">
        <template #input>
          <van-radio-group v-model="form.type" direction="horizontal">
            <van-radio name="repair">🔧 报修</van-radio>
            <van-radio name="complaint">📢 投诉</van-radio>
            <van-radio name="suggest">💡 建议</van-radio>
          </van-radio-group>
        </template>
      </van-field>
      <van-field v-model="form.title" label="标题" placeholder="一句话概括" :rules="[{ required: true }]" />
      <van-field v-model="form.location" label="地点" placeholder="报修地点/投诉对象" />

      <!-- 图片上传 -->
      <div class="upload-cell">
        <div class="upload-label">现场照片（选填）</div>
        <van-uploader v-model="fileList" :preview-full-image="false" :max-count="4"
          :max-size="5 * 1024 * 1024" accept="image/*"
          :before-read="beforeRead" :after-read="afterRead"
          :deletable="!uploading" />
        <p class="upload-tip">最多4张，用于说明问题</p>
      </div>

      <van-field v-model="form.description" type="textarea" rows="6"
        label="描述" placeholder="详细描述问题..." :rules="[{ required: true }]" />
    </van-form>
  </div>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { useRouter } from 'vue-router'
import { submitComplaint } from '../api/complaint'
import { uploadImages } from '../api/goods'
import { showToast, showSuccessToast } from 'vant'

const router = useRouter()
const loading = ref(false)
const uploading = ref(false)
const fileList = ref([])
const imageUrls = ref([])

watch(fileList, (nl) => {
  if (imageUrls.value.length > nl.length) {
    imageUrls.value = imageUrls.value.slice(0, nl.length)
  }
})

const form = reactive({ type: 'repair', title: '', location: '', description: '' })

function beforeRead(file) {
  if (file.type && !file.type.startsWith('image/')) {
    showToast('请选择图片文件')
    return false
  }
  return true
}

async function afterRead(item) {
  uploading.value = true
  try {
    const file = item.file || item
    const res = await uploadImages([file])
    if (res.code === 200) {
      imageUrls.value.push(...res.data)
      item.status = 'done'
    } else {
      showToast(res.message || '上传失败')
      item.status = 'failed'; item.message = '上传失败，点击重试'
    }
  } catch {
    showToast('上传失败')
    item.status = 'failed'; item.message = '上传失败，点击重试'
  } finally { uploading.value = false }
}

async function handleSubmit() {
  if (!form.title || !form.description) return showToast('请完善信息')
  if (uploading.value) return showToast('图片正在上传中，请稍候')
  loading.value = true
  try {
    const data = { ...form }
    if (imageUrls.value.length > 0) {
      data.images = JSON.stringify(imageUrls.value)
    }
    await submitComplaint(data)
    showSuccessToast('提交成功')
    setTimeout(() => router.back(), 800)
  } catch {} finally { loading.value = false }
}
</script>

<style scoped>
.form { margin-top: 0; }
.upload-cell { background: #fff; padding: 12px 16px; margin-bottom: 8px; }
.upload-label { font-size: 14px; color: #323233; margin-bottom: 8px; }
.upload-tip { font-size: 12px; color: #999; margin-top: 4px; }
</style>

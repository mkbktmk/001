<template>
  <div class="page-container">
    <van-nav-bar title="发帖" left-arrow @click-left="router.back()" fixed placeholder />

    <van-form @submit="handleSubmit">
      <van-cell-group inset>
        <van-field v-model="form.title" placeholder="标题（必填）" :rules="[{ required: true, message: '请填写标题' }]" />
      </van-cell-group>

      <van-cell-group inset style="margin-top:8px">
        <van-field name="board" label="板块">
          <template #input>
            <van-radio-group v-model="form.board" direction="horizontal">
              <van-radio name="study">学习</van-radio>
              <van-radio name="life">生活</van-radio>
              <van-radio name="tech">技术</van-radio>
              <van-radio name="job">求职</van-radio>
              <van-radio name="other">闲聊</van-radio>
            </van-radio-group>
          </template>
        </van-field>
      </van-cell-group>

      <div class="upload-cell">
        <div class="upload-label">图片（选填，最多6张）</div>
        <van-uploader v-model="fileList" :max-count="6"
          :max-size="5 * 1024 * 1024" accept="image/*"
          :preview-full-image="false"
          :before-read="beforeRead" :after-read="afterRead"
          :deletable="!uploading" />
      </div>

      <van-cell-group inset style="margin-top:8px">
        <van-field v-model="form.content" type="textarea" rows="6"
          placeholder="正文内容" :rules="[{ required: true, message: '请填写内容' }]" />
      </van-cell-group>

      <div style="margin: 24px 16px">
        <van-button round block type="primary" native-type="submit" :loading="loading">发布</van-button>
      </div>
    </van-form>
  </div>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { useRouter } from 'vue-router'
import { createPost } from '../api/post'
import { uploadImages } from '../api/goods'
import { showToast } from 'vant'

const router = useRouter()
const loading = ref(false)
const uploading = ref(false)
const fileList = ref([])
const imageUrls = ref([])

watch(fileList, (nl) => {
  if (imageUrls.value.length > nl.length) imageUrls.value = imageUrls.value.slice(0, nl.length)
})

const form = reactive({ title: '', content: '', board: 'life' })

function beforeRead(file) {
  if (file.type && !file.type.startsWith('image/')) { showToast('请选择图片文件'); return false }
  return true
}

async function afterRead(item) {
  uploading.value = true
  try {
    const file = item.file || item
    const res = await uploadImages([file])
    if (res.code === 200) { imageUrls.value.push(...res.data); item.status = 'done' }
    else { showToast(res.message || '上传失败'); fileList.value = fileList.value.filter(f => f !== item) }
  } catch { showToast('上传失败'); fileList.value = fileList.value.filter(f => f !== item) }
  finally { uploading.value = false }
}

async function handleSubmit() {
  if (uploading.value) return showToast('图片上传中，请稍候')
  loading.value = true
  try {
    const data = { ...form }
    if (imageUrls.value.length > 0) data.images = JSON.stringify(imageUrls.value)
    await createPost(data)
    showToast('发布成功')
    setTimeout(() => router.back(), 800)
  } catch (e) { showToast(e?.message || '发布失败，请重试') }
  finally { loading.value = false }
}
</script>

<style scoped>
.upload-cell { background: #fff; padding: 12px 16px; margin: 8px 0; }
.upload-label { font-size: 14px; color: #323233; margin-bottom: 8px; }
</style>

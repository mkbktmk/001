<template>
  <div class="page-container">
    <van-nav-bar :title="isEdit ? '编辑启事' : '发布启事'" left-arrow @click-left="router.back()">
      <template #right>
        <van-button type="primary" size="small" :loading="loading" @click="handleSubmit">{{ isEdit ? '保存' : '发布' }}</van-button>
      </template>
    </van-nav-bar>

    <van-form class="form">
      <van-field name="type" label="类型">
        <template #input>
          <van-radio-group v-model="form.type" direction="horizontal">
            <van-radio name="lost">🔍 寻物</van-radio>
            <van-radio name="found">📦 招领</van-radio>
          </van-radio-group>
        </template>
      </van-field>
      <van-field v-model="form.itemName" label="物品名称" placeholder="如：黑色双肩包" :rules="[{ required: true }]" />
      <van-field name="category" label="分类">
        <template #input>
          <van-radio-group v-model="form.category" direction="horizontal">
            <van-radio name="digital">数码</van-radio>
            <van-radio name="card">证件</van-radio>
            <van-radio name="clothing">衣物</van-radio>
            <van-radio name="key">钥匙</van-radio>
            <van-radio name="book">书籍</van-radio>
            <van-radio name="other">其他</van-radio>
          </van-radio-group>
        </template>
      </van-field>

      <!-- 图片上传 -->
      <div class="upload-cell">
        <div class="upload-label">物品照片（选填）</div>
        <van-uploader v-model="fileList" :preview-full-image="false" :max-count="4"
          :max-size="5 * 1024 * 1024" accept="image/*"
          :before-read="beforeRead" :after-read="afterRead"
          :deletable="!uploading" />
        <p class="upload-tip">最多4张，有助于更快辨认物品</p>
      </div>

      <van-field v-model="form.location" label="地点" placeholder="丢失/拾取地点" />
      <van-field v-model="form.contact" label="联系方式" placeholder="电话/微信/QQ" />
      <van-field v-model="form.description" type="textarea" rows="5"
        label="描述" placeholder="详细描述物品特征..." :rules="[{ required: true }]" />
    </van-form>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { createLostFound, updateLostFound, getLostFoundDetail } from '../api/lostfound'
import { uploadImages } from '../api/goods'
import { showToast, showSuccessToast } from 'vant'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const uploading = ref(false)
const fileList = ref([])
const imageUrls = ref([])

watch(fileList, (nl) => {
  if (imageUrls.value.length > nl.length) imageUrls.value = imageUrls.value.slice(0, nl.length)
})

const isEdit = computed(() => !!route.params.id)

const form = reactive({
  type: 'lost', itemName: '', category: 'other',
  location: '', contact: '', description: ''
})

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
    else { showToast(res.message || '上传失败'); item.status = 'failed'; item.message = '上传失败，点击重试' }
  } catch { showToast('上传失败'); item.status = 'failed'; item.message = '上传失败，点击重试' }
  finally { uploading.value = false }
}

onMounted(async () => {
  if (!isEdit.value) return
  try {
    const d = (await getLostFoundDetail(route.params.id)).data
    form.type = d.type || 'lost'; form.itemName = d.itemName || ''
    form.category = d.category || 'other'; form.location = d.location || ''
    form.contact = d.contact || ''; form.description = d.description || ''
    if (d.images) {
      const urls = typeof d.images === 'string' ? JSON.parse(d.images) : d.images
      imageUrls.value = urls
      fileList.value = urls.map((url, i) => ({ url, status: 'done', message: '' }))
    }
  } catch {}
})

async function handleSubmit() {
  if (!form.itemName || !form.description) return showToast('请完善信息')
  if (uploading.value) return showToast('图片上传中，请稍候')
  loading.value = true
  try {
    const data = { ...form }
    if (imageUrls.value.length > 0) data.images = JSON.stringify(imageUrls.value)
    if (isEdit.value) await updateLostFound(route.params.id, data)
    else await createLostFound(data)
    showToast(isEdit.value ? '修改成功' : '发布成功')
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

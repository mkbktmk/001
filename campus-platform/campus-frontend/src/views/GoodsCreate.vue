<template>
  <div class="page-container">
    <van-nav-bar :title="isEdit ? '编辑商品' : '发布商品'" left-arrow @click-left="$router.back()">
      <template #right>
        <van-button type="primary" size="small" :loading="loading" @click="handleSubmit">{{ isEdit ? '保存' : '发布' }}</van-button>
      </template>
    </van-nav-bar>

    <van-form class="form">
      <van-field v-model="form.title" label="标题" placeholder="商品名称" :rules="[{ required: true }]" />
      <van-field v-model="form.price" label="价格(元)" type="number" placeholder="0.00" :rules="[{ required: true }]" />
      <van-field v-model="form.originalPrice" label="原价(元)" type="number" placeholder="选填" />

      <!-- 图片上传 -->
      <div class="upload-cell">
        <div class="upload-label">商品图片</div>
        <van-uploader
          v-model="fileList"
          :preview-full-image="false"
          :max-count="6"
          :max-size="5 * 1024 * 1024"
          accept="image/*"
          :before-read="beforeRead"
          :after-read="afterRead"
          :deletable="!uploading"
        />
        <p class="upload-tip">最多6张，每张不超过5MB（上传成功后可发布）</p>
      </div>

      <van-field name="category" label="分类">
        <template #input>
          <van-radio-group v-model="form.category" direction="horizontal">
            <van-radio name="textbook">教材</van-radio>
            <van-radio name="digital">数码</van-radio>
            <van-radio name="living">生活</van-radio>
            <van-radio name="clothing">服饰</van-radio>
            <van-radio name="other">其他</van-radio>
          </van-radio-group>
        </template>
      </van-field>
      <van-field name="goodsCondition" label="成色">
        <template #input>
          <van-radio-group v-model="form.goodsCondition" direction="horizontal">
            <van-radio name="new">全新</van-radio>
            <van-radio name="like_new">九成新</van-radio>
            <van-radio name="good">良好</van-radio>
            <van-radio name="fair">一般</van-radio>
          </van-radio-group>
        </template>
      </van-field>
      <van-field v-model="form.contact" label="联系方式" placeholder="微信/QQ/电话" />
      <van-field v-model="form.description" type="textarea" rows="5"
        label="描述" placeholder="详细描述商品情况..." :rules="[{ required: true }]" />
    </van-form>
  </div>
</template>

<script setup>
import { ref, reactive, watch, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { createGoods, updateGoods, uploadImages, getGoodsDetail } from '../api/goods'
import { showToast } from 'vant'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const uploading = ref(false)
const fileList = ref([])
const imageUrls = ref([])

const isEdit = computed(() => !!route.params.id)
const editId = computed(() => route.params.id)

// 用户在 Uploader 中删除图片时，同步清理 imageUrls
watch(fileList, (newList) => {
  if (imageUrls.value.length > newList.length) {
    imageUrls.value = imageUrls.value.slice(0, newList.length)
  }
})

const form = reactive({
  title: '', price: '', originalPrice: '',
  category: 'other', goodsCondition: 'good',
  contact: '', description: ''
})

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
      fileList.value = fileList.value.filter(f => f !== item)
    }
  } catch {
    showToast('上传失败')
    fileList.value = fileList.value.filter(f => f !== item)
  } finally {
    uploading.value = false
  }
}

async function handleSubmit() {
  if (!form.title || !form.price) return showToast('请完善信息')
  if (uploading.value) return showToast('图片正在上传中，请稍候')
  loading.value = true
  try {
    const data = {
      ...form,
      price: parseFloat(form.price),
      originalPrice: form.originalPrice ? parseFloat(form.originalPrice) : null,
      images: imageUrls.value.length > 0 ? JSON.stringify(imageUrls.value) : null
    }
    if (isEdit.value) {
      await updateGoods(editId.value, data)
      showToast('修改成功')
    } else {
      await createGoods(data)
      showToast('发布成功')
    }
    setTimeout(() => router.back(), 800)
  } catch {} finally { loading.value = false }
}

// 编辑模式：加载已有商品数据
onMounted(async () => {
  if (!isEdit.value) return
  try {
    const res = await getGoodsDetail(editId.value)
    const g = res.data
    form.title = g.title
    form.price = String(g.price)
    form.originalPrice = g.originalPrice ? String(g.originalPrice) : ''
    form.category = g.category
    form.goodsCondition = g.goodsCondition
    form.contact = g.contact || ''
    form.description = g.description || ''
    // 已有图片
    if (g.images) {
      const urls = typeof g.images === 'string' ? JSON.parse(g.images) : g.images
      imageUrls.value = urls
      // 在 fileList 中显示已有图片
      fileList.value = urls.map((url, i) => ({ url, status: 'done', message: '' }))
    }
  } catch {}
})
</script>

<style scoped>
.form { margin-top: 8px; }
.upload-cell {
  background: #fff;
  padding: 12px 16px;
}
.upload-label {
  font-size: 14px;
  color: #323233;
  margin-bottom: 8px;
}
.upload-tip {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}
</style>

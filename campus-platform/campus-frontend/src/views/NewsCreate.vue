<template>
  <div class="page-container">
    <van-nav-bar :title="isEdit ? '编辑资讯' : '发布资讯'" left-arrow @click-left="$router.back()">
      <template #right>
        <van-button type="primary" size="small" :loading="loading" @click="handleSubmit">{{ isEdit ? '保存' : '发布' }}</van-button>
      </template>
    </van-nav-bar>

    <van-form class="form">
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
        <van-field v-model="form.summary" label="摘要" placeholder="一句话概述（选填）" type="textarea" rows="2" />
        <van-field v-model="form.coverImage" label="封面图URL" placeholder="图片链接（选填）" />
        <van-field v-model="form.content" label="正文" placeholder="支持 HTML 格式" type="textarea" rows="8" :rules="[{ required: true }]" />
      </van-cell-group>

      <div class="btn">
        <van-button round block type="primary" native-type="submit" :loading="loading">{{ isEdit ? '保存修改' : '立即发布' }}</van-button>
      </div>
    </van-form>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getNewsDetail } from '../api/news'
import { publishNews, updateNews } from '../api/news'
import { showToast } from 'vant'

const route = useRoute()
const router = useRouter()
const loading = ref(false)

const isEdit = computed(() => !!route.params.id)

const form = reactive({
  title: '', category: 'notice', summary: '',
  coverImage: '', content: ''
})

onMounted(async () => {
  if (!isEdit.value) return
  try {
    const res = await getNewsDetail(route.params.id)
    const n = res.data
    form.title = n.title || ''
    form.category = n.category || 'notice'
    form.summary = n.summary || ''
    form.coverImage = n.coverImage || ''
    form.content = n.content || ''
  } catch {}
})

async function handleSubmit() {
  if (!form.title || !form.content) return showToast('请完善标题和正文')
  loading.value = true
  try {
    if (isEdit.value) {
      await updateNews(route.params.id, form)
      showToast('修改成功')
    } else {
      await publishNews(form)
      showToast('发布成功')
    }
    setTimeout(() => router.back(), 800)
  } catch {} finally { loading.value = false }
}
</script>

<style scoped>
.form { margin-top: 8px; }
.btn { margin: 24px 16px; }
</style>

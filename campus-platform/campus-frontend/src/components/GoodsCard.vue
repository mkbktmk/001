<template>
  <div class="goods-card" @click="router.push(`/goods/${goods.id}`)">
    <div class="goods-img">
      <img v-if="coverImage" :src="coverImage" class="cover-img" alt="" />
      <van-icon v-else name="photo-o" size="40" color="#ddd" />
    </div>
    <div class="goods-info">
      <h4>{{ goods.title }}</h4>
      <p class="goods-desc" v-if="goods.description">{{ goods.description.slice(0, 40) }}{{ goods.description.length > 40 ? '...' : '' }}</p>
      <div class="goods-bottom">
        <span class="goods-price">¥{{ goods.price }}</span>
        <span v-if="goods.originalPrice" class="goods-original">¥{{ goods.originalPrice }}</span>
        <span class="goods-tag">{{ conditionMap[goods.goodsCondition] || goods.goodsCondition }}</span>
      </div>
      <div class="goods-meta">
        <span>{{ goods.sellerName }}</span>
        <span class="meta-right">
          <van-icon name="star-o" size="12" /> {{ goods.favCount || 0 }}
          <van-icon name="eye-o" size="12" style="margin-left:8px" /> {{ goods.viewCount || 0 }}
        </span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const props = defineProps({ goods: Object })

const coverImage = computed(() => {
  try {
    const imgs = typeof props.goods.images === 'string'
      ? JSON.parse(props.goods.images)
      : props.goods.images
    return Array.isArray(imgs) && imgs.length > 0 ? imgs[0] : null
  } catch { return null }
})

const conditionMap = {
  new: '全新', like_new: '几乎全新', good: '良好', fair: '一般'
}
</script>

<style scoped>
.goods-card {
  display: flex;
  background: #fff;
  margin: 8px 12px;
  padding: 12px;
  border-radius: 12px;
  cursor: pointer;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04);
  transition: transform .15s;
}
.goods-card:active { transform: scale(0.98); }
.goods-img {
  width: 100px;
  height: 100px;
  border-radius: 8px;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  overflow: hidden;
}
.cover-img { width: 100%; height: 100%; object-fit: cover; }
.goods-info {
  flex: 1;
  padding-left: 12px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.goods-info h4 {
  font-size: 15px;
  font-weight: 500;
  line-height: 1.4;
  margin-bottom: 4px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.goods-desc {
  font-size: 12px;
  color: #999;
  margin-bottom: 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.goods-bottom {
  display: flex;
  align-items: baseline;
  gap: 6px;
  margin-bottom: 4px;
}
.goods-price { font-size: 19px; font-weight: 700; color: #ee0a24; }
.goods-original { font-size: 12px; color: #ccc; text-decoration: line-through; }
.goods-tag {
  font-size: 10px;
  color: #1989fa;
  background: #e8f4ff;
  padding: 1px 6px;
  border-radius: 3px;
}
.goods-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 11px;
  color: #b0b0b0;
  margin-top: auto;
}
.meta-right { display: flex; align-items: center; gap: 2px; }
</style>

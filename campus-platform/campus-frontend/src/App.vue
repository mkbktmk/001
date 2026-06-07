<template>
  <div id="app-container">
    <router-view />
    <van-tabbar v-if="showTabbar" route active-color="#1989fa">
      <van-tabbar-item to="/home" icon="home-o">首页</van-tabbar-item>
      <van-tabbar-item to="/forum" icon="comment-o">论坛</van-tabbar-item>
      <van-tabbar-item to="/goods" icon="shopping-cart-o">二手</van-tabbar-item>
      <van-tabbar-item to="/profile" icon="user-o">我的</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { getMyInfo } from './api/auth'

const route = useRoute()
const hideTabbar = ['Login', 'Register', 'Chat', 'Messages', 'GoodsCreate', 'GoodsEdit', 'GoodsDetail',
  'ForumCreate', 'ForumDetail', 'NewsCreate', 'NewsEdit', 'NewsDetail',
  'LostFoundCreate', 'LostFoundEdit', 'LostFoundDetail', 'ComplaintCreate',
  'ProfileEdit', 'MyFavorites', 'MyGoods', 'MyPosts', 'MyLostFound',
  'AdminComplaints', 'Notifications', 'OrderHistory']
const showTabbar = computed(() => !hideTabbar.includes(route.name))

// 心跳检测：每30秒检查 session 是否有效
let heartbeatTimer = null
onMounted(() => {
  heartbeatTimer = setInterval(async () => {
    if (!localStorage.getItem('token')) return
    try {
      await getMyInfo()
    } catch {
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      window.location.hash = '#/login'
    }
  }, 5000)
})
onUnmounted(() => clearInterval(heartbeatTimer))
</script>

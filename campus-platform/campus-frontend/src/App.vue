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
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

// 子页面隐藏底部导航栏
const hideTabbar = ['Login', 'Register', 'Chat', 'Messages', 'GoodsCreate', 'GoodsEdit', 'GoodsDetail',
  'ForumCreate', 'ForumDetail', 'NewsCreate', 'NewsEdit', 'NewsDetail',
  'LostFoundCreate', 'LostFoundEdit', 'LostFoundDetail', 'ComplaintCreate',
  'ProfileEdit', 'MyFavorites', 'MyGoods', 'MyPosts', 'MyLostFound',
  'AdminComplaints', 'Notifications', 'OrderHistory']
const showTabbar = computed(() => !hideTabbar.includes(route.name))
</script>

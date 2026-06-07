<template>
  <div class="page-container">
    <!-- 登录 / 未登录 -->
    <div v-if="!userStore.isLoggedIn()" class="not-login">
      <van-icon name="user-circle-o" size="64" color="#ccc" />
      <p>登录后查看更多</p>
      <van-button type="primary" round to="/login">去登录</van-button>
    </div>

    <template v-else>
      <!-- 用户信息 -->
      <div class="user-card" @click="$router.push('/profile-edit')">
        <van-image v-if="userStore.userInfo?.avatarUrl" round width="64" height="64" :src="userStore.userInfo.avatarUrl" fit="cover">
          <template #loading><van-icon name="user-circle-o" size="64" /></template>
          <template #error><van-icon name="user-circle-o" size="64" color="#ccc" /></template>
        </van-image>
        <van-icon v-else name="user-circle-o" size="64" color="rgba(255,255,255,0.6)" />
        <div class="user-info">
          <h3>{{ userStore.userInfo?.nickname }}</h3>
          <p>{{ userStore.userInfo?.college }} {{ userStore.userInfo?.major }}</p>
          <van-tag type="primary" size="small">{{ roleLabel(userStore.userInfo?.role) }}</van-tag>
        </div>
      </div>

      <!-- 快捷入口 -->
      <van-cell-group inset style="margin-top:12px">
        <van-cell title="编辑资料" icon="setting-o" is-link to="/profile-edit" />
        <van-cell title="我的帖子" icon="comment-o" is-link to="/my-posts" />
        <van-cell title="我的启事" icon="search" is-link to="/my-lostfound" />
        <van-cell title="我的工单" icon="records" is-link to="/complaint" />
        <van-cell title="我收藏的" icon="star-o" is-link to="/my-favorites" />
        <van-cell title="我的发布" icon="shop-o" is-link to="/my-goods" />
        <van-cell title="购买记录" icon="balance-list-o" is-link to="/my-orders" />
      </van-cell-group>


      <!-- 设置 -->
      <van-cell-group inset style="margin-top:12px">
        <van-cell title="退出登录" icon="revoke" @click="handleLogout" />
      </van-cell-group>
    </template>
  </div>
</template>

<script setup>
import { useUserStore } from '../stores/user'
import { showConfirmDialog } from 'vant'

const userStore = useUserStore()

function roleLabel(r) {
  return ({ student: '学生', teacher: '教师', admin: '管理员' })[r] || r
}

async function handleLogout() {
  try {
    await showConfirmDialog({
      title: '退出登录',
      message: '确定要退出当前账号吗？',
      confirmButtonText: '退出',
      cancelButtonText: '取消'
    })
    userStore.logout()
  } catch {}
}
</script>

<style scoped>
.not-login {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  gap: 16px;
}
.not-login p { font-size: 14px; color: #999; }
.user-card {
  display: flex;
  align-items: center;
  gap: 16px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  padding: 24px 20px;
  color: #fff;
}
.user-info h3 { font-size: 20px; margin-bottom: 4px; }
.user-info p { font-size: 13px; opacity: 0.85; margin-bottom: 6px; }
</style>

import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  { path: '/',             redirect: '/home' },
  { path: '/login',        name: 'Login',        component: () => import('../views/Login.vue') },
  { path: '/register',     name: 'Register',     component: () => import('../views/Register.vue') },
  { path: '/home',         name: 'Home',         component: () => import('../views/Home.vue') },
  { path: '/forum',        name: 'Forum',        component: () => import('../views/Forum.vue') },
  { path: '/forum/:id',    name: 'ForumDetail',  component: () => import('../views/ForumDetail.vue') },
  { path: '/forum-create', name: 'ForumCreate',  component: () => import('../views/ForumCreate.vue') },
  { path: '/news',         name: 'News',         component: () => import('../views/News.vue') },
  { path: '/news/:id',     name: 'NewsDetail',   component: () => import('../views/NewsDetail.vue') },
  { path: '/news-create',  name: 'NewsCreate',   component: () => import('../views/NewsCreate.vue') },
  { path: '/news-edit/:id',name: 'NewsEdit',     component: () => import('../views/NewsCreate.vue') },
  { path: '/lostfound',    name: 'LostFound',    component: () => import('../views/LostFound.vue') },
  { path: '/lostfound-detail/:id', name: 'LostFoundDetail', component: () => import('../views/LostFoundDetail.vue') },
  { path: '/lostfound-create', name: 'LostFoundCreate', component: () => import('../views/LostFoundCreate.vue') },
  { path: '/lostfound-edit/:id', name: 'LostFoundEdit', component: () => import('../views/LostFoundCreate.vue') },
  { path: '/messages',     name: 'Messages',    component: () => import('../views/Messages.vue') },
  { path: '/goods',        name: 'Goods',        component: () => import('../views/Goods.vue') },
  { path: '/goods/:id',    name: 'GoodsDetail',  component: () => import('../views/GoodsDetail.vue') },
  { path: '/goods-create', name: 'GoodsCreate',  component: () => import('../views/GoodsCreate.vue') },
  { path: '/goods-edit/:id', name: 'GoodsEdit',   component: () => import('../views/GoodsCreate.vue') },
  { path: '/my-favorites',  name: 'MyFavorites',  component: () => import('../views/MyFavorites.vue') },
  { path: '/my-goods',      name: 'MyGoods',      component: () => import('../views/MyGoods.vue') },
  { path: '/my-posts',      name: 'MyPosts',      component: () => import('../views/MyPosts.vue') },
  { path: '/my-fav-posts',  name: 'MyFavPosts',  component: () => import('../views/MyFavPosts.vue') },
  { path: '/my-lostfound',  name: 'MyLostFound',  component: () => import('../views/MyLostFound.vue') },
  { path: '/my-orders',     name: 'OrderHistory', component: () => import('../views/OrderHistory.vue') },
  { path: '/complaint',    name: 'Complaint',    component: () => import('../views/Complaint.vue') },
  { path: '/chat',           name: 'Chat',          component: () => import('../views/Chat.vue') },
  { path: '/complaint-create', name: 'ComplaintCreate', component: () => import('../views/ComplaintCreate.vue') },
  { path: '/complaint-detail/:id', name: 'ComplaintDetail', component: () => import('../views/ComplaintDetail.vue') },
  { path: '/admin/complaints', name: 'AdminComplaints', component: () => import('../views/AdminComplaints.vue') },
  { path: '/notifications', name: 'Notifications', component: () => import('../views/Notifications.vue') },
  { path: '/profile',      name: 'Profile',      component: () => import('../views/Profile.vue') },
  { path: '/profile-edit', name: 'ProfileEdit',  component: () => import('../views/ProfileEdit.vue') },
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router

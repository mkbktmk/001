import request from './request'

// 帖子列表
export function getPostList(params) {
  return request.get('/post/list', { params })
}

// 帖子详情
export function getPostDetail(id) {
  return request.get(`/post/detail/${id}`)
}

// 发布帖子
export function createPost(data) {
  return request.post('/post', data)
}

// 删除帖子
export function deletePost(id) {
  return request.delete(`/post/${id}`)
}

// 点赞/取消
export function toggleLike(id) {
  return request.post(`/post/like/${id}`)
}

// 是否已点赞
export function isLiked(id) {
  return request.get(`/post/like/${id}/status`)
}

// 收藏/取消
export function toggleFavorite(id) {
  return request.post(`/post/favorite/${id}`)
}

// 是否已收藏
export function isFavorited(id) {
  return request.get(`/post/favorite/${id}/status`)
}

// 回复列表
export function getComments(postId) {
  return request.get(`/comment/list/${postId}`)
}

// 发表回复
export function createComment(params) {
  return request.post('/comment', null, { params })
}

// 删除回复
export function deleteComment(id) {
  return request.delete(`/comment/${id}`)
}

// 我的帖子
export function getMyPosts(params) {
  return request.get('/post/my', { params })
}
export function getMyFavorites() { return request.get('/post/favorites') }

import request from '@/utils/request'

export function getPostPage(params) {
  return request({
    url: '/api/posts/page',
    method: 'get',
    params
  })
}

export function getAllPosts() {
  return request({
    url: '/api/posts/list',
    method: 'get'
  })
}

export function getPostById(id) {
  return request({
    url: `/api/posts/${id}`,
    method: 'get'
  })
}

export function createPost(data) {
  return request({
    url: '/api/posts',
    method: 'post',
    data
  })
}

export function updatePost(id, data) {
  return request({
    url: `/api/posts/${id}`,
    method: 'put',
    data
  })
}

export function deletePost(id) {
  return request({
    url: `/api/posts/${id}`,
    method: 'delete'
  })
}

export function deletePosts(data) {
  return request({
    url: '/api/posts/batch',
    method: 'delete',
    data
  })
}

export function checkPostHasUsers(id) {
  return request({
    url: `/api/posts/${id}/has-users`,
    method: 'get'
  })
}

export function exportPosts(params) {
  return request({
    url: '/api/posts/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

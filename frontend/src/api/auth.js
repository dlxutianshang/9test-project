import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/api/auth/login',
    method: 'post',
    data
  })
}

export function register(data) {
  return request({
    url: '/api/auth/register',
    method: 'post',
    data
  })
}

export function logout() {
  return request({
    url: '/api/auth/logout',
    method: 'post'
  })
}

export function getCurrentUserInfo() {
  return request({
    url: '/api/auth/me',
    method: 'get'
  })
}

export function changePassword(data) {
  return request({
    url: '/api/auth/change-password',
    method: 'post',
    data
  })
}

export function forgotPassword(data) {
  return request({
    url: '/api/auth/forgot-password',
    method: 'post',
    data
  })
}

export function resetPassword(params) {
  return request({
    url: '/api/auth/reset-password',
    method: 'post',
    params
  })
}

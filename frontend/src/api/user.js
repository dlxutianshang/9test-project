import request from '@/utils/request'

export function getUserPage(params) {
  return request({
    url: '/api/users/page',
    method: 'get',
    params
  })
}

export function getUserById(id) {
  return request({
    url: `/api/users/${id}`,
    method: 'get'
  })
}

export function createUser(data) {
  return request({
    url: '/api/users',
    method: 'post',
    data
  })
}

export function updateUser(id, data) {
  return request({
    url: `/api/users/${id}`,
    method: 'put',
    data
  })
}

export function deleteUser(id) {
  return request({
    url: `/api/users/${id}`,
    method: 'delete'
  })
}

export function updateUserStatus(id, status) {
  return request({
    url: `/api/users/${id}/status`,
    method: 'put',
    params: { status }
  })
}

export function resetUserPassword(id, newPassword) {
  return request({
    url: `/api/users/${id}/reset-password`,
    method: 'put',
    params: { newPassword }
  })
}

export function getUserRoles(id) {
  return request({
    url: `/api/users/${id}/roles`,
    method: 'get'
  })
}

export function getUserPermissions(id) {
  return request({
    url: `/api/users/${id}/permissions`,
    method: 'get'
  })
}

export function assignRoles(data) {
  return request({
    url: '/api/users/assign-roles',
    method: 'post',
    data
  })
}

export function exportUsers(params) {
  return request({
    url: '/api/users/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

export function importUsers(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/api/users/import',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

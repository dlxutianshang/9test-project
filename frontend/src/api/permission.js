import request from '@/utils/request'

export function getPermissionTree() {
  return request({
    url: '/api/permissions/tree',
    method: 'get'
  })
}

export function getAllPermissions() {
  return request({
    url: '/api/permissions/list',
    method: 'get'
  })
}

export function getPermissionById(id) {
  return request({
    url: `/api/permissions/${id}`,
    method: 'get'
  })
}

export function createPermission(data) {
  return request({
    url: '/api/permissions',
    method: 'post',
    data
  })
}

export function updatePermission(id, data) {
  return request({
    url: `/api/permissions/${id}`,
    method: 'put',
    data
  })
}

export function deletePermission(id) {
  return request({
    url: `/api/permissions/${id}`,
    method: 'delete'
  })
}

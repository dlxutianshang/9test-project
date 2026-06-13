import request from '@/utils/request'

export function getRolePage(params) {
  return request({
    url: '/api/roles/page',
    method: 'get',
    params
  })
}

export function getAllRoles() {
  return request({
    url: '/api/roles/list',
    method: 'get'
  })
}

export function getRoleById(id) {
  return request({
    url: `/api/roles/${id}`,
    method: 'get'
  })
}

export function createRole(data) {
  return request({
    url: '/api/roles',
    method: 'post',
    data
  })
}

export function updateRole(id, data) {
  return request({
    url: `/api/roles/${id}`,
    method: 'put',
    data
  })
}

export function deleteRole(id) {
  return request({
    url: `/api/roles/${id}`,
    method: 'delete'
  })
}

export function getRolePermissions(id) {
  return request({
    url: `/api/roles/${id}/permissions`,
    method: 'get'
  })
}

export function assignPermissions(data) {
  return request({
    url: '/api/roles/assign-permissions',
    method: 'post',
    data
  })
}

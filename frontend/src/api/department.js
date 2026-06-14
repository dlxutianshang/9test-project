import request from '@/utils/request'

export function getDepartmentTree(params) {
  return request({
    url: '/api/departments/tree',
    method: 'get',
    params
  })
}

export function getAllDepartments() {
  return request({
    url: '/api/departments/list',
    method: 'get'
  })
}

export function getDepartmentOptions() {
  return request({
    url: '/api/departments/options',
    method: 'get'
  })
}

export function getDepartmentById(id) {
  return request({
    url: `/api/departments/${id}`,
    method: 'get'
  })
}

export function createDepartment(data) {
  return request({
    url: '/api/departments',
    method: 'post',
    data
  })
}

export function updateDepartment(id, data) {
  return request({
    url: `/api/departments/${id}`,
    method: 'put',
    data
  })
}

export function deleteDepartment(id) {
  return request({
    url: `/api/departments/${id}`,
    method: 'delete'
  })
}

export function batchUpdateSortOrder(data) {
  return request({
    url: '/api/departments/batch-sort',
    method: 'put',
    data
  })
}

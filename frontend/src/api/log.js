import request from '@/utils/request'

export function getLogPage(params) {
  return request({
    url: '/api/logs/page',
    method: 'get',
    params
  })
}

export function getLogById(id) {
  return request({
    url: `/api/logs/${id}`,
    method: 'get'
  })
}

export function deleteLog(id) {
  return request({
    url: `/api/logs/${id}`,
    method: 'delete'
  })
}

export function cleanOldLogs(beforeDate) {
  return request({
    url: '/api/logs/clean',
    method: 'delete',
    params: { beforeDate }
  })
}

import request from '@/utils/request'

export function getAnnouncementPage(params) {
  return request({
    url: '/api/announcements/page',
    method: 'get',
    params
  })
}

export function getAnnouncementById(id) {
  return request({
    url: `/api/announcements/${id}`,
    method: 'get'
  })
}

export function getReadUsers(id) {
  return request({
    url: `/api/announcements/${id}/read-users`,
    method: 'get'
  })
}

export function createAnnouncement(data) {
  return request({
    url: '/api/announcements',
    method: 'post',
    data
  })
}

export function updateAnnouncement(id, data) {
  return request({
    url: `/api/announcements/${id}`,
    method: 'put',
    data
  })
}

export function deleteAnnouncement(id) {
  return request({
    url: `/api/announcements/${id}`,
    method: 'delete'
  })
}

export function deleteAnnouncements(data) {
  return request({
    url: '/api/announcements/batch',
    method: 'delete',
    data
  })
}

export function checkHasReadUsers(id) {
  return request({
    url: `/api/announcements/${id}/has-read-users`,
    method: 'get'
  })
}

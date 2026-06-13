import { defineStore } from 'pinia'
import { login, logout, getCurrentUserInfo } from '@/api/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo') || 'null'),
    roles: JSON.parse(localStorage.getItem('roles') || '[]'),
    permissions: JSON.parse(localStorage.getItem('permissions') || '[]')
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    username: (state) => state.userInfo?.nickname || state.userInfo?.username || '',
    hasPermission: (state) => (permission) => {
      return state.permissions.some(p => p.permissionCode === permission) ||
             state.roles.some(r => r.roleCode === 'ROLE_ADMIN')
    }
  },

  actions: {
    async login(loginData) {
      const res = await login(loginData)
      const { token, user, roles, permissions } = res.data
      this.token = token
      this.userInfo = user
      this.roles = roles || []
      this.permissions = permissions || []
      localStorage.setItem('token', token)
      localStorage.setItem('userInfo', JSON.stringify(user))
      localStorage.setItem('roles', JSON.stringify(roles || []))
      localStorage.setItem('permissions', JSON.stringify(permissions || []))
      return res.data
    },

    async getUserInfo() {
      const res = await getCurrentUserInfo()
      const { user, roles, permissions } = res.data
      this.userInfo = user
      this.roles = roles || []
      this.permissions = permissions || []
      localStorage.setItem('userInfo', JSON.stringify(user))
      localStorage.setItem('roles', JSON.stringify(roles || []))
      localStorage.setItem('permissions', JSON.stringify(permissions || []))
      return res.data
    },

    async logout() {
      try {
        await logout()
      } catch (e) {
      } finally {
        this.token = ''
        this.userInfo = null
        this.roles = []
        this.permissions = []
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        localStorage.removeItem('roles')
        localStorage.removeItem('permissions')
      }
    },

    checkPermission(permission) {
      return this.roles.some(r => r.roleCode === 'ROLE_ADMIN') ||
             this.permissions.some(p => p.permissionCode === permission)
    }
  }
})

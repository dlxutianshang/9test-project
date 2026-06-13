import router from '../router'
import { useUserStore } from '../store/user'

const whiteList = ['/login', '/register', '/forgot-password']

router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()
  const hasToken = userStore.token

  if (hasToken) {
    if (to.path === '/login') {
      next({ path: '/' })
    } else {
      if (!userStore.userInfo || !userStore.userInfo.id) {
        try {
          await userStore.getUserInfo()
        } catch (error) {
          userStore.logout()
          next(`/login?redirect=${to.path}`)
          return
        }
      }
      next()
    }
  } else {
    if (whiteList.indexOf(to.path) !== -1) {
      next()
    } else {
      next(`/login?redirect=${to.path}`)
    }
  }
})

router.afterEach(() => {
})

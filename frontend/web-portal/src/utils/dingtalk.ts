import { dingtalkLogin, type LoginResponse } from '../api'

declare const dd: any

export function initDingtalkAuth(): Promise<LoginResponse | null> {
  return new Promise((resolve) => {
    if (typeof dd === 'undefined') {
      console.warn('DingTalk JSAPI not available, running outside DingTalk')
      resolve(null)
      return
    }

    dd.ready(() => {
      dd.runtime.permission.requestAuthCode({
        corpId: '__corpId__',
        onSuccess: (result: { code: string }) => {
          dingtalkLogin(result.code)
            .then((user) => {
              localStorage.setItem('baopu-session', JSON.stringify(user))
              resolve(user)
            })
            .catch(() => resolve(null))
        },
        onFail: () => resolve(null)
      })
    })

    dd.error(() => resolve(null))
  })
}

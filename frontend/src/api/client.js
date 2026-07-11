import axios from 'axios'

// Single axios instance. baseURL '/api' is proxied to the gateway in dev.
const api = axios.create({ baseURL: '/api' })

// Attach the JWT to every request if we have one.
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('of_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Most services wrap responses in { success, message, data }. Auth returns a flat DTO.
export const unwrap = (res) => (res?.data && 'data' in res.data ? res.data.data : res.data)

export default api

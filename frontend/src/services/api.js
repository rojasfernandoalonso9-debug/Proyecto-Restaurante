import axios from 'axios'

export const api = axios.create({ baseURL: import.meta.env.VITE_API_URL || '/api', timeout: 15000 })

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('restaurant_token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

api.interceptors.response.use((response) => response, (error) => {
  if (error.response?.status === 401 && !error.config?.url?.includes('/auth/login')) {
    localStorage.removeItem('restaurant_token')
    window.dispatchEvent(new Event('restaurant:session-expired'))
  }
  const message = error.response?.data?.error || error.response?.data?.message || 'No se pudo completar la solicitud.'
  return Promise.reject(new Error(message))
})

export const endpoints = {
  login: (credentials) => api.post('/auth/login', credentials),
  me: () => api.get('/auth/me'),
  dashboard: () => api.get('/dashboard'),
  tables: () => api.get('/tables'),
  products: (availableOnly = false) => api.get('/products', { params: { availableOnly } }),
  categories: () => api.get('/categories'),
  orders: () => api.get('/orders'),
  kitchen: () => api.get('/orders/kitchen'),
  supplies: () => api.get('/inventory/supplies'),
  movements: () => api.get('/inventory/movements'),
  sales: () => api.get('/sales'),
  users: () => api.get('/users'),
  report: (period) => api.get('/reports', { params: { period } }),
}

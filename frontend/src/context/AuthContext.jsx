import { createContext, useContext, useEffect, useMemo, useState } from 'react'
import { endpoints } from '../services/api.js'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)
  useEffect(() => {
    const expire = () => { setUser(null); setLoading(false) }
    window.addEventListener('restaurant:session-expired', expire)
    const token = localStorage.getItem('restaurant_token')
    if (!token) setLoading(false)
    else endpoints.me().then(({ data }) => setUser(data)).catch(() => localStorage.removeItem('restaurant_token')).finally(() => setLoading(false))
    return () => window.removeEventListener('restaurant:session-expired', expire)
  }, [])
  const value = useMemo(() => ({
    user, loading,
    async login(credentials) {
      const { data } = await endpoints.login(credentials)
      localStorage.setItem('restaurant_token', data.token)
      const { data: profile } = await endpoints.me()
      setUser(profile)
      return profile
    },
    logout() { localStorage.removeItem('restaurant_token'); setUser(null) },
  }), [user, loading])
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) throw new Error('useAuth debe usarse dentro de AuthProvider')
  return context
}

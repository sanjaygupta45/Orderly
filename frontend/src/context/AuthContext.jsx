import { createContext, useContext, useState } from 'react'
import api from '../api/client'

const AuthContext = createContext(null)
export const useAuth = () => useContext(AuthContext)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const raw = localStorage.getItem('of_user')
    return raw ? JSON.parse(raw) : null
  })

  // auth-service returns a flat LoginResponseDTO (accessToken, userId, email, role, ...)
  const persist = (data) => {
    localStorage.setItem('of_token', data.accessToken)
    const u = { userId: data.userId, email: data.email, role: data.role, fullName: data.fullName }
    localStorage.setItem('of_user', JSON.stringify(u))
    setUser(u)
  }

  const login = async (email, password) => {
    const { data } = await api.post('/auth/login', { email, password })
    persist(data)
  }

  const register = async (payload) => {
    const { data } = await api.post('/auth/register', payload)
    persist(data)
  }

  const logout = () => {
    localStorage.removeItem('of_token')
    localStorage.removeItem('of_user')
    setUser(null)
  }

  return (
    <AuthContext.Provider value={{ user, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

import { useState } from 'react'
import { useNavigate, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Login() {
  const { login, register } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const [mode, setMode] = useState('login')
  const [form, setForm] = useState({ email: '', password: '', fullName: '', role: 'CUSTOMER' })
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)

  const dest = location.state?.from || '/'
  const set = (key) => (e) => setForm({ ...form, [key]: e.target.value })

  const submit = async (e) => {
    e.preventDefault()
    setError('')
    setBusy(true)
    try {
      if (mode === 'login') await login(form.email, form.password)
      else await register(form)
      navigate(dest, { replace: true })
    } catch (err) {
      setError(err.response?.data?.message || 'Authentication failed')
    } finally {
      setBusy(false)
    }
  }

  return (
    <div className="mx-auto max-w-sm rounded-lg border bg-white p-6 shadow-sm">
      <h1 className="mb-4 text-xl font-bold">{mode === 'login' ? 'Login' : 'Create account'}</h1>
      <form onSubmit={submit} className="space-y-3">
        {mode === 'register' && (
          <input className="w-full rounded border p-2" placeholder="Full name" value={form.fullName} onChange={set('fullName')} required />
        )}
        <input className="w-full rounded border p-2" type="email" placeholder="Email" value={form.email} onChange={set('email')} required />
        <input className="w-full rounded border p-2" type="password" placeholder="Password (min 6)" value={form.password} onChange={set('password')} required />
        {mode === 'register' && (
          <select className="w-full rounded border p-2" value={form.role} onChange={set('role')}>
            <option value="CUSTOMER">Customer</option>
            <option value="ADMIN">Admin</option>
          </select>
        )}
        {error && <p className="text-sm text-red-600">{error}</p>}
        <button disabled={busy} className="w-full rounded bg-indigo-600 py-2 text-white hover:bg-indigo-700 disabled:opacity-50">
          {busy ? '…' : mode === 'login' ? 'Login' : 'Register'}
        </button>
      </form>
      <button
        onClick={() => { setMode(mode === 'login' ? 'register' : 'login'); setError('') }}
        className="mt-3 text-sm text-indigo-600 hover:underline"
      >
        {mode === 'login' ? 'Need an account? Register' : 'Have an account? Login'}
      </button>
    </div>
  )
}

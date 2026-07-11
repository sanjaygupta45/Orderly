import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { useCart } from '../context/CartContext'

export default function Navbar() {
  const { user, logout } = useAuth()
  const { count } = useCart()
  const navigate = useNavigate()

  return (
    <header className="bg-white shadow">
      <nav className="mx-auto flex max-w-5xl items-center justify-between p-4">
        <Link to="/" className="text-xl font-bold text-indigo-600">OrderFlow</Link>
        <div className="flex items-center gap-4 text-sm">
          <Link to="/" className="hover:text-indigo-600">Catalog</Link>
          <Link to="/cart" className="hover:text-indigo-600">
            Cart
            {count > 0 && (
              <span className="ml-1 rounded-full bg-indigo-600 px-2 py-0.5 text-xs text-white">{count}</span>
            )}
          </Link>
          {user && <Link to="/orders" className="hover:text-indigo-600">Orders</Link>}
          {user ? (
            <>
              <span className="hidden text-slate-500 sm:inline">{user.email}</span>
              <button
                onClick={() => { logout(); navigate('/') }}
                className="rounded bg-slate-200 px-3 py-1 hover:bg-slate-300"
              >
                Logout
              </button>
            </>
          ) : (
            <Link to="/login" className="rounded bg-indigo-600 px-3 py-1 text-white hover:bg-indigo-700">Login</Link>
          )}
        </div>
      </nav>
    </header>
  )
}

import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import api, { unwrap } from '../api/client'
import { useCart } from '../context/CartContext'
import { useAuth } from '../context/AuthContext'

export default function Checkout() {
  const { items, total, clear } = useCart()
  const { user } = useAuth()
  const navigate = useNavigate()
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState('')

  const placeOrder = async () => {
    setBusy(true)
    setError('')
    try {
      const payload = {
        userId: user.userId,
        items: items.map((i) => ({ skuCode: i.skuCode, quantity: i.quantity, unitPrice: i.unitPrice })),
      }
      const order = unwrap(await api.post('/v1/orders', payload))
      clear()
      navigate(`/orders/${order.orderId}`) // confirmation + live tracking
    } catch (err) {
      setError(err.response?.data?.message || 'Could not place order')
    } finally {
      setBusy(false)
    }
  }

  if (items.length === 0) return <p className="text-slate-500">Your cart is empty.</p>

  return (
    <div className="rounded-lg border bg-white p-6 shadow-sm">
      <h1 className="mb-4 text-2xl font-bold">Checkout</h1>
      <ul className="divide-y">
        {items.map((i) => (
          <li key={i.skuCode} className="flex justify-between py-2">
            <span>{i.name} × {i.quantity}</span>
            <span>${(i.unitPrice * i.quantity).toFixed(2)}</span>
          </li>
        ))}
      </ul>
      <div className="mt-3 flex justify-between font-bold"><span>Total</span><span>${total.toFixed(2)}</span></div>
      {error && <p className="mt-3 text-sm text-red-600">{error}</p>}
      <button onClick={placeOrder} disabled={busy} className="mt-4 w-full rounded bg-indigo-600 py-2 text-white hover:bg-indigo-700 disabled:opacity-50">
        {busy ? 'Placing order…' : 'Place order'}
      </button>
    </div>
  )
}

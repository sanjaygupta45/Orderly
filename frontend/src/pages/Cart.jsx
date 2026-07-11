import { Link, useNavigate } from 'react-router-dom'
import { useCart } from '../context/CartContext'

export default function Cart() {
  const { items, updateQty, removeItem, total } = useCart()
  const navigate = useNavigate()

  if (items.length === 0) {
    return (
      <div>
        <h1 className="mb-4 text-2xl font-bold">Cart</h1>
        <p className="text-slate-500">Your cart is empty. <Link to="/" className="text-indigo-600 hover:underline">Browse products</Link></p>
      </div>
    )
  }

  return (
    <div>
      <h1 className="mb-4 text-2xl font-bold">Cart</h1>
      <div className="space-y-2">
        {items.map((i) => (
          <div key={i.skuCode} className="flex items-center justify-between rounded border bg-white p-3">
            <div>
              <p className="font-semibold">{i.name}</p>
              <p className="text-sm text-slate-500">${Number(i.unitPrice).toFixed(2)} · {i.skuCode}</p>
            </div>
            <div className="flex items-center gap-3">
              <input
                type="number"
                min="1"
                value={i.quantity}
                onChange={(e) => updateQty(i.skuCode, parseInt(e.target.value || '1', 10))}
                className="w-16 rounded border p-1"
              />
              <span className="w-20 text-right font-semibold">${(i.unitPrice * i.quantity).toFixed(2)}</span>
              <button onClick={() => removeItem(i.skuCode)} className="text-sm text-red-600 hover:underline">Remove</button>
            </div>
          </div>
        ))}
      </div>
      <div className="mt-4 flex items-center justify-between">
        <span className="text-lg font-bold">Total: ${total.toFixed(2)}</span>
        <button onClick={() => navigate('/checkout')} className="rounded bg-indigo-600 px-4 py-2 text-white hover:bg-indigo-700">
          Checkout
        </button>
      </div>
    </div>
  )
}

import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import api, { unwrap } from '../api/client'
import StatusBadge from '../components/StatusBadge'

export default function OrderTracking() {
  const { orderId } = useParams()
  const [order, setOrder] = useState(null)
  const [error, setError] = useState('')

  // Poll the order while the saga runs so the status updates live.
  useEffect(() => {
    let active = true
    const poll = async () => {
      try {
        const res = await api.get(`/v1/orders/${orderId}`)
        if (active) setOrder(unwrap(res))
      } catch {
        if (active) setError('Could not load order')
      }
    }
    poll()
    const timer = setInterval(poll, 2000)
    return () => { active = false; clearInterval(timer) }
  }, [orderId])

  if (error) return <p className="text-red-600">{error}</p>
  if (!order) return <p className="text-slate-500">Loading order…</p>

  const terminal = ['CONFIRMED', 'CANCELLED', 'FAILED'].includes(order.status)

  return (
    <div className="rounded-lg border bg-white p-6 shadow-sm">
      <h1 className="text-2xl font-bold">Order tracking</h1>
      <p className="mt-1 font-mono text-sm text-slate-500">{order.orderId}</p>
      <div className="mt-4 flex items-center gap-3">
        <StatusBadge status={order.status} />
        {!terminal && <span className="text-sm text-slate-400">updating live…</span>}
      </div>
      {order.failureReason && <p className="mt-2 text-sm text-red-600">Reason: {order.failureReason}</p>}
      <div className="mt-4">
        <h2 className="font-semibold">Items</h2>
        <ul className="mt-1 divide-y text-sm">
          {order.items?.map((i, idx) => (
            <li key={idx} className="flex justify-between py-1">
              <span>{i.skuCode} × {i.quantity}</span>
              <span>${(Number(i.unitPrice) * i.quantity).toFixed(2)}</span>
            </li>
          ))}
        </ul>
        <div className="mt-2 flex justify-between font-bold"><span>Total</span><span>${Number(order.totalAmount).toFixed(2)}</span></div>
      </div>
      <Link to="/orders" className="mt-4 inline-block text-sm text-indigo-600 hover:underline">View all orders →</Link>
    </div>
  )
}

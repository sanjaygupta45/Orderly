import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import api, { unwrap } from '../api/client'
import { useAuth } from '../context/AuthContext'
import StatusBadge from '../components/StatusBadge'

export default function OrderHistory() {
  const { user } = useAuth()
  const [orders, setOrders] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    api.get(`/v1/orders?userId=${user.userId}`)
      .then((res) => setOrders(unwrap(res) || []))
      .catch(() => setOrders([]))
      .finally(() => setLoading(false))
  }, [user])

  if (loading) return <p className="text-slate-500">Loading…</p>

  return (
    <div>
      <h1 className="mb-4 text-2xl font-bold">Order history</h1>
      {orders.length === 0 ? (
        <p className="text-slate-500">No orders yet.</p>
      ) : (
        <div className="space-y-2">
          {orders.map((o) => (
            <Link
              key={o.orderId}
              to={`/orders/${o.orderId}`}
              className="flex items-center justify-between rounded border bg-white p-3 hover:bg-slate-50"
            >
              <div>
                <p className="font-mono text-sm">{o.orderId}</p>
                <p className="text-sm text-slate-500">${Number(o.totalAmount).toFixed(2)} · {o.items?.length || 0} item(s)</p>
              </div>
              <StatusBadge status={o.status} />
            </Link>
          ))}
        </div>
      )}
    </div>
  )
}

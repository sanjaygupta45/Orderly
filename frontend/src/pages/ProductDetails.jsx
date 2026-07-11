import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import api, { unwrap } from '../api/client'
import { useCart } from '../context/CartContext'
import { useAuth } from '../context/AuthContext'

export default function ProductDetails() {
  const { id } = useParams()
  const { addItem } = useCart()
  const { user } = useAuth()
  const [product, setProduct] = useState(null)
  const [availability, setAvailability] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    api.get(`/v1/products/${id}`)
      .then((res) => setProduct(unwrap(res)))
      .catch(() => setError('Product not found'))
  }, [id])

  // Inventory reads are protected by the gateway, so only fetch when logged in.
  useEffect(() => {
    if (product?.skuCode && user) {
      api.get(`/v1/inventory/${product.skuCode}`)
        .then((res) => setAvailability(unwrap(res)?.availableQuantity))
        .catch(() => setAvailability(null))
    }
  }, [product, user])

  if (error) return <p className="text-red-600">{error}</p>
  if (!product) return <p className="text-slate-500">Loading…</p>

  return (
    <div className="rounded-lg border bg-white p-6 shadow-sm">
      <Link to="/" className="text-sm text-indigo-600 hover:underline">← Back to catalog</Link>
      <h1 className="mt-2 text-2xl font-bold">{product.name}</h1>
      <p className="text-sm text-slate-500">SKU {product.skuCode}{product.category ? ` · ${product.category}` : ''}</p>
      <p className="mt-4">{product.description}</p>
      <p className="mt-4 text-xl font-bold">${Number(product.price).toFixed(2)}</p>
      {availability != null && <p className="mt-1 text-sm text-slate-500">{availability} in stock</p>}
      <button onClick={() => addItem(product)} className="mt-4 rounded bg-indigo-600 px-4 py-2 text-white hover:bg-indigo-700">
        Add to cart
      </button>
    </div>
  )
}

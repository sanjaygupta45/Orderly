import { useEffect, useState } from 'react'
import api, { unwrap } from '../api/client'
import ProductCard from '../components/ProductCard'

export default function Catalog() {
  const [products, setProducts] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    api.get('/v1/products')
      .then((res) => setProducts(unwrap(res) || []))
      .catch(() => setError('Could not load products (is the gateway running?)'))
      .finally(() => setLoading(false))
  }, [])

  if (loading) return <p className="text-slate-500">Loading catalog…</p>
  if (error) return <p className="text-red-600">{error}</p>
  if (products.length === 0) return <p className="text-slate-500">No products yet — an admin can add some.</p>

  return (
    <div>
      <h1 className="mb-4 text-2xl font-bold">Product Catalog</h1>
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
        {products.map((p) => <ProductCard key={p.id} product={p} />)}
      </div>
    </div>
  )
}

import { Link } from 'react-router-dom'
import { useCart } from '../context/CartContext'

export default function ProductCard({ product }) {
  const { addItem } = useCart()
  return (
    <div className="flex flex-col rounded-lg border bg-white p-4 shadow-sm">
      <Link to={`/products/${product.id}`} className="font-semibold hover:text-indigo-600">
        {product.name}
      </Link>
      {product.category && (
        <span className="mt-1 w-fit rounded bg-slate-100 px-2 py-0.5 text-xs text-slate-500">{product.category}</span>
      )}
      <p className="mt-2 flex-1 text-sm text-slate-500">{product.description}</p>
      <div className="mt-3 flex items-center justify-between">
        <span className="font-bold">${Number(product.price).toFixed(2)}</span>
        <button
          onClick={() => addItem(product)}
          className="rounded bg-indigo-600 px-3 py-1 text-sm text-white hover:bg-indigo-700"
        >
          Add to cart
        </button>
      </div>
    </div>
  )
}

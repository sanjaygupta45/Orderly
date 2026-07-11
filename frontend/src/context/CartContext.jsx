import { createContext, useContext, useEffect, useState } from 'react'

const CartContext = createContext(null)
export const useCart = () => useContext(CartContext)

export function CartProvider({ children }) {
  const [items, setItems] = useState(() => JSON.parse(localStorage.getItem('of_cart') || '[]'))

  useEffect(() => {
    localStorage.setItem('of_cart', JSON.stringify(items))
  }, [items])

  const addItem = (product) => {
    setItems((prev) => {
      const existing = prev.find((i) => i.skuCode === product.skuCode)
      if (existing) {
        return prev.map((i) => (i.skuCode === product.skuCode ? { ...i, quantity: i.quantity + 1 } : i))
      }
      return [...prev, { skuCode: product.skuCode, name: product.name, unitPrice: product.price, quantity: 1 }]
    })
  }

  const updateQty = (skuCode, quantity) =>
    setItems((prev) => prev.map((i) => (i.skuCode === skuCode ? { ...i, quantity: Math.max(1, quantity) } : i)))

  const removeItem = (skuCode) => setItems((prev) => prev.filter((i) => i.skuCode !== skuCode))
  const clear = () => setItems([])

  const total = items.reduce((sum, i) => sum + i.unitPrice * i.quantity, 0)
  const count = items.reduce((sum, i) => sum + i.quantity, 0)

  return (
    <CartContext.Provider value={{ items, addItem, updateQty, removeItem, clear, total, count }}>
      {children}
    </CartContext.Provider>
  )
}

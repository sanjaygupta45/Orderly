const COLORS = {
  NEW: 'bg-slate-100 text-slate-700',
  PENDING_PAYMENT: 'bg-amber-100 text-amber-800',
  CONFIRMED: 'bg-green-100 text-green-800',
  CANCELLED: 'bg-red-100 text-red-800',
  FAILED: 'bg-red-100 text-red-800',
}

export default function StatusBadge({ status }) {
  return (
    <span className={`rounded-full px-3 py-1 text-xs font-semibold ${COLORS[status] || 'bg-slate-100 text-slate-700'}`}>
      {status}
    </span>
  )
}

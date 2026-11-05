const labels = {
  AVAILABLE: 'Disponible', OCCUPIED: 'Ocupada', RESERVED: 'Reservada',
  PENDING: 'Pendiente', IN_PREPARATION: 'En preparación', READY: 'Listo',
  DELIVERED: 'Entregado', CANCELLED: 'Cancelado', INACTIVE: 'Inactivo', PAID: 'Pagada', REFUNDED: 'Reembolsada',
  IN: 'Entrada', OUT: 'Salida', ADJUSTMENT: 'Ajuste',
}

export function StatusBadge({ status }) {
  const tone = ({ AVAILABLE: 'green', DELIVERED: 'green', PAID: 'green', READY: 'blue', IN_PREPARATION: 'orange', PENDING: 'orange', RESERVED: 'violet', OCCUPIED: 'blue', CANCELLED: 'red', INACTIVE: 'neutral', REFUNDED: 'red', OUT: 'red', IN: 'green', ADJUSTMENT: 'violet' })[status] || 'neutral'
  return <span className={`status-badge status-${tone}`}><i />{labels[status] || status}</span>
}

import { useEffect, useState } from 'react'
import { Eye, RefreshCw } from 'lucide-react'
import { endpoints } from '../services/api.js'
import { ErrorNotice, LoadingState, Modal, PageTitle } from '../components/UI.jsx'
import { StatusBadge } from '../components/StatusBadge.jsx'

const money = (value) => new Intl.NumberFormat('es-PE', { style: 'currency', currency: 'PEN' }).format(value || 0)
const dateTime = (value) => value ? new Date(value).toLocaleString('es-PE', { dateStyle: 'medium', timeStyle: 'short' }) : '—'

export function SalesPage() {
  const [sales, setSales] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [selected, setSelected] = useState(null)
  function refresh() { setLoading(true); endpoints.sales().then(({ data }) => { setSales(data); setError('') }).catch((e) => setError(e.message)).finally(() => setLoading(false)) }
  useEffect(refresh, [])
  return <><PageTitle eyebrow="PAGOS REGISTRADOS" title="Ventas" subtitle="Consulta las transacciones simuladas y el detalle de cada comprobante." actions={<button className="button button-quiet" onClick={refresh}><RefreshCw size={15} /> Actualizar</button>} /><ErrorNotice message={error} onRetry={refresh} />{loading ? <LoadingState /> : <section className="panel list-panel"><div className="list-toolbar"><div><h3>Transacciones</h3><span>{sales.length} registros recientes</span></div></div><div className="table-scroll"><table className="data-table"><thead><tr><th>FECHA</th><th>VENTA</th><th>PEDIDO</th><th>CAJERO</th><th>MÉTODO</th><th>ESTADO</th><th className="align-right">IMPORTE</th><th /></tr></thead><tbody>{sales.map((sale) => <tr key={sale.id}><td>{dateTime(sale.paidAt)}</td><td><strong className="order-code">V-{String(sale.id).padStart(5, '0')}</strong></td><td>{sale.orderNumber}</td><td>{sale.cashierName}</td><td>{({ CASH: 'Efectivo', CARD: 'Tarjeta', TRANSFER: 'Transferencia' })[sale.paymentMethod]}</td><td><StatusBadge status={sale.status} /></td><td className="align-right amount-cell">{money(sale.amount)}</td><td><button className="icon-button" onClick={() => setSelected(sale)} title="Ver detalle"><Eye size={16} /></button></td></tr>)}</tbody></table></div></section>}{!loading && !sales.length && <div className="empty-state"><strong>Todavía no hay pagos registrados</strong><span>Los pagos de pedidos entregados aparecerán aquí.</span></div>}{selected && <Modal title={`Venta V-${String(selected.id).padStart(5, '0')}`} description={`Pedido ${selected.orderNumber} · ${dateTime(selected.paidAt)}`} onClose={() => setSelected(null)}><div className="sale-detail-list">{selected.items.map((item) => <div className="sale-detail-row" key={`${item.productId}-${item.productName}`}><span><b>{item.quantity}×</b> {item.productName}</span><strong>{money(item.unitPrice * item.quantity)}</strong></div>)}<div className="payment-total"><span>Total · {({ CASH: 'Efectivo', CARD: 'Tarjeta', TRANSFER: 'Transferencia' })[selected.paymentMethod]}</span><strong>{money(selected.amount)}</strong></div></div></Modal>}</>
}

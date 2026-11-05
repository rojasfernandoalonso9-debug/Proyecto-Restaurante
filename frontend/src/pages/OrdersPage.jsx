import { useEffect, useMemo, useState } from 'react'
import { ArrowRight, CreditCard, RefreshCw, UtensilsCrossed, X } from 'lucide-react'
import { api, endpoints } from '../services/api.js'
import { useAuth } from '../context/AuthContext.jsx'
import { CreateButton, EmptyState, ErrorNotice, LoadingState, Modal, PageTitle } from '../components/UI.jsx'
import { StatusBadge } from '../components/StatusBadge.jsx'

const money = (value) => new Intl.NumberFormat('es-PE', { style: 'currency', currency: 'PEN' }).format(value || 0)
const dateTime = (value) => value ? new Date(value).toLocaleString('es-PE', { dateStyle: 'medium', timeStyle: 'short' }) : '—'

export function OrdersPage() {
  const { user } = useAuth()
  const [orders, setOrders] = useState([])
  const [tables, setTables] = useState([])
  const [products, setProducts] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [createOpen, setCreateOpen] = useState(false)
  const [paymentOrder, setPaymentOrder] = useState(null)
  const [paymentMethod, setPaymentMethod] = useState('CARD')
  const [form, setForm] = useState({ tableId: '', notes: '', quantities: {} })
  const [saving, setSaving] = useState(false)
  const [formError, setFormError] = useState('')
  const availableTables = tables.filter((table) => table.status === 'AVAILABLE')
  const cart = useMemo(() => Object.entries(form.quantities).filter(([, quantity]) => quantity > 0).map(([id, quantity]) => ({ productId: Number(id), quantity })), [form.quantities])
  const total = useMemo(() => cart.reduce((sum, line) => sum + (products.find((product) => product.id === line.productId)?.price || 0) * line.quantity, 0), [cart, products])

  async function refresh() {
    setLoading(true)
    try { const [orderData, tableData, productData] = await Promise.all([endpoints.orders(), endpoints.tables(), endpoints.products(true)]); setOrders(orderData.data); setTables(tableData.data); setProducts(productData.data); setError('') }
    catch (e) { setError(e.message) } finally { setLoading(false) }
  }
  useEffect(() => { refresh() }, [])
  function changeQuantity(id, by) { setForm((old) => ({ ...old, quantities: { ...old.quantities, [id]: Math.max(0, (old.quantities[id] || 0) + by) } })) }
  async function createOrder(e) {
    e.preventDefault(); setSaving(true)
    try { await api.post('/orders', { tableId: Number(form.tableId), notes: form.notes, items: cart }); setCreateOpen(false); setForm({ tableId: '', notes: '', quantities: {} }); await refresh() }
    catch (e) { setFormError(e.message) } finally { setSaving(false) }
  }
  async function updateStatus(order, status) { try { await api.patch(`/orders/${order.id}/status`, { status }); await refresh() } catch (e) { setError(e.message) } }
  async function collectPayment(e) {
    e.preventDefault(); if (!paymentOrder) return; setSaving(true)
    try { await api.post(`/orders/${paymentOrder.id}/payment`, { paymentMethod }); setPaymentOrder(null); await refresh() }
    catch (e) { setFormError(e.message) } finally { setSaving(false) }
  }

  return <><PageTitle eyebrow="SERVICIO DE SALÓN" title="Pedidos" subtitle="Crea pedidos, sigue su preparación y registra los pagos." actions={<><button className="button button-quiet" onClick={refresh}><RefreshCw size={15} /> Actualizar</button><CreateButton onClick={() => { setError(''); setFormError(''); setCreateOpen(true) }}>Nuevo pedido</CreateButton></>} /><ErrorNotice message={error} onRetry={refresh} />{loading ? <LoadingState /> : orders.length ? <section className="panel list-panel"><div className="list-toolbar"><div><h3>Historial de pedidos</h3><span>{orders.length} registros</span></div></div><div className="table-scroll"><table className="data-table"><thead><tr><th>PEDIDO</th><th>MESA</th><th>MESERO</th><th>HORA</th><th>ESTADO</th><th className="align-right">TOTAL</th><th>ACCIONES</th></tr></thead><tbody>{orders.map((order) => <tr key={order.id}><td><strong className="order-code">{order.orderNumber}</strong><small className="table-subline">{order.items?.length || 0} productos</small></td><td>Mesa {order.tableNumber}</td><td>{order.waiterName}</td><td>{dateTime(order.openedAt)}</td><td><StatusBadge status={order.status} /></td><td className="align-right amount-cell">{money(order.total)}</td><td><div className="row-actions">{order.status === 'PENDING' && (user?.role === 'ADMINISTRADOR' || order.waiterId === user?.id) && <button className="button button-small button-quiet" onClick={() => updateStatus(order, 'IN_PREPARATION')}>Enviar a cocina</button>}{order.status === 'IN_PREPARATION' && <span className="action-hint">En cocina</span>}{order.status === 'READY' && (user?.role === 'ADMINISTRADOR' || order.waiterId === user?.id) && <button className="button button-small button-primary" onClick={() => updateStatus(order, 'DELIVERED')}>Entregar</button>}{order.status === 'DELIVERED' && ['ADMINISTRADOR', 'MESERO'].includes(user?.role) && (user?.role === 'ADMINISTRADOR' || order.waiterId === user?.id) && <button className="button button-small button-primary" onClick={() => { setFormError(''); setPaymentOrder(order); setPaymentMethod('CARD') }}><CreditCard size={14} />Cobrar</button>}{['PENDING', 'IN_PREPARATION', 'READY'].includes(order.status) && (user?.role === 'ADMINISTRADOR' || order.waiterId === user?.id) && <button className="icon-button danger-icon" onClick={() => updateStatus(order, 'CANCELLED')} title="Cancelar pedido"><X size={16} /></button>}</div></td></tr>)}</tbody></table></div></section> : <EmptyState title="Todavía no hay pedidos" detail="Crea una comanda nueva para iniciar el servicio." />}
    {createOpen && <Modal title="Nuevo pedido" description="Selecciona una mesa disponible y agrega productos." onClose={() => setCreateOpen(false)} wide><form className="form-stack" onSubmit={createOrder}><label>Mesa<select required value={form.tableId} onChange={(e) => setForm({ ...form, tableId: e.target.value })}><option value="">Selecciona una mesa</option>{availableTables.map((table) => <option key={table.id} value={table.id}>Mesa {table.tableNumber} · {table.capacity} personas</option>)}</select></label><div className="cart-product-list">{products.map((product) => <div className="cart-product" key={product.id}><div className="product-thumb"><UtensilsCrossed size={16} /></div><div className="cart-product-name"><strong>{product.name}</strong><span>{money(product.price)}</span></div><div className="quantity-control"><button type="button" onClick={() => changeQuantity(product.id, -1)}>−</button><span>{form.quantities[product.id] || 0}</span><button type="button" onClick={() => changeQuantity(product.id, 1)}>+</button></div></div>)}</div><label>Observaciones<textarea rows="2" maxLength="500" value={form.notes} onChange={(e) => setForm({ ...form, notes: e.target.value })} /></label>{formError && <p className="inline-error">{formError}</p>}<div className="modal-actions"><span className="cart-total">Total estimado <strong>{money(total)}</strong></span><button className="button button-primary" disabled={saving || !availableTables.length || !cart.length}>{saving ? 'Guardando…' : 'Crear comanda'} <ArrowRight size={15} /></button></div></form></Modal>}
    {paymentOrder && <Modal title="Registrar pago" description={`Pedido ${paymentOrder.orderNumber} · ${money(paymentOrder.total)}. El registro es simulado.`} onClose={() => setPaymentOrder(null)}><form className="form-stack" onSubmit={collectPayment}><label>Método de pago<select value={paymentMethod} onChange={(e) => setPaymentMethod(e.target.value)}><option value="CASH">Efectivo</option><option value="CARD">Tarjeta</option><option value="TRANSFER">Transferencia</option></select></label><div className="payment-total"><span>Total a registrar</span><strong>{money(paymentOrder.total)}</strong></div>{formError && <p className="inline-error">{formError}</p>}<div className="modal-actions"><button type="button" className="button button-quiet" onClick={() => setPaymentOrder(null)}>Cancelar</button><button className="button button-primary" disabled={saving}>{saving ? 'Guardando…' : 'Confirmar pago'} <CreditCard size={15} /></button></div></form></Modal>}
  </>
}

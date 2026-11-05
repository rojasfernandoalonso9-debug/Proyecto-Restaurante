import { useEffect, useState } from 'react'
import { ArrowDownRight, ArrowUpRight, Armchair, ArrowRight, ClipboardList, DollarSign, PackageCheck, RefreshCw, Sparkles } from 'lucide-react'
import { Area, AreaChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts'
import { endpoints } from '../services/api.js'
import { Link } from 'react-router-dom'
import { StatusBadge } from '../components/StatusBadge.jsx'
import { PageTitle, LoadingState } from '../components/UI.jsx'

const money = (value) => new Intl.NumberFormat('es-PE', { style: 'currency', currency: 'PEN' }).format(value || 0)

export function DashboardPage() {
  const [data, setData] = useState(null)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)
  const todayLabel = new Intl.DateTimeFormat('es-PE', { weekday: 'long' }).format(new Date()).toLocaleUpperCase('es-PE')
  const load = () => { setLoading(true); endpoints.dashboard().then(({ data: result }) => { setData(result); setError('') }).catch((e) => setError(e.message)).finally(() => setLoading(false)) }
  useEffect(() => { load() }, [])
  const metrics = data ? [
    { label: 'Ventas de hoy', value: money(data.todaySales), helper: 'Pagos registrados', icon: DollarSign, tone: 'blue' },
    { label: 'Pedidos activos', value: data.pendingOrders, helper: 'En espera o preparación', icon: ClipboardList, tone: 'orange' },
    { label: 'Pedidos entregados', value: data.completedOrders, helper: 'Acumulado del sistema', icon: PackageCheck, tone: 'green' },
    { label: 'Mesas ocupadas', value: `${data.occupiedTables} / ${data.occupiedTables + data.availableTables}`, helper: `${data.availableTables} disponibles`, icon: Armchair, tone: 'violet' },
  ] : []
  return <>
    <PageTitle eyebrow={`${todayLabel} · OPERACIONES`} title="Buenos días, equipo" subtitle="Así se mueve La Mesa hoy. Que cada detalle cuente." actions={<button className="button button-quiet" onClick={load}><RefreshCw size={15} /> Actualizar</button>} />
    {error && <div className="notice notice-error">{error} <button onClick={load}>Reintentar</button></div>}
    {loading ? <LoadingState /> : data && <>
      <div className="metric-grid">{metrics.map(({ label, value, helper, icon: Icon, tone }, index) => <article className={`metric-card metric-${tone}`} key={label}><div className="metric-top"><span>{label}</span><div className="metric-icon"><Icon size={18} /></div></div><div className="metric-value">{value}</div><div className="metric-helper">{index === 0 ? <ArrowUpRight size={14} /> : index === 1 ? <ArrowDownRight size={14} /> : <span className="helper-dot" />}{helper}</div><div className="metric-accent" /></article>)}</div>
      <div className="dashboard-grid"><section className="panel sales-panel"><div className="panel-heading"><div><span className="section-kicker">ÚLTIMOS 7 DÍAS</span><h3>Ritmo de ventas</h3></div><span className="chart-legend"><i /> Ingresos</span></div><div className="chart-wrap"><ResponsiveContainer width="100%" height="100%"><AreaChart data={data.sales} margin={{ top: 8, right: 8, left: -18, bottom: 0 }}><defs><linearGradient id="salesFill" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stopColor="#4169e1" stopOpacity={0.18} /><stop offset="95%" stopColor="#4169e1" stopOpacity={0} /></linearGradient></defs><CartesianGrid vertical={false} stroke="#eef0f4" /><XAxis dataKey="date" axisLine={false} tickLine={false} tick={{ fill: '#a0a6b2', fontSize: 11 }} dy={10} /><YAxis axisLine={false} tickLine={false} tick={{ fill: '#a0a6b2', fontSize: 11 }} tickFormatter={(v) => `${v}`} /><Tooltip formatter={(v) => [money(v), 'Ventas']} contentStyle={{ border: '1px solid #edf0f4', borderRadius: 12, boxShadow: '0 8px 24px #13203a12' }} /><Area type="monotone" dataKey="total" stroke="#4169e1" strokeWidth={2.5} fill="url(#salesFill)" activeDot={{ r: 5, strokeWidth: 0 }} /></AreaChart></ResponsiveContainer></div><div className="chart-footer"><span><i className="online-dot" /> Datos conectados a tus ventas</span><span>Moneda: PEN</span></div></section>
        <section className="panel popular-panel"><div className="panel-heading"><div><span className="section-kicker">FAVORITOS DE LA CASA</span><h3>Más pedidos</h3></div><div className="sparkle"><Sparkles size={16} /></div></div><div className="popular-list">{data.topProducts?.length ? data.topProducts.map((item, i) => <div className="popular-row" key={item.name}><span className={`rank rank-${i + 1}`}>{String(i + 1).padStart(2, '0')}</span><span className="popular-name">{item.name}</span><span className="popular-count">{item.quantity}<small> uds.</small></span></div>) : <div className="empty-inline">Aún no hay ventas entregadas.</div>}</div><Link to="/products" className="panel-link">Ver carta completa <ArrowRight size={15} /></Link></section></div>
      <section className="panel activity-panel"><div className="panel-heading activity-heading"><div><span className="section-kicker">EN TIEMPO REAL</span><h3>Actividad reciente</h3></div><Link to="/orders" className="panel-link">Todos los pedidos <ArrowRight size={15} /></Link></div><div className="table-scroll"><table className="data-table"><thead><tr><th>PEDIDO</th><th>MESA</th><th>RESPONSABLE</th><th>HORA</th><th>ESTADO</th><th className="align-right">TOTAL</th></tr></thead><tbody>{data.recentOrders.map((order) => <tr key={order.id}><td><strong className="order-code">{order.orderNumber}</strong></td><td>Mesa {order.tableNumber}</td><td>{order.waiterName}</td><td>{new Date(order.openedAt).toLocaleTimeString('es-PE', { hour: '2-digit', minute: '2-digit' })}</td><td><StatusBadge status={order.status} /></td><td className="align-right amount-cell">{money(order.total)}</td></tr>)}{!data.recentOrders.length && <tr><td colSpan="6" className="empty-cell">Todavía no hay pedidos. <Link to="/orders">Crear el primero</Link></td></tr>}</tbody></table></div></section>
    </>}
  </>
}

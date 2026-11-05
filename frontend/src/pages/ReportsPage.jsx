import { useCallback, useEffect, useState } from 'react'
import { ArrowDownToLine } from 'lucide-react'
import { api, endpoints } from '../services/api.js'
import { EmptyState, ErrorNotice, LoadingState, PageTitle } from '../components/UI.jsx'

const money = (value) => new Intl.NumberFormat('es-PE', { style: 'currency', currency: 'PEN' }).format(value || 0)

export function ReportsPage() {
  const [period, setPeriod] = useState('WEEK')
  const { data, error, loading, refresh } = useReport(period)
  async function exportReport() {
    try {
      const { data: csv } = await api.get('/reports/export', { params: { period }, responseType: 'blob' })
      const url = URL.createObjectURL(csv)
      const link = document.createElement('a')
      link.href = url; link.download = `reporte-${period.toLowerCase()}.csv`; link.click()
      URL.revokeObjectURL(url)
    } catch (e) { window.alert(e.message) }
  }
  return <>
    <PageTitle eyebrow="ANÁLISIS DEL NEGOCIO" title="Reportes" subtitle="Consulta el rendimiento y exporta los resultados para tu análisis." actions={<><select className="period-select" value={period} onChange={(e) => setPeriod(e.target.value)}><option value="DAY">Hoy</option><option value="WEEK">Esta semana</option><option value="MONTH">Este mes</option></select><button className="button button-primary" onClick={exportReport}><ArrowDownToLine size={15} /> Exportar Excel / CSV</button></>} />
    <ErrorNotice message={error} onRetry={refresh} />
    {loading ? <LoadingState /> : data && <div className="report-grid">
      <article className="report-card report-card-feature"><span>VENTAS DEL PERÍODO</span><strong>{money(data.totalSales)}</strong><p>{data.paidTransactions} pagos registrados en {data.period.toLowerCase()}.</p></article>
      <article className="report-card"><span>PEDIDOS ENTREGADOS</span><strong>{data.deliveredOrders}</strong><p>Dentro del período seleccionado.</p></article>
      <article className="report-card"><span>MESEROS CON VENTAS</span><strong>{data.waiterPerformance.length}</strong><p>Rendimiento por responsable.</p></article>
      <article className="report-card report-card-wide"><header><div><span>VENTAS POR DÍA</span><h3>Período seleccionado</h3></div><span className="role-chip">PEN · datos reales</span></header><div className="report-bars">{data.salesByDay.map((point) => { const max = Math.max(1, ...data.salesByDay.map((d) => d.total)); return <div className="report-bar-col" key={point.date}><div className="report-bar-value">{point.total ? money(point.total) : '—'}</div><div className="report-bar-track"><i style={{ height: `${Math.max(4, point.total / max * 100)}%` }} /></div><span>{point.date}</span></div> })}</div></article>
      <article className="report-card report-card-wide"><header><div><span>PRODUCTOS DESTACADOS</span><h3>Más vendidos</h3></div></header>{data.topProducts.length ? data.topProducts.map((product, index) => <div className="report-rank-row" key={product.name}><span>{String(index + 1).padStart(2, '0')}</span><b>{product.name}</b><strong>{product.quantity} uds.</strong></div>) : <EmptyState title="Sin ventas registradas" detail="Los productos aparecerán al registrar pagos." />}</article>
      <article className="report-card report-card-wide"><header><div><span>RENDIMIENTO DE MESEROS</span><h3>Ventas por responsable</h3></div></header>{data.waiterPerformance.length ? data.waiterPerformance.map((waiter) => <div className="report-rank-row" key={waiter.userId}><span>{waiter.orders} pedidos</span><b>{waiter.name}</b><strong>{money(waiter.sales)}</strong></div>) : <EmptyState title="Sin ventas en el período" detail="Las ventas se agruparán por mesero responsable." />}</article>
    </div>}
  </>
}

function useReport(period) {
  const [data, setData] = useState(null)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)
  const refresh = useCallback(() => { setLoading(true); endpoints.report(period).then(({ data: result }) => { setData(result); setError('') }).catch((e) => setError(e.message)).finally(() => setLoading(false)) }, [period])
  useEffect(() => { refresh() }, [refresh])
  return { data, error, loading, refresh }
}

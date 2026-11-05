import { useEffect, useState } from 'react'
import { Armchair, Edit3, Trash2 } from 'lucide-react'
import { api, endpoints } from '../services/api.js'
import { useAuth } from '../context/AuthContext.jsx'
import { CreateButton, EmptyState, ErrorNotice, LoadingState, Modal, PageTitle } from '../components/UI.jsx'
import { StatusBadge } from '../components/StatusBadge.jsx'

export function TablesPage() {
  const { user } = useAuth()
  const [tables, setTables] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [modal, setModal] = useState(false)
  const [editing, setEditing] = useState(null)
  const [form, setForm] = useState({ tableNumber: '', capacity: 4, status: 'AVAILABLE' })
  const [message, setMessage] = useState('')
  function refresh() { setLoading(true); endpoints.tables().then(({ data }) => { setTables(data); setError('') }).catch((e) => setError(e.message)).finally(() => setLoading(false)) }
  useEffect(refresh, [])
  function open(table = null) { setEditing(table); setForm(table ? { tableNumber: table.tableNumber, capacity: table.capacity, status: table.status } : { tableNumber: '', capacity: 4, status: 'AVAILABLE' }); setMessage(''); setModal(true) }
  async function save(e) { e.preventDefault(); try { const body = { ...form, tableNumber: Number(form.tableNumber), capacity: Number(form.capacity) }; if (editing) await api.put(`/tables/${editing.id}`, body); else await api.post('/tables', body); setModal(false); refresh() } catch (err) { setMessage(err.message) } }
  async function remove(table) { if (!window.confirm(`¿Eliminar la mesa ${table.tableNumber}? Solo se permite si no tiene historial de pedidos.`)) return; try { await api.delete(`/tables/${table.id}`); refresh() } catch (e) { setError(e.message) } }
  return <><PageTitle eyebrow="DISTRIBUCIÓN DEL SALÓN" title="Mesas" subtitle="Consulta de un vistazo la disponibilidad de cada espacio." actions={user?.role === 'ADMINISTRADOR' && <CreateButton onClick={() => open()}>Agregar mesa</CreateButton>} /><ErrorNotice message={error} onRetry={refresh} />{loading ? <LoadingState /> : <div className="table-grid">{tables?.map((table) => <article className={`table-card table-${table.status.toLowerCase()}`} key={table.id}><div className="table-card-top"><span className="table-icon"><Armchair size={21} /></span><StatusBadge status={table.status} />{user?.role === 'ADMINISTRADOR' && <div className="row-actions"><button className="icon-button" onClick={() => open(table)} title="Editar mesa"><Edit3 size={14} /></button><button className="icon-button danger-icon" onClick={() => remove(table)} title="Eliminar mesa"><Trash2 size={14} /></button></div>}</div><div className="table-card-number">{String(table.tableNumber).padStart(2, '0')}<small>MESA</small></div><div className="table-card-bottom"><span>{table.capacity} personas</span><span className="table-seat-icons">{Array.from({ length: Math.min(table.capacity, 4) }, (_, i) => <i key={i} />)}</span></div></article>)}</div>}{!loading && !tables?.length && <EmptyState title="No hay mesas registradas" detail="Agrega las mesas del salón para comenzar." />}{modal && <Modal title={editing ? 'Editar mesa' : 'Agregar mesa'} description="Configura la capacidad y disponibilidad de este espacio." onClose={() => setModal(false)}><form className="form-stack" onSubmit={save}><label>Número de mesa<input type="number" min="1" required value={form.tableNumber} onChange={(e) => setForm({ ...form, tableNumber: e.target.value })} /></label><label>Capacidad<input type="number" min="1" max="32" required value={form.capacity} onChange={(e) => setForm({ ...form, capacity: e.target.value })} /></label>{editing && <label>Estado<select value={form.status} onChange={(e) => setForm({ ...form, status: e.target.value })}><option value="AVAILABLE">Disponible</option><option value="OCCUPIED">Ocupada</option><option value="RESERVED">Reservada</option></select></label>}{message && <p className="inline-error">{message}</p>}<div className="modal-actions"><button type="button" className="button button-quiet" onClick={() => setModal(false)}>Cancelar</button><button className="button button-primary">Guardar mesa</button></div></form></Modal>}</>
}

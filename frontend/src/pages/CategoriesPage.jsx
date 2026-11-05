import { useEffect, useState } from 'react'
import { api, endpoints } from '../services/api.js'
import { CreateButton, EmptyState, ErrorNotice, LoadingState, Modal, PageTitle } from '../components/UI.jsx'
import { StatusBadge } from '../components/StatusBadge.jsx'

export function CategoriesPage() {
  const [categories, setCategories] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [editing, setEditing] = useState(null)
  const [modal, setModal] = useState(false)
  const [form, setForm] = useState({ name: '', description: '', active: true })
  const [message, setMessage] = useState('')
  function refresh() { setLoading(true); endpoints.categories().then(({ data }) => { setCategories(data); setError('') }).catch((e) => setError(e.message)).finally(() => setLoading(false)) }
  useEffect(refresh, [])
  function open(category = null) { setEditing(category); setForm(category ? { name: category.name, description: category.description || '', active: category.active } : { name: '', description: '', active: true }); setMessage(''); setModal(true) }
  async function save(event) { event.preventDefault(); try { if (editing) await api.put(`/categories/${editing.id}`, form); else await api.post('/categories', form); setModal(false); refresh() } catch (e) { setMessage(e.message) } }
  return <><PageTitle eyebrow="ORGANIZACIÓN DE CARTA" title="Categorías" subtitle="Agrupa productos para que la carta sea fácil de recorrer." actions={<CreateButton onClick={() => open()}>Nueva categoría</CreateButton>} /><ErrorNotice message={error} onRetry={refresh} />{loading ? <LoadingState /> : categories?.length ? <section className="category-grid">{categories.map((category) => <article className="category-card" key={category.id}><div className="category-card-mark">{category.name.slice(0, 1)}</div><span className="section-kicker">CATEGORÍA</span><h3>{category.name}</h3><p>{category.description || 'Sin descripción'}</p><div className="category-card-footer"><StatusBadge status={category.active ? 'AVAILABLE' : 'INACTIVE'} /><button className="button button-quiet button-small" onClick={() => open(category)}>Editar</button></div></article>)}</section> : <EmptyState title="Todavía no hay categorías" detail="Agrega categorías para organizar la carta." />}{modal && <Modal title={editing ? 'Editar categoría' : 'Nueva categoría'} description="Nombra y describe una sección de la carta." onClose={() => setModal(false)}><form className="form-stack" onSubmit={save}><label>Nombre<input required maxLength="80" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} /></label><label>Descripción<textarea rows="3" maxLength="255" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} /></label>{editing && <label className="checkbox-label"><input type="checkbox" checked={form.active} onChange={(e) => setForm({ ...form, active: e.target.checked })} /> Disponible</label>}{message && <p className="inline-error">{message}</p>}<div className="modal-actions"><button type="button" className="button button-quiet" onClick={() => setModal(false)}>Cancelar</button><button className="button button-primary">Guardar categoría</button></div></form></Modal>}</>
}

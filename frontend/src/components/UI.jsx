import { LoaderCircle, Plus, X } from 'lucide-react'

export function PageTitle({ eyebrow, title, subtitle, actions }) {
  return <div className="page-title-row"><div><div className="eyebrow">{eyebrow}</div><h1>{title}</h1><p>{subtitle}</p></div>{actions && <div className="page-actions">{actions}</div>}</div>
}

export function LoadingState({ label = 'Cargando información…' }) {
  return <div className="loading-state"><LoaderCircle className="spin" size={21} /><span>{label}</span></div>
}

export function EmptyState({ title, detail }) {
  return <div className="empty-state"><div className="empty-orbit">✳</div><strong>{title}</strong><span>{detail}</span></div>
}

export function Modal({ title, description, onClose, children, wide = false }) {
  return <div className="modal-backdrop" onMouseDown={(event) => event.target === event.currentTarget && onClose()}><section className={`modal-card ${wide ? 'modal-wide' : ''}`} role="dialog" aria-modal="true"><header className="modal-header"><div><span className="section-kicker">LA MESA · GESTIÓN</span><h2>{title}</h2>{description && <p>{description}</p>}</div><button className="icon-button" onClick={onClose} aria-label="Cerrar"><X size={18} /></button></header>{children}</section></div>
}

export function CreateButton({ onClick, children = 'Nuevo registro' }) {
  return <button className="button button-primary" onClick={onClick}><Plus size={16} />{children}</button>
}

export function ErrorNotice({ message, onRetry }) {
  if (!message) return null
  return <div className="notice notice-error">{message}{onRetry && <button onClick={onRetry}>Reintentar</button>}</div>
}

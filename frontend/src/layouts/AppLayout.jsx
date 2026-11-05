import { useState } from 'react'
import { NavLink, Outlet, useLocation, useNavigate } from 'react-router-dom'
import { Armchair, BarChart3, ChefHat, ClipboardList, Coffee, LayoutDashboard, LogOut, Menu, Package, ReceiptText, Tags, Users, X } from 'lucide-react'
import { useAuth } from '../context/AuthContext.jsx'

const navigation = [
  { to: '/dashboard', label: 'Resumen', icon: LayoutDashboard, roles: ['ADMINISTRADOR', 'MESERO'] },
  { to: '/orders', label: 'Pedidos', icon: ClipboardList, roles: ['ADMINISTRADOR', 'MESERO'] },
  { to: '/kitchen', label: 'Cocina', icon: ChefHat, roles: ['ADMINISTRADOR', 'COCINA'] },
  { to: '/tables', label: 'Mesas', icon: Armchair, roles: ['ADMINISTRADOR', 'MESERO'] },
  { to: '/products', label: 'Productos', icon: Coffee, roles: ['ADMINISTRADOR'] },
  { to: '/categories', label: 'Categorías', icon: Tags, roles: ['ADMINISTRADOR'] },
  { to: '/inventory', label: 'Inventario', icon: Package, roles: ['ADMINISTRADOR', 'COCINA'] },
  { to: '/sales', label: 'Ventas', icon: ReceiptText, roles: ['ADMINISTRADOR'] },
  { to: '/reports', label: 'Reportes', icon: BarChart3, roles: ['ADMINISTRADOR'] },
  { to: '/users', label: 'Equipo', icon: Users, roles: ['ADMINISTRADOR'] },
]
const titles = Object.fromEntries(navigation.map(({ to, label }) => [to, label]))

export function AppLayout() {
  const [open, setOpen] = useState(false)
  const { user, logout } = useAuth()
  const location = useLocation()
  const navigate = useNavigate()
  const today = new Intl.DateTimeFormat('es-PE', { weekday: 'long', day: 'numeric', month: 'long' }).format(new Date())
  const visible = navigation.filter((item) => item.roles.includes(user?.role))
  function signOut() { logout(); navigate('/login') }

  return <div className="app-shell">
    {open && <button className="mobile-scrim" aria-label="Cerrar menú" onClick={() => setOpen(false)} />}
    <aside className={`sidebar ${open ? 'sidebar-open' : ''}`}>
      <div className="brand"><div className="brand-mark"><span>M</span></div><div><strong>mesa</strong><small>RESTAURANT OS</small></div><button className="icon-button mobile-close" onClick={() => setOpen(false)} aria-label="Cerrar"><X size={19} /></button></div>
      <div className="venue-switch"><span className="venue-dot" /><div><small>LOCAL ACTIVO</small><strong>La Mesa · Principal</strong></div><span className="chevron">⌄</span></div>
      <p className="nav-caption">GESTIÓN</p>
      <nav className="main-nav">
        {visible.map(({ to, label, icon: Icon }) => <NavLink key={to} to={to} onClick={() => setOpen(false)} className={({ isActive }) => `nav-link ${isActive ? 'nav-link-active' : ''}`}><Icon size={18} strokeWidth={1.8} /><span>{label}</span>{to === '/kitchen' && <i className="nav-pulse" />}</NavLink>)}
      </nav>
      <div className="sidebar-bottom"><div className="plan-card"><div className="plan-icon"><Coffee size={17} /></div><strong>Todo en orden</strong><span>Tu equipo está listo para atender.</span></div><div className="sidebar-user"><div className="avatar avatar-side">{user?.fullName?.slice(0, 1) || 'U'}</div><div className="user-copy"><strong>{user?.fullName}</strong><span>{user?.role?.toLowerCase()}</span></div><button className="icon-button logout-button" onClick={signOut} title="Cerrar sesión" aria-label="Cerrar sesión"><LogOut size={17} /></button></div></div>
    </aside>
    <div className="main-column">
      <header className="topbar"><button className="icon-button mobile-menu" onClick={() => setOpen(true)} aria-label="Abrir menú"><Menu size={20} /></button><div className="breadcrumb"><span>La Mesa</span><b>/</b><strong>{titles[location.pathname] || 'Panel'}</strong></div><div className="topbar-right"><span className="top-date">{today}</span><div className="avatar avatar-top">{user?.fullName?.slice(0, 1) || 'U'}</div></div></header>
      <main className="page-content"><Outlet /></main>
      <footer className="app-footer"><span>MESa · Gestión de restaurante</span><span>Hecho para servir mejor <span className="footer-heart">✦</span></span></footer>
    </div>
  </div>
}

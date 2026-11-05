import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { ArrowRight, Eye, EyeOff, LockKeyhole, Mail, UtensilsCrossed } from 'lucide-react'
import { useAuth } from '../context/AuthContext.jsx'

export function LoginPage() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState({ username: '', password: '' })
  const [showPassword, setShowPassword] = useState(false)
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState('')
  async function submit(event) {
    event.preventDefault(); setBusy(true); setError('')
    try { const profile = await login(form); navigate(profile.role === 'COCINA' ? '/kitchen' : '/dashboard') } catch (err) { setError(err.message) } finally { setBusy(false) }
  }
  return <main className="login-shell"><section className="login-art"><div className="login-art-top"><div className="brand-mark brand-mark-light"><span>M</span></div><span>LA MESA · RESTAURANT OS</span></div><div className="login-quote"><div className="quote-mark">“</div><h1>Cada servicio<br />cuenta una historia.</h1><p>Un gran restaurante se construye cuidando cada detalle, desde la cocina hasta la mesa.</p><div className="quote-rule" /><span>OPERACIONES MÁS SIMPLES. EXPERIENCIAS MEJORES.</span></div><div className="login-art-footer"><span><i /> SISTEMA OPERATIVO</span><span>© 2026 LA MESA</span></div><div className="art-circle art-circle-one" /><div className="art-circle art-circle-two" /></section><section className="login-panel"><div className="login-form-wrap"><div className="login-mobile-brand"><div className="brand-mark"><span>M</span></div><strong>mesa</strong></div><div className="eyebrow">BIENVENIDO DE VUELTA</div><h2>Tu salón, bajo control.</h2><p className="login-subtitle">Ingresa a tu espacio de trabajo para continuar.</p><form onSubmit={submit} className="login-form"><label htmlFor="username">Usuario</label><div className="input-wrap"><Mail size={17} /><input id="username" autoComplete="username" placeholder="Tu usuario" value={form.username} onChange={(e) => setForm({ ...form, username: e.target.value })} required /></div><label htmlFor="password">Contraseña</label><div className="input-wrap"><LockKeyhole size={17} /><input id="password" type={showPassword ? 'text' : 'password'} autoComplete="current-password" placeholder="••••••••" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} required /><button type="button" className="password-toggle" onClick={() => setShowPassword(!showPassword)} aria-label="Mostrar contraseña">{showPassword ? <EyeOff size={17} /> : <Eye size={17} />}</button></div>{error && <div className="inline-error">{error}</div>}<button className="button button-primary login-submit" disabled={busy}>{busy ? 'Ingresando…' : 'Ingresar al sistema'}<ArrowRight size={17} /></button></form><div className="login-demo"><div className="demo-icon"><UtensilsCrossed size={16} /></div><div><strong>Acceso de demostración</strong><span>Usuario: admin <b>·</b> Clave: password</span></div><button onClick={() => setForm({ username: 'admin', password: 'password' })}>Usar</button></div><div className="login-legal">Al ingresar aceptas las políticas internas de La Mesa.<br />¿Necesitas ayuda? <a href="mailto:soporte@example.test">Contactar soporte</a></div></div></section></main>
}

import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import { AuthProvider, useAuth } from './context/AuthContext.jsx'
import { ProtectedRoute } from './routes/ProtectedRoute.jsx'
import { AppLayout } from './layouts/AppLayout.jsx'
import { LoginPage } from './pages/LoginPage.jsx'
import { DashboardPage } from './pages/DashboardPage.jsx'
import { ProductsPage, KitchenPage, InventoryPage } from './pages/ManagementPages.jsx'
import { UsersPage } from './pages/UsersPage.jsx'
import { OrdersPage } from './pages/OrdersPage.jsx'
import { SalesPage } from './pages/SalesPage.jsx'
import { TablesPage } from './pages/TablesPage.jsx'
import { ReportsPage } from './pages/ReportsPage.jsx'
import { CategoriesPage } from './pages/CategoriesPage.jsx'

function HomeRedirect() {
  const { user, loading } = useAuth()
  if (loading) return <div className="boot-screen">Cargando Mesa...</div>
  return <Navigate to={user ? (user.role === 'COCINA' ? '/kitchen' : '/dashboard') : '/login'} replace />
}

function AppRoutes() {
  return <Routes>
    <Route path="/login" element={<LoginPage />} />
    <Route element={<ProtectedRoute />}>
      <Route element={<AppLayout />}>
        <Route path="/dashboard" element={<ProtectedRoute roles={['ADMINISTRADOR', 'MESERO']}><DashboardPage /></ProtectedRoute>} />
        <Route path="/tables" element={<ProtectedRoute roles={['ADMINISTRADOR', 'MESERO']}><TablesPage /></ProtectedRoute>} />
        <Route path="/orders" element={<ProtectedRoute roles={['ADMINISTRADOR', 'MESERO']}><OrdersPage /></ProtectedRoute>} />
        <Route path="/kitchen" element={<ProtectedRoute roles={['ADMINISTRADOR', 'COCINA']}><KitchenPage /></ProtectedRoute>} />
        <Route path="/products" element={<ProtectedRoute roles={['ADMINISTRADOR']}><ProductsPage /></ProtectedRoute>} />
        <Route path="/categories" element={<ProtectedRoute roles={['ADMINISTRADOR']}><CategoriesPage /></ProtectedRoute>} />
        <Route path="/inventory" element={<ProtectedRoute roles={['ADMINISTRADOR', 'COCINA']}><InventoryPage /></ProtectedRoute>} />
        <Route path="/sales" element={<ProtectedRoute roles={['ADMINISTRADOR']}><SalesPage /></ProtectedRoute>} />
        <Route path="/users" element={<ProtectedRoute roles={['ADMINISTRADOR']}><UsersPage /></ProtectedRoute>} />
        <Route path="/reports" element={<ProtectedRoute roles={['ADMINISTRADOR']}><ReportsPage /></ProtectedRoute>} />
      </Route>
    </Route>
    <Route path="*" element={<HomeRedirect />} />
  </Routes>
}

export default function App() {
  return <BrowserRouter><AuthProvider><AppRoutes /></AuthProvider></BrowserRouter>
}

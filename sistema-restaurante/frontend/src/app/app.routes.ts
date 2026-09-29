import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'login' },

  { path: 'login', loadComponent: () => import('./features/login/login.component').then(m => m.LoginComponent) },
  { path: 'olvide-password', loadComponent: () => import('./features/login/olvide-password.component').then(m => m.OlvidePasswordComponent) },
  { path: 'restablecer-password', loadComponent: () => import('./features/login/restablecer-password.component').then(m => m.RestablecerPasswordComponent) },

  // CU12 / CU13 - Cliente, sin autenticacion, vía codigo QR
  { path: 'menu/:codigoQr', loadComponent: () => import('./features/menu-publico/menu-publico.component').then(m => m.MenuPublicoComponent) },

  {
    path: 'admin',
    loadComponent: () => import('./features/layout/layout.component').then(m => m.LayoutComponent),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['ADMINISTRADOR'] },
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'usuarios' },
      { path: 'usuarios', loadComponent: () => import('./features/usuarios/usuarios.component').then(m => m.UsuariosComponent) },
      { path: 'inventario', loadComponent: () => import('./features/inventario/inventario-admin.component').then(m => m.InventarioAdminComponent) },
      { path: 'menu', loadComponent: () => import('./features/menu-admin/menu-admin.component').then(m => m.MenuAdminComponent) },
      { path: 'reportes', loadComponent: () => import('./features/reportes/reportes.component').then(m => m.ReportesComponent) },
      { path: 'bitacora', loadComponent: () => import('./features/bitacora/bitacora.component').then(m => m.BitacoraComponent) },
    ],
  },

  {
    path: 'mesero',
    loadComponent: () => import('./features/layout/layout.component').then(m => m.LayoutComponent),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['MESERO'] },
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'pedidos' },
      { path: 'pedidos', loadComponent: () => import('./features/pedidos/pedidos.component').then(m => m.PedidosComponent) },
      { path: 'llamados', loadComponent: () => import('./features/llamados/llamados.component').then(m => m.LlamadosComponent) },
    ],
  },

  {
    path: 'cocina',
    loadComponent: () => import('./features/layout/layout.component').then(m => m.LayoutComponent),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['COCINA'] },
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'pedidos' },
      { path: 'pedidos', loadComponent: () => import('./features/cocina/cocina.component').then(m => m.CocinaComponent) },
      { path: 'inventario', loadComponent: () => import('./features/inventario/inventario-cocina.component').then(m => m.InventarioCocinaComponent) },
    ],
  },

  { path: '**', redirectTo: 'login' },
];

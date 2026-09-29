import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UsuarioService } from '../../core/services/usuario.service';
import { CrearUsuarioRequest, Rol, Usuario } from '../../core/models/usuario.model';

/** CU02 Registrar Usuario / CU04-admin Gestionar estado (Administrador). */
@Component({
  selector: 'app-usuarios',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './usuarios.component.html',
})
export class UsuariosComponent implements OnInit {
  usuarios = signal<Usuario[]>([]);
  mostrarFormulario = signal(false);
  cargando = signal(false);
  error = signal<string | null>(null);
  mensaje = signal<string | null>(null);

  nuevo: CrearUsuarioRequest = { nombreCompleto: '', correo: '', telefono: '', rol: 'MESERO', passwordTemporal: '' };
  roles: Rol[] = ['ADMINISTRADOR', 'MESERO', 'COCINA'];

  constructor(private usuarioService: UsuarioService) {}

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.usuarioService.listar().subscribe((data) => this.usuarios.set(data));
  }

  abrirFormulario(): void {
    this.error.set(null);
    this.mensaje.set(null);
    this.nuevo = { nombreCompleto: '', correo: '', telefono: '', rol: 'MESERO', passwordTemporal: '' };
    this.mostrarFormulario.set(true);
  }

  cancelar(): void {
    this.mostrarFormulario.set(false);
  }

  guardar(): void {
    this.error.set(null);
    this.cargando.set(true);
    this.usuarioService.crear(this.nuevo).subscribe({
      next: () => {
        this.cargando.set(false);
        this.mostrarFormulario.set(false);
        this.mensaje.set('Usuario creado exitosamente.');
        this.cargar();
      },
      error: (err) => {
        this.cargando.set(false);
        this.error.set(err?.error?.message ?? 'No se pudo crear el usuario.');
      },
    });
  }

  cambiarEstado(usuario: Usuario): void {
    this.usuarioService.actualizarEstado(usuario.id, !usuario.activo).subscribe({
      next: () => this.cargar(),
      error: (err) => this.error.set(err?.error?.message ?? 'No se pudo actualizar el usuario.'),
    });
  }
}

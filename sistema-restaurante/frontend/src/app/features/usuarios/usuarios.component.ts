import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UsuarioService } from '../../core/services/usuario.service';
import { CrearUsuarioRequest, Rol, Usuario } from '../../core/models/usuario.model';

/** CU02 Registrar Usuario (incluye horario de trabajo y salario) / Gestionar estado (Administrador). */
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

  nuevo: CrearUsuarioRequest = this.formularioVacio();
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
    this.nuevo = this.formularioVacio();
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

  horario(u: Usuario): string {
    if (!u.horaInicioTrabajo || !u.horaFinTrabajo) return '—';
    return `${u.horaInicioTrabajo.slice(0, 5)} - ${u.horaFinTrabajo.slice(0, 5)}`;
  }

  private formularioVacio(): CrearUsuarioRequest {
    return {
      nombreCompleto: '',
      correo: '',
      telefono: '',
      rol: 'MESERO',
      passwordTemporal: '',
      horaInicioTrabajo: '08:00',
      horaFinTrabajo: '17:00',
      salario: 0,
    };
  }
}

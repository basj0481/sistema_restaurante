import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MenuService } from '../../core/services/menu.service';
import { Categoria, Platillo, PlatilloRequest } from '../../core/models/menu.model';

/**
 * Mantenimiento del menú (categorías y platillos) que alimenta CU12 Ver Menú.
 * No corresponde a un CU formal del alcance actual (ver nota en MenuService del backend);
 * se incluye para que el sistema sea utilizable de punta a punta.
 */
@Component({
  selector: 'app-menu-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './menu-admin.component.html',
})
export class MenuAdminComponent implements OnInit {
  categorias = signal<Categoria[]>([]);
  platillos = signal<Platillo[]>([]);
  mostrarFormulario = signal(false);
  editandoId: number | null = null;
  cargando = signal(false);
  error = signal<string | null>(null);
  mensaje = signal<string | null>(null);

  form: PlatilloRequest = { nombre: '', categoriaId: 0, precio: 0, descripcion: '', fotoUrl: '' };

  constructor(private menuService: MenuService) {}

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.menuService.listarCategorias().subscribe((c) => this.categorias.set(c));
    this.menuService.listarPlatillos().subscribe((p) => this.platillos.set(p));
  }

  nuevoPlatillo(): void {
    this.editandoId = null;
    this.form = { nombre: '', categoriaId: this.categorias()[0]?.id ?? 0, precio: 0, descripcion: '', fotoUrl: '' };
    this.mostrarFormulario.set(true);
  }

  editar(p: Platillo): void {
    this.editandoId = p.id;
    this.form = { nombre: p.nombre, categoriaId: p.categoriaId, precio: p.precio, descripcion: p.descripcion, fotoUrl: p.fotoUrl };
    this.mostrarFormulario.set(true);
  }

  cancelar(): void {
    this.mostrarFormulario.set(false);
  }

  guardar(): void {
    this.cargando.set(true);
    const obs = this.editandoId
      ? this.menuService.actualizar(this.editandoId, this.form)
      : this.menuService.crear(this.form);
    obs.subscribe({
      next: () => {
        this.cargando.set(false);
        this.mostrarFormulario.set(false);
        this.mensaje.set(this.editandoId ? 'Platillo actualizado exitosamente.' : 'Platillo creado exitosamente.');
        this.cargar();
      },
      error: (err) => {
        this.cargando.set(false);
        this.error.set(err?.error?.message ?? 'No se pudo guardar el platillo.');
      },
    });
  }

  toggleDisponibilidad(p: Platillo): void {
    this.menuService.cambiarDisponibilidad(p.id, p.estado === 'AGOTADO').subscribe(() => this.cargar());
  }
}

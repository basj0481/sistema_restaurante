import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { InventarioService } from '../../core/services/inventario.service';
import { Insumo, MovimientoInventario } from '../../core/models/inventario.model';

/** CU05 Consultar Inventario (Administrador, solo lectura). */
@Component({
  selector: 'app-inventario-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="encabezado">
      <h2>Inventario</h2>
    </div>

    <div class="form-campo" style="max-width: 300px;">
      <label>Buscar insumo</label>
      <input type="text" [(ngModel)]="filtro" placeholder="Nombre del insumo..." />
    </div>

    <div class="card">
      <table class="tabla">
        <thead>
          <tr>
            <th>Nombre</th>
            <th>Unidad</th>
            <th>Stock actual</th>
            <th>Stock mínimo</th>
            <th>Estado</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          <tr *ngFor="let i of insumosFiltrados()">
            <td>{{ i.nombre }}</td>
            <td>{{ i.unidadMedida }}</td>
            <td>{{ i.stockActual }}</td>
            <td>{{ i.stockMinimo }}</td>
            <td>
              <span class="badge" [class.badge-rojo]="i.stockBajo" [class.badge-verde]="!i.stockBajo">
                {{ i.stockBajo ? 'Stock Bajo' : 'Normal' }}
              </span>
            </td>
            <td>
              <button class="btn btn-secundario" (click)="verDetalle(i)">Ver detalle</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="card" style="margin-top: 20px;" *ngIf="insumoSeleccionado()">
      <h3>Movimientos de "{{ insumoSeleccionado()!.nombre }}"</h3>
      <table class="tabla">
        <thead>
          <tr><th>Fecha</th><th>Tipo</th><th>Cantidad</th><th>Usuario</th><th>Motivo</th></tr>
        </thead>
        <tbody>
          <tr *ngFor="let m of movimientos()">
            <td>{{ m.fecha | date: 'short' }}</td>
            <td>{{ m.tipo }}</td>
            <td>{{ m.cantidad }}</td>
            <td>{{ m.usuario }}</td>
            <td>{{ m.motivo }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  `,
})
export class InventarioAdminComponent implements OnInit {
  insumos = signal<Insumo[]>([]);
  movimientos = signal<MovimientoInventario[]>([]);
  insumoSeleccionado = signal<Insumo | null>(null);
  filtro = '';

  constructor(private inventarioService: InventarioService) {}

  ngOnInit(): void {
    this.inventarioService.listar().subscribe((data) => this.insumos.set(data));
  }

  insumosFiltrados(): Insumo[] {
    const f = this.filtro.trim().toLowerCase();
    if (!f) return this.insumos();
    return this.insumos().filter((i) => i.nombre.toLowerCase().includes(f));
  }

  verDetalle(insumo: Insumo): void {
    this.insumoSeleccionado.set(insumo);
    this.inventarioService.historial(insumo.id).subscribe((data) => this.movimientos.set(data));
  }
}

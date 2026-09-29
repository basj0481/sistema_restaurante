import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { InventarioService } from '../../core/services/inventario.service';
import { Insumo } from '../../core/models/inventario.model';

/** CU11 Agregar Productos al Inventario (Cocina). */
@Component({
  selector: 'app-inventario-cocina',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './inventario-cocina.component.html',
})
export class InventarioCocinaComponent implements OnInit {
  insumos = signal<Insumo[]>([]);
  accion = signal<'ENTRADA' | 'NUEVO' | null>(null);
  cargando = signal(false);
  error = signal<string | null>(null);
  mensaje = signal<string | null>(null);

  // FA01 - registrar entrada de un insumo existente
  entradaInsumoId: number | null = null;
  entradaCantidad: number | null = null;
  entradaMotivo = '';

  // FA02 - crear un nuevo insumo
  nuevoNombre = '';
  nuevaUnidad = '';
  nuevoStockInicial: number | null = null;
  nuevoStockMinimo: number | null = null;

  constructor(private inventarioService: InventarioService) {}

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.inventarioService.listar().subscribe((data) => this.insumos.set(data));
  }

  mostrarEntrada(): void {
    this.error.set(null);
    this.mensaje.set(null);
    this.entradaInsumoId = null;
    this.entradaCantidad = null;
    this.entradaMotivo = '';
    this.accion.set('ENTRADA');
  }

  mostrarNuevo(): void {
    this.error.set(null);
    this.mensaje.set(null);
    this.nuevoNombre = '';
    this.nuevaUnidad = '';
    this.nuevoStockInicial = null;
    this.nuevoStockMinimo = null;
    this.accion.set('NUEVO');
  }

  cancelar(): void {
    this.accion.set(null);
  }

  guardarEntrada(): void {
    if (!this.entradaInsumoId || !this.entradaCantidad) return;
    this.cargando.set(true);
    this.inventarioService
      .registrarEntrada({ insumoId: this.entradaInsumoId, cantidad: this.entradaCantidad, motivo: this.entradaMotivo })
      .subscribe({
        next: () => {
          this.cargando.set(false);
          this.accion.set(null);
          this.mensaje.set('Entrada de inventario registrada exitosamente.');
          this.cargar();
        },
        error: (err) => {
          this.cargando.set(false);
          this.error.set(err?.error?.message ?? 'No se pudo registrar la entrada.');
        },
      });
  }

  guardarNuevoInsumo(): void {
    if (!this.nuevoNombre || !this.nuevaUnidad || this.nuevoStockInicial === null || this.nuevoStockMinimo === null) return;
    this.cargando.set(true);
    this.inventarioService
      .crearInsumo({
        nombre: this.nuevoNombre,
        unidadMedida: this.nuevaUnidad,
        stockInicial: this.nuevoStockInicial,
        stockMinimo: this.nuevoStockMinimo,
      })
      .subscribe({
        next: () => {
          this.cargando.set(false);
          this.accion.set(null);
          this.mensaje.set('Insumo actualizado exitosamente.');
          this.cargar();
        },
        error: (err) => {
          this.cargando.set(false);
          this.error.set(err?.error?.message ?? 'No se pudo crear el insumo.');
        },
      });
  }
}

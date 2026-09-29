import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BitacoraService } from '../../core/services/bitacora.service';
import { RegistroBitacora } from '../../core/models/bitacora.model';

/** CU04 Consultar Bitácora del Sistema (Administrador). */
@Component({
  selector: 'app-bitacora',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './bitacora.component.html',
})
export class BitacoraComponent implements OnInit {
  registros = signal<RegistroBitacora[]>([]);
  tipo: '' | 'TRANSACCION' | 'USUARIO' = '';
  desde = '';
  hasta = '';
  error = signal<string | null>(null);
  cargando = signal(false);

  constructor(private bitacoraService: BitacoraService) {}

  ngOnInit(): void {
    this.buscar();
  }

  buscar(): void {
    this.error.set(null);
    this.cargando.set(true);
    const filtros: any = {};
    if (this.tipo) filtros.tipo = this.tipo;
    if (this.desde) filtros.desde = `${this.desde}T00:00:00`;
    if (this.hasta) filtros.hasta = `${this.hasta}T23:59:59`;

    this.bitacoraService.consultar(filtros).subscribe({
      next: (data) => {
        this.cargando.set(false);
        this.registros.set(data);
      },
      error: (err) => {
        this.cargando.set(false);
        this.error.set(err?.error?.message ?? 'El rango de fechas seleccionado no es válido.');
      },
    });
  }

  limpiarFiltros(): void {
    this.tipo = '';
    this.desde = '';
    this.hasta = '';
    this.buscar();
  }

  exportar(): void {
    const contenido = JSON.stringify(this.registros(), null, 2);
    const blob = new Blob([contenido], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'bitacora.json';
    a.click();
    URL.revokeObjectURL(url);
  }
}

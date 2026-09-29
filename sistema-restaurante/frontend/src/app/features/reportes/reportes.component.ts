import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReporteService } from '../../core/services/reporte.service';
import { ReporteVentas } from '../../core/models/reporte.model';

/** CU06 Generar Reporte de Ventas (Administrador). Solo considera pedidos cobrados (Ver CU08). */
@Component({
  selector: 'app-reportes',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reportes.component.html',
})
export class ReportesComponent {
  desde = this.hace30Dias();
  hasta = this.hoy();
  reporte = signal<ReporteVentas | null>(null);
  cargando = signal(false);
  error = signal<string | null>(null);

  constructor(private reporteService: ReporteService) {}

  generar(): void {
    this.error.set(null);
    this.cargando.set(true);
    const desdeIso = `${this.desde}T00:00:00`;
    const hastaIso = `${this.hasta}T23:59:59`;
    this.reporteService.ventas(desdeIso, hastaIso).subscribe({
      next: (r) => {
        this.cargando.set(false);
        this.reporte.set(r);
      },
      error: (err) => {
        this.cargando.set(false);
        // RN AN02 No.13
        this.error.set(err?.error?.message ?? 'El rango de fechas seleccionado para el reporte no es válido.');
      },
    });
  }

  exportar(): void {
    const r = this.reporte();
    if (!r) return;
    const contenido = JSON.stringify(r, null, 2);
    const blob = new Blob([contenido], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `reporte-ventas-${this.desde}-a-${this.hasta}.json`;
    a.click();
    URL.revokeObjectURL(url);
  }

  private hoy(): string {
    return new Date().toISOString().slice(0, 10);
  }

  private hace30Dias(): string {
    const d = new Date();
    d.setDate(d.getDate() - 30);
    return d.toISOString().slice(0, 10);
  }
}

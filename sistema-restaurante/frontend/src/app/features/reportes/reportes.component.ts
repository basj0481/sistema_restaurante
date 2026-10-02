import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';
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

  /** CU06 FA03 - Exportar (solo formato PDF). */
  exportar(): void {
    const r = this.reporte();
    if (!r) return;

    const doc = new jsPDF();
    let y = 16;

    doc.setFontSize(16);
    doc.text('Reporte de Ventas', 14, y);
    y += 8;
    doc.setFontSize(10);
    doc.setTextColor(100);
    doc.text(`Período: ${this.desde} a ${this.hasta}`, 14, y);
    y += 10;

    doc.setTextColor(0);
    doc.setFontSize(12);
    doc.text(`Total de ventas: Q${r.totalVentas}`, 14, y); y += 7;
    doc.text(`Pedidos cobrados: ${r.cantidadPedidos}`, 14, y); y += 7;
    doc.text(`Efectivo: Q${r.totalEfectivo}    Tarjeta: Q${r.totalTarjeta}`, 14, y); y += 10;

    autoTable(doc, {
      startY: y,
      head: [['Platillo', 'Cantidad', 'Total']],
      body: r.platillosMasVendidos.map((p) => [p.platillo, String(p.cantidadVendida), `Q${p.totalGenerado}`]),
      headStyles: { fillColor: [122, 59, 18] },
      margin: { left: 14, right: 14 },
    });

    const siguienteY = (doc as any).lastAutoTable.finalY + 10;
    autoTable(doc, {
      startY: siguienteY,
      head: [['Mesero', 'Pedidos', 'Total vendido']],
      body: r.ventasPorMesero.map((v) => [v.mesero, String(v.cantidadPedidos), `Q${v.totalVendido}`]),
      headStyles: { fillColor: [122, 59, 18] },
      margin: { left: 14, right: 14 },
    });

    doc.save(`reporte-ventas-${this.desde}-a-${this.hasta}.pdf`);
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

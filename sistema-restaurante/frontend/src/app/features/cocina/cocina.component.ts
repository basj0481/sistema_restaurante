import { Component, OnInit, OnDestroy, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CocinaService } from '../../core/services/cocina.service';
import { Pedido } from '../../core/models/pedido.model';

/** CU09 Recibir Pedido (boton "Recibir") y CU10 Marcar Pedido Listo (boton "Listo"). */
@Component({
  selector: 'app-cocina',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './cocina.component.html',
  styleUrl: './cocina.component.css',
})
export class CocinaComponent implements OnInit, OnDestroy {
  pedidos = signal<Pedido[]>([]);
  procesando = signal<number | null>(null);
  error = signal<string | null>(null);
  private intervalo: any;

  constructor(private cocinaService: CocinaService) {}

  ngOnInit(): void {
    this.cargar();
    // CU09/CU10: la pantalla de Cocina debe actualizarse en tiempo real (aprox. via sondeo)
    this.intervalo = setInterval(() => this.cargar(), 4000);
  }

  ngOnDestroy(): void {
    clearInterval(this.intervalo);
  }

  cargar(): void {
    this.cocinaService.cola().subscribe((data) => this.pedidos.set(data));
  }

  recibir(p: Pedido): void {
    this.procesando.set(p.id);
    this.cocinaService.recibir(p.id).subscribe({
      next: () => { this.procesando.set(null); this.cargar(); },
      error: (err) => { this.procesando.set(null); this.error.set(err?.error?.message ?? 'No se pudo recibir el pedido.'); },
    });
  }

  marcarListo(p: Pedido): void {
    this.procesando.set(p.id);
    this.cocinaService.marcarListo(p.id).subscribe({
      next: () => { this.procesando.set(null); this.cargar(); },
      error: (err) => { this.procesando.set(null); this.error.set(err?.error?.message ?? 'No se pudo marcar el pedido como listo.'); },
    });
  }

  minutosTranscurridos(fecha: string): number {
    return Math.floor((Date.now() - new Date(fecha).getTime()) / 60000);
  }
}

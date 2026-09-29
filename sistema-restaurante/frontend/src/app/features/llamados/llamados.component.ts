import { Component, OnInit, OnDestroy, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LlamadoService } from '../../core/services/llamado.service';
import { Llamado } from '../../core/models/llamado.model';

/** CU13 Llamar al Mesero - lado del Mesero: ver y atender llamados pendientes. */
@Component({
  selector: 'app-llamados',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="encabezado">
      <h2>Llamados de Mesas</h2>
    </div>

    <div class="card" *ngIf="llamados().length === 0">
      <p style="color:#999; margin:0;">No hay llamados pendientes en este momento.</p>
    </div>

    <div class="tarjetas">
      <div class="card llamado" *ngFor="let l of llamados()">
        <div class="mesa">Mesa {{ l.numeroMesa }}</div>
        <div class="hora">{{ l.fechaCreacion | date: 'shortTime' }}</div>
        <button class="btn btn-exito" (click)="atender(l)">Marcar Atendido</button>
      </div>
    </div>
  `,
  styles: [`
    .tarjetas { display: flex; gap: 16px; flex-wrap: wrap; }
    .llamado { width: 180px; text-align: center; }
    .llamado .mesa { font-size: 22px; font-weight: 700; color: var(--color-primario); }
    .llamado .hora { font-size: 12px; color: #7a6a55; margin-bottom: 10px; }
  `],
})
export class LlamadosComponent implements OnInit, OnDestroy {
  llamados = signal<Llamado[]>([]);
  private intervalo: any;

  constructor(private llamadoService: LlamadoService) {}

  ngOnInit(): void {
    this.cargar();
    // CU13: la notificacion debe reflejarse en tiempo real (aprox. via sondeo periodico)
    this.intervalo = setInterval(() => this.cargar(), 4000);
  }

  ngOnDestroy(): void {
    clearInterval(this.intervalo);
  }

  cargar(): void {
    this.llamadoService.pendientes().subscribe((data) => this.llamados.set(data));
  }

  atender(l: Llamado): void {
    this.llamadoService.atender(l.id).subscribe(() => this.cargar());
  }
}

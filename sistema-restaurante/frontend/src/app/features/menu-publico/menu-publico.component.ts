import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { PublicoService } from '../../core/services/publico.service';
import { Categoria, Platillo } from '../../core/models/menu.model';

/**
 * CU12 Ver Menú (vía Código QR) y CU13 Llamar al Mesero (vía Código QR).
 * Sin autenticación: el Cliente llega aquí escaneando el QR de su mesa,
 * que codifica la URL /menu/{codigoQr}.
 */
@Component({
  selector: 'app-menu-publico',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './menu-publico.component.html',
  styleUrl: './menu-publico.component.css',
})
export class MenuPublicoComponent implements OnInit {
  codigoQr = '';
  numeroMesa = signal<number | null>(null);
  categorias = signal<Categoria[]>([]);
  platillos = signal<Platillo[]>([]);
  cargando = signal(true);
  error = signal<string | null>(null);

  llamadoEnviado = signal(false);
  enviandoLlamado = signal(false);
  errorLlamado = signal<string | null>(null);

  constructor(private route: ActivatedRoute, private publicoService: PublicoService) {}

  ngOnInit(): void {
    this.codigoQr = this.route.snapshot.paramMap.get('codigoQr') ?? '';
    this.publicoService.verMenu(this.codigoQr).subscribe({
      next: (res) => {
        this.cargando.set(false);
        this.numeroMesa.set(res.numeroMesa);
        this.categorias.set(res.categorias);
        this.platillos.set(res.platillos);
      },
      error: () => {
        this.cargando.set(false);
        this.error.set('El código QR no es válido o la mesa no está activa.');
      },
    });
  }

  platillosDe(categoriaId: number): Platillo[] {
    return this.platillos().filter((p) => p.categoriaId === categoriaId);
  }

  // CU13 Llamar al Mesero
  llamarMesero(): void {
    this.errorLlamado.set(null);
    this.enviandoLlamado.set(true);
    this.publicoService.llamarMesero(this.codigoQr).subscribe({
      next: () => {
        this.enviandoLlamado.set(false);
        this.llamadoEnviado.set(true);
        setTimeout(() => this.llamadoEnviado.set(false), 15000);
      },
      error: (err) => {
        this.enviandoLlamado.set(false);
        this.errorLlamado.set(err?.error?.message ?? 'No se pudo enviar el llamado. Intenta de nuevo.');
      },
    });
  }
}

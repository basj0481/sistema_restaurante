import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { PublicoService } from '../../core/services/publico.service';

/**
 * CU12 Ver Menú (vía Código QR) y CU13 Llamar al Mesero (vía Código QR).
 * Sin autenticación: el Cliente llega aquí escaneando el QR de su mesa,
 * que codifica la URL /menu/{codigoQr}. El menú se muestra como PDF.
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
  menuDisponible = signal(false);
  urlPdfSegura = signal<SafeResourceUrl | null>(null);
  cargando = signal(true);
  error = signal<string | null>(null);

  llamadoEnviado = signal(false);
  enviandoLlamado = signal(false);
  errorLlamado = signal<string | null>(null);

  constructor(
    private route: ActivatedRoute,
    private publicoService: PublicoService,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit(): void {
    this.codigoQr = this.route.snapshot.paramMap.get('codigoQr') ?? '';
    this.publicoService.verMenu(this.codigoQr).subscribe({
      next: (res) => {
        this.cargando.set(false);
        this.numeroMesa.set(res.numeroMesa);
        this.menuDisponible.set(res.menuDisponible);
        if (res.menuDisponible) {
          this.urlPdfSegura.set(this.sanitizer.bypassSecurityTrustResourceUrl(this.publicoService.urlMenuPdf()));
        }
      },
      error: () => {
        this.cargando.set(false);
        this.error.set('El código QR no es válido o la mesa no está activa.');
      },
    });
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

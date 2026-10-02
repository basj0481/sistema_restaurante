import { Component, OnInit, OnDestroy, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PedidoService } from '../../core/services/pedido.service';
import { MenuService } from '../../core/services/menu.service';
import { Pedido, PedidoItemRequest, TipoPedido, MetodoPago } from '../../core/models/pedido.model';
import { Platillo, Categoria } from '../../core/models/menu.model';

/** CU07 Generar Pedido y CU08 Cobrar Cuenta (Mesero). */
@Component({
  selector: 'app-pedidos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './pedidos.component.html',
})
export class PedidosComponent implements OnInit, OnDestroy {
  pedidos = signal<Pedido[]>([]);
  platillos = signal<Platillo[]>([]);
  categorias = signal<Categoria[]>([]);

  mostrarNuevoPedido = signal(false);
  tipo: TipoPedido = 'MESA';
  numeroMesa: number | null = null;
  clienteNombre = '';
  clienteTelefono = '';
  carrito: { platillo: Platillo; cantidad: number; notas: string }[] = [];

  pedidoACobrar = signal<Pedido | null>(null);
  metodoPago: MetodoPago = 'EFECTIVO';
  montoRecibido: number | null = null;

  cargando = signal(false);
  error = signal<string | null>(null);
  mensaje = signal<string | null>(null);

  private intervalo: any;

  constructor(private pedidoService: PedidoService, private menuService: MenuService) {}

  ngOnInit(): void {
    this.cargarPedidos();
    this.menuService.listarPlatillos().subscribe((p) => this.platillos.set(p));
    this.menuService.listarCategorias().subscribe((c) => this.categorias.set(c));
    // Actualizacion casi en tiempo real del estado de los pedidos (CU09/CU10 los cambian desde Cocina)
    this.intervalo = setInterval(() => this.cargarPedidos(), 5000);
  }

  ngOnDestroy(): void {
    clearInterval(this.intervalo);
  }

  cargarPedidos(): void {
    this.pedidoService.misPedidos().subscribe((data) => this.pedidos.set(data));
  }

  // ---------- CU07 Generar Pedido ----------

  abrirNuevoPedido(): void {
    this.error.set(null);
    this.tipo = 'MESA';
    this.numeroMesa = null;
    this.clienteNombre = '';
    this.clienteTelefono = '';
    this.carrito = [];
    this.mostrarNuevoPedido.set(true);
  }

  agregarAlCarrito(platillo: Platillo): void {
    const existente = this.carrito.find((c) => c.platillo.id === platillo.id);
    if (existente) existente.cantidad++;
    else this.carrito.push({ platillo, cantidad: 1, notas: '' });
  }

  quitarDelCarrito(index: number): void {
    this.carrito.splice(index, 1);
  }

  totalCarrito(): number {
    return this.carrito.reduce((acc, c) => acc + c.platillo.precio * c.cantidad, 0);
  }

  enviarPedido(): void {
    if (this.carrito.length === 0) return;
    this.error.set(null);
    this.cargando.set(true);

    const items: PedidoItemRequest[] = this.carrito.map((c) => ({
      platilloId: c.platillo.id,
      cantidad: c.cantidad,
      notas: c.notas || undefined,
    }));

    this.pedidoService
      .crear({
        tipo: this.tipo,
        numeroMesa: this.tipo === 'MESA' ? this.numeroMesa ?? undefined : undefined,
        clienteNombre: this.tipo === 'PARA_LLEVAR' ? this.clienteNombre : undefined,
        clienteTelefono: this.tipo === 'PARA_LLEVAR' ? this.clienteTelefono : undefined,
        items,
      })
      .subscribe({
        next: () => {
          this.cargando.set(false);
          this.mostrarNuevoPedido.set(false);
          this.mensaje.set('Pedido enviado a cocina exitosamente.');
          this.cargarPedidos();
        },
        error: (err) => {
          this.cargando.set(false);
          this.error.set(err?.error?.message ?? 'No se pudo enviar el pedido.');
        },
      });
  }

  cancelarNuevoPedido(): void {
    this.mostrarNuevoPedido.set(false);
  }

  // ---------- CU08 Cobrar Cuenta ----------

  abrirCobro(pedido: Pedido): void {
    this.error.set(null);
    this.metodoPago = 'EFECTIVO';
    this.montoRecibido = null;
    this.pedidoACobrar.set(pedido);
  }

  cambio(): number {
    const p = this.pedidoACobrar();
    if (!p || this.montoRecibido === null) return 0;
    return Math.max(0, this.montoRecibido - p.total);
  }

  confirmarCobro(): void {
    const p = this.pedidoACobrar();
    if (!p) return;
    this.error.set(null);
    this.cargando.set(true);

    this.pedidoService
      .cobrar(p.id, {
        metodoPago: this.metodoPago,
        montoRecibido: this.metodoPago === 'EFECTIVO' ? this.montoRecibido ?? undefined : undefined,
      })
      .subscribe({
        next: (pago) => {
          this.cargando.set(false);
          this.pedidoACobrar.set(null);
          // CU08: comprobante asociado con la API de la SAT
          this.mensaje.set(
            `Cuenta cobrada exitosamente. Comprobante ${pago.numeroComprobante} — Autorización SAT: ${pago.numeroAutorizacionSat}`
          );
          this.cargarPedidos();
        },
        error: (err) => {
          this.cargando.set(false);
          this.error.set(err?.error?.message ?? 'No se pudo cobrar la cuenta.');
        },
      });
  }

  cancelarCobro(): void {
    this.pedidoACobrar.set(null);
  }

  puedeCobrarse(p: Pedido): boolean {
    return p.estado === 'LISTO' || p.estado === 'ENTREGADO';
  }

  colorEstado(estado: string): string {
    switch (estado) {
      case 'ENVIADO': return 'badge-gris';
      case 'EN_PREPARACION': return 'badge-naranja';
      case 'LISTO': return 'badge-verde';
      case 'ENTREGADO': return 'badge-azul';
      case 'COBRADO': return 'badge-verde';
      case 'CANCELADO': return 'badge-rojo';
      default: return 'badge-gris';
    }
  }
}

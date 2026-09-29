export type TipoPedido = "MESA" | "PARA_LLEVAR";
export type EstadoPedido = "ENVIADO" | "EN_PREPARACION" | "LISTO" | "ENTREGADO" | "COBRADO" | "CANCELADO";
export type MetodoPago = "EFECTIVO" | "TARJETA";

export interface PedidoItemRequest {
  platilloId: number;
  cantidad: number;
  notas?: string;
}

export interface CrearPedidoRequest {
  tipo: TipoPedido;
  numeroMesa?: number;
  clienteNombre?: string;
  clienteTelefono?: string;
  items: PedidoItemRequest[];
}

export interface PedidoItem {
  id: number;
  platilloId: number;
  platillo: string;
  cantidad: number;
  precioUnitario: number;
  notas?: string;
  cancelado: boolean;
}

export interface Pedido {
  id: number;
  tipo: TipoPedido;
  numeroMesa?: number;
  clienteNombre?: string;
  mesero: string;
  estado: EstadoPedido;
  subtotal: number;
  total: number;
  fechaCreacion: string;
  fechaRecibido?: string;
  fechaListo?: string;
  items: PedidoItem[];
}

export interface CobrarCuentaRequest {
  metodoPago: MetodoPago;
  montoRecibido?: number;
}

export interface Pago {
  id: number;
  pedidoId: number;
  metodoPago: MetodoPago;
  total: number;
  montoRecibido?: number;
  cambio?: number;
  numeroComprobante: string;
  fecha: string;
}

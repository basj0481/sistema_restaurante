export interface PlatilloVendido {
  platillo: string;
  cantidadVendida: number;
  totalGenerado: number;
}

export interface VentaPorMesero {
  mesero: string;
  cantidadPedidos: number;
  totalVendido: number;
}

export interface ReporteVentas {
  totalVentas: number;
  cantidadPedidos: number;
  totalEfectivo: number;
  totalTarjeta: number;
  platillosMasVendidos: PlatilloVendido[];
  ventasPorMesero: VentaPorMesero[];
}

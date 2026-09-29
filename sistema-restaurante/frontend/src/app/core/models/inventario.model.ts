export interface Insumo {
  id: number;
  nombre: string;
  unidadMedida: string;
  stockActual: number;
  stockMinimo: number;
  stockBajo: boolean;
}

export interface MovimientoInventario {
  id: number;
  tipo: string;
  cantidad: number;
  usuario: string;
  motivo?: string;
  fecha: string;
}

export interface RegistrarEntradaRequest {
  insumoId: number;
  cantidad: number;
  motivo?: string;
}

export interface CrearInsumoRequest {
  nombre: string;
  unidadMedida: string;
  stockInicial: number;
  stockMinimo: number;
}

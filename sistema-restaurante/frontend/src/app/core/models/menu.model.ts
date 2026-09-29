export interface Categoria {
  id: number;
  nombre: string;
  orden: number;
}

export interface RecetaItemRequest {
  insumoId: number;
  cantidad: number;
}

export interface Platillo {
  id: number;
  nombre: string;
  categoriaId: number;
  categoriaNombre: string;
  precio: number;
  descripcion?: string;
  fotoUrl?: string;
  estado: "DISPONIBLE" | "AGOTADO";
}

export interface PlatilloRequest {
  nombre: string;
  categoriaId: number;
  precio: number;
  descripcion?: string;
  fotoUrl?: string;
  receta?: RecetaItemRequest[];
}

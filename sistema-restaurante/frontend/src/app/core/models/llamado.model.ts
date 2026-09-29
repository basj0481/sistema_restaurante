export interface Llamado {
  id: number;
  numeroMesa: number;
  estado: "PENDIENTE" | "ATENDIDO";
  fechaCreacion: string;
}

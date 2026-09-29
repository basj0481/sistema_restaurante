export interface RegistroBitacora {
  id: number;
  tipo: "TRANSACCION" | "USUARIO";
  usuario: string;
  accion: string;
  detalle?: string;
  fecha: string;
}

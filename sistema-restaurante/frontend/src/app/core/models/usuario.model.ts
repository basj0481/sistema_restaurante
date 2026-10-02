export type Rol = "ADMINISTRADOR" | "MESERO" | "COCINA";

export interface Usuario {
  id: number;
  nombreCompleto: string;
  correo: string;
  telefono?: string;
  rol: Rol;
  activo: boolean;
  fechaCreacion: string;
  horaInicioTrabajo?: string; // "HH:mm:ss"
  horaFinTrabajo?: string;
  salario?: number;
}

export interface CrearUsuarioRequest {
  nombreCompleto: string;
  correo: string;
  telefono?: string;
  rol: Rol;
  passwordTemporal: string;
  horaInicioTrabajo: string; // "HH:mm"
  horaFinTrabajo: string;
  salario: number;
}

export interface LoginResponse {
  token: string;
  id: number;
  nombreCompleto: string;
  correo: string;
  rol: Rol;
  debeCambiarPassword: boolean;
}

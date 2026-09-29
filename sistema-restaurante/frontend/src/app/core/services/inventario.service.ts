import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { environment } from "../../../environments/environment";
import { CrearInsumoRequest, Insumo, MovimientoInventario, RegistrarEntradaRequest } from "../models/inventario.model";

/** CU05 Consultar Inventario (Admin, lectura) / CU11 Agregar Productos al Inventario (Cocina). */
@Injectable({ providedIn: "root" })
export class InventarioService {
  private base = `${environment.apiUrl}/inventario`;

  constructor(private http: HttpClient) {}

  listar(): Observable<Insumo[]> {
    return this.http.get<Insumo[]>(this.base);
  }

  historial(insumoId: number): Observable<MovimientoInventario[]> {
    return this.http.get<MovimientoInventario[]>(`${this.base}/${insumoId}/movimientos`);
  }

  registrarEntrada(request: RegistrarEntradaRequest): Observable<Insumo> {
    return this.http.post<Insumo>(`${this.base}/entrada`, request);
  }

  crearInsumo(request: CrearInsumoRequest): Observable<Insumo> {
    return this.http.post<Insumo>(this.base, request);
  }
}

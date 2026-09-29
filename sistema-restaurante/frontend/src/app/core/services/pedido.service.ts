import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { environment } from "../../../environments/environment";
import { CobrarCuentaRequest, CrearPedidoRequest, Pago, Pedido } from "../models/pedido.model";

/** CU07 Generar Pedido, CU08 Cobrar Cuenta (Mesero). */
@Injectable({ providedIn: "root" })
export class PedidoService {
  private base = `${environment.apiUrl}/pedidos`;

  constructor(private http: HttpClient) {}

  crear(request: CrearPedidoRequest): Observable<Pedido> {
    return this.http.post<Pedido>(this.base, request);
  }

  misPedidos(): Observable<Pedido[]> {
    return this.http.get<Pedido[]>(`${this.base}/mios`);
  }

  obtener(id: number): Observable<Pedido> {
    return this.http.get<Pedido>(`${this.base}/${id}`);
  }

  cancelar(id: number, motivo: string): Observable<void> {
    return this.http.patch<void>(`${this.base}/${id}/cancelar`, { motivo });
  }

  cobrar(id: number, request: CobrarCuentaRequest): Observable<Pago> {
    return this.http.post<Pago>(`${this.base}/${id}/cobrar`, request);
  }
}

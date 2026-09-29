import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { environment } from "../../../environments/environment";
import { Pedido } from "../models/pedido.model";

/** CU09 Recibir Pedido, CU10 Marcar Pedido Listo (Cocina). */
@Injectable({ providedIn: "root" })
export class CocinaService {
  private base = `${environment.apiUrl}/cocina`;

  constructor(private http: HttpClient) {}

  cola(): Observable<Pedido[]> {
    return this.http.get<Pedido[]>(`${this.base}/pedidos`);
  }

  recibir(id: number): Observable<Pedido> {
    return this.http.patch<Pedido>(`${this.base}/pedidos/${id}/recibir`, {});
  }

  marcarListo(id: number): Observable<Pedido> {
    return this.http.patch<Pedido>(`${this.base}/pedidos/${id}/listo`, {});
  }
}

import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { environment } from "../../../environments/environment";
import { Categoria, Platillo } from "../models/menu.model";

export interface MenuPublicoResponse {
  numeroMesa: number;
  categorias: Categoria[];
  platillos: Platillo[];
}

/** CU12 Ver Menu, CU13 Llamar al Mesero - sin autenticacion, vía codigo QR. */
@Injectable({ providedIn: "root" })
export class PublicoService {
  private base = `${environment.apiUrl}/publico`;

  constructor(private http: HttpClient) {}

  verMenu(codigoQr: string): Observable<MenuPublicoResponse> {
    return this.http.get<MenuPublicoResponse>(`${this.base}/mesas/${codigoQr}/menu`);
  }

  llamarMesero(codigoQr: string): Observable<any> {
    return this.http.post(`${this.base}/mesas/${codigoQr}/llamar-mesero`, {});
  }
}

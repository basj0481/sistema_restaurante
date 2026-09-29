import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { environment } from "../../../environments/environment";
import { RegistroBitacora } from "../models/bitacora.model";

/** CU04 Consultar Bitacora del Sistema (Administrador). */
@Injectable({ providedIn: "root" })
export class BitacoraService {
  private base = `${environment.apiUrl}/bitacora`;

  constructor(private http: HttpClient) {}

  consultar(filtros: { tipo?: string; desde?: string; hasta?: string }): Observable<RegistroBitacora[]> {
    let params: any = {};
    if (filtros.tipo) params.tipo = filtros.tipo;
    if (filtros.desde) params.desde = filtros.desde;
    if (filtros.hasta) params.hasta = filtros.hasta;
    return this.http.get<RegistroBitacora[]>(this.base, { params });
  }
}

import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { environment } from "../../../environments/environment";
import { ReporteVentas } from "../models/reporte.model";

/** CU06 Generar Reporte de Ventas (Administrador). */
@Injectable({ providedIn: "root" })
export class ReporteService {
  private base = `${environment.apiUrl}/reportes`;

  constructor(private http: HttpClient) {}

  ventas(desde: string, hasta: string): Observable<ReporteVentas> {
    return this.http.get<ReporteVentas>(`${this.base}/ventas`, { params: { desde, hasta } });
  }
}

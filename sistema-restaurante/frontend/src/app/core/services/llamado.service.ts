import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { environment } from "../../../environments/environment";
import { Llamado } from "../models/llamado.model";

/** CU13 Llamar al Mesero - lado del Mesero. */
@Injectable({ providedIn: "root" })
export class LlamadoService {
  private base = `${environment.apiUrl}/llamados`;

  constructor(private http: HttpClient) {}

  pendientes(): Observable<Llamado[]> {
    return this.http.get<Llamado[]>(this.base);
  }

  atender(id: number): Observable<Llamado> {
    return this.http.patch<Llamado>(`${this.base}/${id}/atender`, {});
  }
}

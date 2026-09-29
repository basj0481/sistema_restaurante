import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { environment } from "../../../environments/environment";
import { Categoria, Platillo, PlatilloRequest } from "../models/menu.model";

/** Mantenimiento de categorias/platillos (Administrador) que alimenta CU12 Ver Menu. */
@Injectable({ providedIn: "root" })
export class MenuService {
  private base = `${environment.apiUrl}/menu`;

  constructor(private http: HttpClient) {}

  listarCategorias(): Observable<Categoria[]> {
    return this.http.get<Categoria[]>(`${this.base}/categorias`);
  }

  listarPlatillos(): Observable<Platillo[]> {
    return this.http.get<Platillo[]>(`${this.base}/platillos`);
  }

  crear(request: PlatilloRequest): Observable<Platillo> {
    return this.http.post<Platillo>(`${this.base}/platillos`, request);
  }

  actualizar(id: number, request: PlatilloRequest): Observable<Platillo> {
    return this.http.put<Platillo>(`${this.base}/platillos/${id}`, request);
  }

  cambiarDisponibilidad(id: number, disponible: boolean): Observable<Platillo> {
    return this.http.patch<Platillo>(`${this.base}/platillos/${id}/disponibilidad`, { disponible });
  }
}

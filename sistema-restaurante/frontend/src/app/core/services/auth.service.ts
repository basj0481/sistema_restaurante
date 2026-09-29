import { Injectable, signal } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Router } from "@angular/router";
import { Observable, tap } from "rxjs";
import { environment } from "../../../environments/environment";
import { LoginResponse, Rol } from "../models/usuario.model";

const STORAGE_KEY = "restaurante_sesion";

@Injectable({ providedIn: "root" })
export class AuthService {
  sesion = signal<LoginResponse | null>(this.leerSesionGuardada());

  constructor(private http: HttpClient, private router: Router) {}

  login(correo: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${environment.apiUrl}/auth/login`, { correo, password }).pipe(
      tap((res) => {
        localStorage.setItem(STORAGE_KEY, JSON.stringify(res));
        this.sesion.set(res);
      })
    );
  }

  solicitarRecuperacion(correo: string): Observable<any> {
    return this.http.post(`${environment.apiUrl}/auth/forgot-password`, { correo });
  }

  restablecerPassword(token: string, nuevaPassword: string): Observable<any> {
    return this.http.post(`${environment.apiUrl}/auth/reset-password`, { token, nuevaPassword });
  }

  logout(): void {
    localStorage.removeItem(STORAGE_KEY);
    this.sesion.set(null);
    this.router.navigate(["/login"]);
  }

  get token(): string | null {
    return this.sesion()?.token ?? null;
  }

  get rol(): Rol | null {
    return this.sesion()?.rol ?? null;
  }

  estaAutenticado(): boolean {
    return !!this.sesion();
  }

  private leerSesionGuardada(): LoginResponse | null {
    const raw = localStorage.getItem(STORAGE_KEY);
    return raw ? (JSON.parse(raw) as LoginResponse) : null;
  }
}

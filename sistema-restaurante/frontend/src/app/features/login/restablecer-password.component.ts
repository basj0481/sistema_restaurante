import { Component, signal } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { ActivatedRoute, Router, RouterLink } from "@angular/router";
import { AuthService } from "../../core/services/auth.service";

/** CU03 Recuperar Contrasena - paso 2: definir la nueva contrasena a partir del enlace recibido. */
@Component({
  selector: "app-restablecer-password",
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="pantalla-login">
      <form class="card tarjeta-login" (ngSubmit)="restablecer()">
        <h1>Restablecer Contraseña</h1>
        <p class="subtitulo">Mínimo 8 caracteres, con mayúscula, número y carácter especial.</p>

        <div class="mensaje-error" *ngIf="error()">{{ error() }}</div>
        <div class="mensaje-exito" *ngIf="mensaje()">{{ mensaje() }}</div>

        <div class="form-campo">
          <label>Nueva Contraseña</label>
          <input type="password" name="nueva" [(ngModel)]="nuevaPassword" required />
        </div>
        <div class="form-campo">
          <label>Confirmar Contraseña</label>
          <input type="password" name="confirmar" [(ngModel)]="confirmar" required />
        </div>

        <button class="btn btn-primario" type="submit" [disabled]="cargando() || !nuevaPassword || nuevaPassword !== confirmar">
          {{ cargando() ? "Guardando..." : "Restablecer Contraseña" }}
        </button>
        <a class="enlace-olvido" routerLink="/login">Volver al inicio de sesión</a>
      </form>
    </div>
  `,
  styleUrl: "./login.component.css",
})
export class RestablecerPasswordComponent {
  token = "";
  nuevaPassword = "";
  confirmar = "";
  cargando = signal(false);
  error = signal<string | null>(null);
  mensaje = signal<string | null>(null);

  constructor(private auth: AuthService, private route: ActivatedRoute, private router: Router) {
    this.token = this.route.snapshot.queryParamMap.get("token") ?? "";
  }

  restablecer(): void {
    this.error.set(null);
    this.cargando.set(true);
    this.auth.restablecerPassword(this.token, this.nuevaPassword).subscribe({
      next: () => {
        this.cargando.set(false);
        this.mensaje.set("Contraseña actualizada exitosamente.");
        setTimeout(() => this.router.navigate(["/login"]), 1500);
      },
      error: (err) => {
        this.cargando.set(false);
        this.error.set(err?.error?.message ?? "El enlace de recuperación de contraseña ha expirado.");
      },
    });
  }
}

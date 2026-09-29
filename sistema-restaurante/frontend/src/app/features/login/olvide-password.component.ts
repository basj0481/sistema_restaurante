import { Component, signal } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { RouterLink } from "@angular/router";
import { AuthService } from "../../core/services/auth.service";

/** CU03 Recuperar Contrasena - paso 1: solicitar el enlace por correo. */
@Component({
  selector: "app-olvide-password",
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="pantalla-login">
      <form class="card tarjeta-login" (ngSubmit)="enviar()">
        <h1>Recuperar Contraseña</h1>
        <p class="subtitulo">Ingresa tu correo electrónico y te enviaremos un enlace de recuperación.</p>

        <div class="mensaje-error" *ngIf="error()">{{ error() }}</div>
        <div class="mensaje-exito" *ngIf="mensaje()">{{ mensaje() }}</div>

        <div class="form-campo">
          <label>Correo electrónico</label>
          <input type="email" name="correo" [(ngModel)]="correo" required />
        </div>

        <button class="btn btn-primario" type="submit" [disabled]="cargando() || !correo">
          {{ cargando() ? "Enviando..." : "Enviar" }}
        </button>
        <a class="enlace-olvido" routerLink="/login">Cancelar</a>
      </form>
    </div>
  `,
  styleUrl: "./login.component.css",
})
export class OlvidePasswordComponent {
  correo = "";
  cargando = signal(false);
  error = signal<string | null>(null);
  mensaje = signal<string | null>(null);

  constructor(private auth: AuthService) {}

  enviar(): void {
    this.error.set(null);
    this.mensaje.set(null);
    this.cargando.set(true);
    this.auth.solicitarRecuperacion(this.correo).subscribe({
      next: () => {
        this.cargando.set(false);
        this.mensaje.set("Correo de recuperación de contraseña enviado exitosamente.");
      },
      error: (err) => {
        this.cargando.set(false);
        this.error.set(err?.error?.message ?? "El correo no se encuentra registrado.");
      },
    });
  }
}

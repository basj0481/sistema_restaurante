import { Component, signal } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { Router, RouterLink } from "@angular/router";
import { AuthService } from "../../core/services/auth.service";

/** CU00 Portal de Acceso / CU01 Iniciar Sesion. */
@Component({
  selector: "app-login",
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: "./login.component.html",
  styleUrl: "./login.component.css",
})
export class LoginComponent {
  correo = "";
  password = "";
  cargando = signal(false);
  error = signal<string | null>(null);

  constructor(private auth: AuthService, private router: Router) {}

  ingresar(): void {
    this.error.set(null);
    this.cargando.set(true);
    this.auth.login(this.correo, this.password).subscribe({
      next: (res) => {
        this.cargando.set(false);
        if (res.rol === "ADMINISTRADOR") this.router.navigate(["/admin"]);
        else if (res.rol === "MESERO") this.router.navigate(["/mesero"]);
        else this.router.navigate(["/cocina"]);
      },
      error: (err) => {
        this.cargando.set(false);
        // FA01/FA02 - RN AN02
        this.error.set(err?.error?.message ?? "Usuario o contrasena incorrectos.");
      },
    });
  }
}

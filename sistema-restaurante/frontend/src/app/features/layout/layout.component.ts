import { Component } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterLink, RouterLinkActive, RouterOutlet } from "@angular/router";
import { AuthService } from "../../core/services/auth.service";

/** Shell comun para Administrador / Mesero / Cocina, con navegacion segun el rol (CU01 paso 7). */
@Component({
  selector: "app-layout",
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: "./layout.component.html",
  styleUrl: "./layout.component.css",
})
export class LayoutComponent {
  constructor(public auth: AuthService) {}

  cerrarSesion(): void {
    this.auth.logout();
  }
}

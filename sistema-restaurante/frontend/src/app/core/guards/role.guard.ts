import { inject } from "@angular/core";
import { CanActivateFn, Router } from "@angular/router";
import { AuthService } from "../services/auth.service";
import { Rol } from "../models/usuario.model";

/** Uso en rutas: canActivate: [roleGuard], data: { roles: ["ADMINISTRADOR"] } */
export const roleGuard: CanActivateFn = (route) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  const rolesPermitidos = route.data["roles"] as Rol[] | undefined;
  const rolActual = auth.rol;

  if (!rolActual) {
    router.navigate(["/login"]);
    return false;
  }
  if (rolesPermitidos && !rolesPermitidos.includes(rolActual)) {
    router.navigate(["/"]);
    return false;
  }
  return true;
};

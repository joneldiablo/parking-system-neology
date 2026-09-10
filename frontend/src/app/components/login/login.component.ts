import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styles: ['']
})
export class LoginComponent {

  username = '';
  password = '';
  error = '';
  loading = false;

  constructor(private auth: AuthService, private router: Router) { }

  login(): void {
    if (!this.username.trim() || !this.password) {
      this.error = 'Ingresa usuario y contraseña';
      return;
    }
    this.loading = true;
    this.error = '';
    this.auth.login(this.username.trim(), this.password).subscribe({
      next: () => this.router.navigate(['/vehiculos']),
      error: (err) => {
        this.loading = false;
        this.error = err.status === 401
          ? 'Credenciales inválidas o usuario inactivo'
          : 'Error al conectar con el servidor';
      }
    });
  }
}
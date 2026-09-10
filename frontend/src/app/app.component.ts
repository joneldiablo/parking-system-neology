import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from './services/auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent {

  constructor(private auth: AuthService, private router: Router) { }

  get logged(): boolean {
    return this.auth.isAuthenticated();
  }

  get username(): string {
    return this.auth.getUser()?.username || '';
  }

  get isSuperAdmin(): boolean {
    return this.auth.isSuperAdmin();
  }

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}
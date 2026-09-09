import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  template: `
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary mb-4">
      <div class="container">
        <a class="navbar-brand" routerLink="/">Parking System</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse"
                data-bs-target="#navMenu" aria-controls="navMenu"
                aria-expanded="false" aria-label="Toggle navigation">
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navMenu">
          <ul class="navbar-nav me-auto">
            <li class="nav-item">
              <a class="nav-link" routerLink="/vehiculos" routerLinkActive="active">Vehículos</a>
            </li>
            <li class="nav-item">
              <a class="nav-link" routerLink="/estancias" routerLinkActive="active">Estancias</a>
            </li>
            <li class="nav-item">
              <a class="nav-link" routerLink="/registro" routerLinkActive="active">Registrar</a>
            </li>
            <li class="nav-item">
              <a class="nav-link" routerLink="/pagos" routerLinkActive="active">Pagos</a>
            </li>
          </ul>
        </div>
      </div>
    </nav>

    <main class="container py-3">
      <router-outlet></router-outlet>
    </main>
  `,
  styles: []
})
export class AppComponent {
  title = 'Parking System';
}
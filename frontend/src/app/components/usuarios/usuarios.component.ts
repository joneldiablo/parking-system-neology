import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Usuario, UsuariosService } from '../../services/usuarios.service';

@Component({
  selector: 'app-usuarios',
  templateUrl: './usuarios.component.html'
})
export class UsuariosComponent implements OnInit {

  usuarios: Usuario[] = [];
  mostrarFormulario = false;
  nuevo = { username: '', password: '', activo: true };
  editando: { id: number; password: string; activo: boolean } | null = null;
  cargando = false;
  msg: { tipo: 'success' | 'danger'; text: string } | null = null;

  constructor(private service: UsuariosService, private auth: AuthService) { }

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.cargando = true;
    this.service.listar().subscribe({
      next: (usuarios) => {
        this.usuarios = usuarios;
        this.cargando = false;
      },
      error: () => {
        this.cargando = false;
        this.notif('danger', 'No se pudo cargar la lista de administradores');
      }
    });
  }

  crear(): void {
    if (!this.nuevo.username.trim() || !this.nuevo.password) {
      this.notif('danger', 'Usuario y contraseña son obligatorios');
      return;
    }
    this.service.crear({
      username: this.nuevo.username.trim(),
      password: this.nuevo.password,
      activo: this.nuevo.activo
    }).subscribe({
      next: () => {
        this.cargar();
        this.mostrarFormulario = false;
        this.nuevo = { username: '', password: '', activo: true };
        this.notif('success', 'Administrador creado');
      },
      error: (e) => this.notif('danger', e.error?.mensaje || 'No se pudo crear el administrador')
    });
  }

  guardarCambios(id: number): void {
    const dto: { password?: string; activo: boolean } = { activo: this.editando!.activo };
    if (this.editando!.password) {
      dto.password = this.editando!.password;
    }
    this.service.actualizar(id, dto).subscribe({
      next: () => {
        this.cargar();
        this.editando = null;
        this.notif('success', 'Cambios guardados');
      },
      error: (e) => this.notif('danger', e.error?.mensaje || 'No se pudieron guardar los cambios')
    });
  }

  eliminar(usuario: Usuario): void {
    if (!confirm(`¿Eliminar al administrador "${usuario.username}"? Esta acción no se puede deshacer.`)) {
      return;
    }
    this.service.eliminar(usuario.id).subscribe({
      next: () => {
        this.cargar();
        this.notif('success', 'Administrador eliminado');
      },
      error: (e) => this.notif('danger', e.error?.mensaje || 'No se pudo eliminar')
    });
  }

  esYo(usuario: Usuario): boolean {
    return usuario.username === this.auth.getUser()?.username;
  }

  private notif(tipo: 'success' | 'danger', text: string): void {
    this.msg = { tipo, text };
    setTimeout(() => this.msg = null, 4000);
  }
}
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface Usuario {
  id: number;
  username: string;
  rol: string;
  activo: boolean;
}

export interface CrearUsuario {
  username: string;
  password: string;
  activo: boolean;
}

export interface ActualizarUsuario {
  password?: string;
  activo?: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class UsuariosService {

  private readonly url = '/api/usuarios';

  constructor(private http: HttpClient) { }

  listar(): Observable<Usuario[]> {
    return this.http.get<Usuario[]>(this.url);
  }

  crear(dto: CrearUsuario): Observable<any> {
    return this.http.post(this.url, dto);
  }

  actualizar(id: number, dto: ActualizarUsuario): Observable<any> {
    return this.http.put(`${this.url}/${id}`, dto);
  }

  eliminar(id: number): Observable<any> {
    return this.http.delete(`${this.url}/${id}`);
  }
}
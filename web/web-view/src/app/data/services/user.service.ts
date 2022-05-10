import { Injectable } from '@angular/core'
import { HttpClient } from '@angular/common/http'

import { Observable } from 'rxjs'

import { environment } from 'src/environments/environment'

@Injectable()
export class UserService {
  private url = environment.api

  public constructor (private readonly http: HttpClient) { }

  public loadUsers (): Observable<any[]> {
    return this.http.get<any[]>(`${this.url}/users/`)
  }

  public deleteUserById (userId: string): Observable<any> {
    return this.http.delete<any>(`${this.url}/users/${userId}`)
  }

  public addUser (user: any): Observable<any> {
    return this.http.post<any>(`${this.url}/users`, user)
  }

  public updateUserById (userId: string, body: any): Observable<any> {
    return this.http.patch<any>(`${this.url}/users/${userId}`, body)
  }
}
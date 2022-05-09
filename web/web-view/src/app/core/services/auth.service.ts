import { Injectable } from '@angular/core'
import { HttpClient } from '@angular/common/http'

import { Observable, BehaviorSubject } from 'rxjs'
import { tap } from 'rxjs/operators'
import { JwtHelperService } from '@auth0/angular-jwt'

import { environment } from 'src/environments/environment'
import { SignInData } from 'src/app/data/dtos/signin-data.model'
import { Token } from 'src/app/data/dtos/token.model'

@Injectable()
export class AuthService {
  private url = environment.api
  private jwt = new JwtHelperService()

  private isLoggedIn$ = new BehaviorSubject<boolean>(this.validateToken())
  
  constructor (private readonly http: HttpClient) { }

  public isLoggedIn (): Observable<boolean> {
    return this.isLoggedIn$.asObservable()
  }

  public signIn (credentials: SignInData): Observable<any> {
    return this.http
      .post<Token>(`${this.url}/login`, credentials)
      .pipe(tap(({ token }) => {
        this.setToken(token)
        this.isLoggedIn$.next(true)
      }))
  }

  public signOut (): void {
    this.destroyToken()
    this.isLoggedIn$.next(false)
  }

  public loadLoggedUser (): Observable<any> {
    return this.http.get<any>(`${this.url}/me`)
  }

  public getToken (): any {
    return localStorage.getItem('token')
  }

  private validateToken (): boolean {
    const token = this.getToken()
    return !this.jwt.isTokenExpired(token)
  }

  private setToken (token: string): void {
    localStorage.setItem('token', token)
  }

  private destroyToken (): void {
    localStorage.removeItem('token')
  }
}
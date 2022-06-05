import { Injectable } from '@angular/core'
import { HttpClient } from '@angular/common/http'

import { Observable } from 'rxjs'

import { environment } from 'src/environments/environment'
import { ShoppingCartAddItem } from '../dtos/shoppingCartAddItem.model'

@Injectable()
export class ShoppingCartService {
  private url = environment.api

  public constructor (private readonly http: HttpClient) { }

  public addItemIntoShoppingCart (requestPayload: ShoppingCartAddItem): Observable<any[]> {
    return this.http.post<any[]>(`${this.url}/shoppingCart`, requestPayload);
  }

  public loadShoppingCart (): Observable<any[]> {
    return this.http.get<any[]>(`${this.url}/shoppingCart`)
  }

  public deleteShoppingCartItem (itemId: any): Observable<any[]> {
    return this.http.delete<any[]>(`${this.url}/shoppingCart/items/${itemId}`)
  }

  public updateShoppingCartItem (itemId: any, patchPayload: any): Observable<any[]> {
    return this.http.patch<any[]>(`${this.url}/shoppingCart/items/${itemId}`, patchPayload)
  }
}
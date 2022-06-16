import { Injectable } from '@angular/core'
import { HttpClient, HttpParams } from '@angular/common/http'

import { Observable } from 'rxjs'

import { environment } from 'src/environments/environment'
import { ShoppingCartAddItem } from '../dtos/shoppingCartAddItem.model'

@Injectable()
export class OrderService {
  private url = environment.api

  public constructor (private readonly http: HttpClient) { }

  public loadOrders (): Observable<any[]> {
    return this.http.get<any[]>(`${this.url}/orders/me`);
  }

  public loadAllOrders (params: HttpParams): Observable<any[]> {
    return this.http.get<any[]>(`${this.url}/orders`, {params: params});
  }

  public confirmOrderPayment (orderId: any): Observable<any> {
    return this.http.post<any>(`${this.url}/orders/${orderId}`, null);
  }

  public updateOrder (orderId: any, patchPayload: any): Observable<any[]> {
    return this.http.patch<any>(`${this.url}/orders/${orderId}`, patchPayload);
  }
}
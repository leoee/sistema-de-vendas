import { Injectable } from '@angular/core'
import { HttpClient } from '@angular/common/http'

import { Observable } from 'rxjs'

import { environment } from 'src/environments/environment'
import { ShoppingCartAddItem } from '../dtos/shoppingCartAddItem.model'

@Injectable()
export class OrderService {
  private url = environment.api

  public constructor (private readonly http: HttpClient) { }

  public loadOrders (): Observable<any[]> {
    return this.http.get<any[]>(`${this.url}/orders/me`)
  }
}
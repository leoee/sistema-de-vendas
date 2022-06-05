import { Injectable } from '@angular/core'
import { HttpClient, HttpParams } from '@angular/common/http'

import { Observable } from 'rxjs'

import { environment } from 'src/environments/environment'
import { Item } from '../dtos/item.model'

@Injectable()
export class ItemService {
  private url = environment.api

  public constructor (private readonly http: HttpClient) { }

  public loadItems (): Observable<any[]> {
    return this.http.get<any[]>(`${this.url}/items`)
  }

  public loadItemById (itemId: any): Observable<any[]> {
    return this.http.get<any[]>(`${this.url}/items/${itemId}`)
  }

  public loadItemByName (name: string): Observable<any[]> {
    let params = new HttpParams().set('name', name);
    return this.http.get<any[]>(`${this.url}/items`, {params: params});
  }

  public loadItemByParams(params: HttpParams): Observable<any[]> {
    return this.http.get<any[]>(`${this.url}/items`, {params: params});
  }

  public createItem (item: Item): Observable<any[]> {
    return this.http.post<any>(`${this.url}/items`, item);
  }

  public deleteItem (itemId: string): Observable<any[]> {
    return this.http.delete<any>(`${this.url}/items/${itemId}`);
  }

  public updateItem (itemId: string, data: any): Observable<any[]> {
    return this.http.patch<any>(`${this.url}/items/${itemId}`, data);
  }
}
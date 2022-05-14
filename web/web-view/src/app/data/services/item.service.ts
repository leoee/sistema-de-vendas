import { Injectable } from '@angular/core'
import { HttpClient } from '@angular/common/http'

import { Observable } from 'rxjs'

import { environment } from 'src/environments/environment'

@Injectable()
export class ItemService {
  private url = environment.api

  public constructor (private readonly http: HttpClient) { }

  public loadItems (): Observable<any[]> {
    return this.http.get<any[]>(`${this.url}/items`)
  }
}
import { Injectable } from '@angular/core'

import { ToastrService } from 'ngx-toastr'

@Injectable()
export class NotificationService {
  public constructor (private readonly toastr: ToastrService) { }

  public success (title: string, message: string): void {
    this.toastr.success(message, title)
  }

  public show (title: string, message: string): void {
    this.toastr.show(message, title)
  }

  public error (title: string, message = 'Tente novamente'): void {
    this.toastr.error(message, title)
  }
}
import { Injectable, OnDestroy } from '@angular/core'
import { CanActivate, Router } from '@angular/router'

import { Subject } from 'rxjs'
import { takeUntil } from 'rxjs/operators'
import { UserService } from 'src/app/data/services/user.service'

import { AuthService } from '../services/auth.service'

@Injectable()
export class AuthRoleGuard implements CanActivate, OnDestroy {

  private unsub$ = new Subject<void>()

  constructor (
    private readonly authService: AuthService,
    private readonly router: Router
  ) { }

  public ngOnDestroy (): void {
    this.unsub$.next()
    this.unsub$.complete()
  }

  public canActivate (): boolean {
    this.authService.loadLoggedUser()
      .pipe(takeUntil(this.unsub$))
      .subscribe(user => {
        if (user?.role != 'ADMIN') {
          this.router.navigate(['/home'])
          return false
        }
      })

    return true
  }
}
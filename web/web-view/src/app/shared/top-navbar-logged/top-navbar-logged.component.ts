import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { Subject } from 'rxjs';
import { Observable } from 'rxjs/internal/Observable';
import { map } from 'rxjs/internal/operators/map';
import { AuthService } from 'src/app/core/services/auth.service';

@Component({
  selector: 'app-top-navbar-logged',
  templateUrl: './top-navbar-logged.component.html',
  styleUrls: ['./top-navbar-logged.component.css']
})
export class TopNavbarLoggedComponent implements OnInit, OnDestroy {
  @Input() public user$!: Observable<any>
  private unsub$ = new Subject<void>();

  constructor(private readonly authService: AuthService) { }

  ngOnInit(): void {
    this.getUserCaseLogged();
  }

  public ngOnDestroy (): void {
    this.unsub$.next()
    this.unsub$.complete()
  }

  private getUserCaseLogged(): any {
    this.user$ = this.authService
    .loadLoggedUser()
    .pipe(map(user => user))
  }

  public signOut() {
    this.authService.signOut();
  }

}

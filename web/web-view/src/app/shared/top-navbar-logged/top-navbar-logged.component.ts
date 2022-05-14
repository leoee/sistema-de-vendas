import { HttpErrorResponse } from '@angular/common/http';
import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, Subject } from 'rxjs';
import { map } from 'rxjs/internal/operators/map';
import { takeUntil } from 'rxjs/operators';
import { AuthService } from 'src/app/core/services/auth.service';
import { NotificationService } from 'src/app/core/services/notification.service';
import { UserService } from 'src/app/data/services/user.service';

@Component({
  selector: 'app-top-navbar-logged',
  templateUrl: './top-navbar-logged.component.html',
  styleUrls: ['./top-navbar-logged.component.css']
})
export class TopNavbarLoggedComponent implements OnInit, OnDestroy {
  public user$!: Observable<any>
  public form!: FormGroup

  public error$ = new Subject<boolean>();

  private unsub$ = new Subject<void>();

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly authService: AuthService,
    private readonly modal: NgbModal,
    private readonly userService: UserService,
    private readonly notificationService: NotificationService) { }

  ngOnInit(): void {
    this.initializeForms();
    this.getUserCaseLogged();
  }

  public ngOnDestroy (): void {
    this.unsub$.next()
    this.unsub$.complete()
  }

  private initializeForms (): void {
    this.form = this.formBuilder.group({
      name: ['', []],
      cellphoneNumber: ['', []],
      password: ['', [Validators.minLength(7)]],
      address: this.formBuilder.group({
        place: ['', []],
        zip: ['', []],
        city: ['', []],
      })
    })
  }

  private getUserCaseLogged(): any {
    this.user$ = this.authService
    .loadLoggedUser()
    .pipe(map(user => user))
  }

  public signOut() {
    this.authService.signOut();
  }

  public openEditUserModal (content: any): void {
    this.modal.open(content, { centered: true })
      .result.then(
        () => {},
        () => this.modal.dismissAll()
      )
  }

  private loadUser() {
    this.user$ = this.authService
      .loadLoggedUser()
      .pipe(map(user => user))
  }

  public updateUser(user: any, updatedData: any): any {
    for (let property in updatedData.address) {
      if (!updatedData.address[property]) {
        delete updatedData.address[property]
      }
    }

    for (let property in updatedData) {
      if (!updatedData[property] || Object.keys(updatedData[property]).length === 0) {
        delete updatedData[property]
      }
    }


    this.userService.updateUserById(user.id, updatedData)
    .pipe(takeUntil(this.unsub$))
    .subscribe(
      () => {
        this.notificationService.success('Atenção!', 'Informações salvas com sucesso!')
        this.loadUser()
      },
      (error: HttpErrorResponse) => {
        if (error.status == 400) {
          this.notificationService.error('Hmn..!', 'Parece que algum campo não foi preenchido corretamente.')
        } else {
          this.notificationService.error('Hmn..!', 'Algo deu errado. Tente novamente.')
        }
        this.error$.next(true)
      }
    )
  }

}

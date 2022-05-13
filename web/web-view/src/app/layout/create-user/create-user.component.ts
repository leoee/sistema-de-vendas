import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/app/core/services/notification.service';
import { UserService } from 'src/app/data/services/user.service';

@Component({
  selector: 'app-create-user',
  templateUrl: './create-user.component.html',
  styleUrls: ['./create-user.component.css']
})
export class CreateUserComponent implements OnInit, OnDestroy {
  public userData!: FormGroup
  private unsub$ = new Subject<void>();

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly userService: UserService,
    private readonly router: Router,
    private readonly notificationService: NotificationService) { }

  public ngOnInit(): void {
    this.initializeForm();
  }

  public ngOnDestroy (): void {
    this.unsub$.next()
    this.unsub$.complete()
  }

  private initializeForm(): void {
    this.userData = this.formBuilder.group({
      name: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(7)]],
      cpf: ['', [Validators.required]],
      cellphoneNumber: ['', [Validators.required]],
      address: this.formBuilder.group({
        place: ['', [Validators.required]],
        zipCode: ['', [Validators.required]],
        city: ['', [Validators.required]],
      })

    })
  }

  public createUser(userData: any): void {
    if (!this.userData.valid) {
      this.notificationService.error('Erro', 'Dados para cadastro não estão validos.');
      return;
    }

    this.userService.addUser(userData)
      .pipe(takeUntil(this.unsub$))
      .subscribe(
        () => {
          this.notificationService.success('', `Usuário ${userData.name} cadastrado com sucesso!!`)
          this.router.navigate(['/home'])
        },
        ({ error: httpError }: HttpErrorResponse) => {
          this.notificationService.error(`Parece que algo deu errado com seu cadastro... ${httpError.msg}`)
        }
      )
  }

}

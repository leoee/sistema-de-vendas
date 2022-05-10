import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
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
    private readonly userService: UserService) { }

  public ngOnInit(): void {
    this.initializeForm();
  }

  public ngOnDestroy (): void {
    this.unsub$.next()
    this.unsub$.complete()
  }

  private initializeForm(): void {
    this.userData = this.formBuilder.group({
      fullName: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(7)]],
      cpf: ['', [Validators.required]],
      cellphone: ['', [Validators.required]],
      address: ['', [Validators.required]],
      zipCode: ['', [Validators.required]],
      city: ['', [Validators.required]],
    })
  }

  public createUser(userData: any): void {
    console.log(userData);
    this.userService.addUser(userData)
      .pipe(takeUntil(this.unsub$))
      .subscribe(
        () => {
          console.log('created');
        },
        ({ error: httpError }: HttpErrorResponse) => {
          console.log(httpError);
          console.log('error');
        }
      )
  }

}

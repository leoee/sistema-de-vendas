import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

import {NgbCarouselConfig} from '@ng-bootstrap/ng-bootstrap';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AuthService } from 'src/app/core/services/auth.service';
import { NotificationService } from 'src/app/core/services/notification.service';
import { SignInData } from 'src/app/data/dtos/signin-data.model';

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.css'],
  providers: [NgbCarouselConfig]
})
export class MainComponent implements OnInit, OnDestroy {
  public userData!: FormGroup

  public showNavigationArrows = true;
  public showNavigationIndicators = false;
  public images = [1055, 194, 368].map((n) => `https://picsum.photos/id/${n}/900/500`);

  private unsub$ = new Subject<void>();

  public constructor(
    private config: NgbCarouselConfig,
    private readonly formBuilder: FormBuilder,
    private readonly authService: AuthService,
    private readonly notificationService: NotificationService) {
    config.showNavigationArrows = true;
    config.showNavigationIndicators = true;
  }

  public ngOnInit(): void {
    this.initializeForm();
  }

  public ngOnDestroy (): void {
    this.unsub$.next()
    this.unsub$.complete()
  }

  public signIn (credentials: SignInData): void {
    console.log(credentials);
    this.authService.signIn(credentials)
      .pipe(takeUntil(this.unsub$))
      .subscribe(
        () => {
          this.notificationService.success('Ei!', 'Login efetuado com sucesso!')
        },
        ({ error: httpError }: HttpErrorResponse) => {
          this.notificationService.error('Ops!', 'E-mail ou senha inválida.')
        }
      )
  }

  private initializeForm(): void {
    this.userData = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(7)]]
    })
  }

}

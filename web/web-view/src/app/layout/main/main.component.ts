import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import {NgbCarouselConfig} from '@ng-bootstrap/ng-bootstrap';
import { Observable, Subject } from 'rxjs';
import { map, takeUntil } from 'rxjs/operators';
import { AuthService } from 'src/app/core/services/auth.service';
import { NotificationService } from 'src/app/core/services/notification.service';
import { Item } from 'src/app/data/dtos/item.model';
import { SignInData } from 'src/app/data/dtos/signin-data.model';
import { ItemService } from 'src/app/data/services/item.service';

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.css'],
  providers: [NgbCarouselConfig]
})
export class MainComponent implements OnInit, OnDestroy {
  public userData!: FormGroup
  public items$!: Observable<any>
  public images: Item[] = [];

  public showNavigationArrows = true;
  public showNavigationIndicators = false;

  private unsub$ = new Subject<void>();

  public constructor(
    public config: NgbCarouselConfig,
    private readonly formBuilder: FormBuilder,
    private readonly authService: AuthService,
    private readonly router: Router,
    private readonly notificationService: NotificationService,
    private readonly itemService: ItemService) {
    config.showNavigationArrows = false;
    config.showNavigationIndicators = false;
    config.interval = 5000;
    config.pauseOnHover = true;
  }

  public ngOnInit(): void {
    this.initializeForm();
    this.loadItems();
  }

  public ngOnDestroy (): void {
    this.unsub$.next()
    this.unsub$.complete()
  }

  public signIn (credentials: SignInData): void {
    if (!this.userData.valid) {
      this.notificationService.error('Ops!', 'E-mail ou senha inválida.')
      return;
    }

    this.authService.signIn(credentials)
      .pipe(takeUntil(this.unsub$))
      .subscribe(
        () => {
          this.notificationService.success('Ei!', 'Login efetuado com sucesso!')
          this.router.navigate(['/home-page'])
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

  private loadItems(): void {
    let that = this;

    this.items$ = this.itemService
    .loadItems()
    .pipe(map(items => items));
    
    this.items$.subscribe(function (item) {
      that.images?.push(item);
    })
  }
}

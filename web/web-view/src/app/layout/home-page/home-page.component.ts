import { HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { of, Subject } from 'rxjs';
import { Observable } from 'rxjs/internal/Observable';
import { catchError, map, takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/app/core/services/notification.service';
import { Item } from 'src/app/data/dtos/item.model';
import { ShoppingCartAddItem } from 'src/app/data/dtos/shoppingCartAddItem.model';
import { ItemService } from 'src/app/data/services/item.service';
import { ShoppingCartService } from 'src/app/data/services/shoppingCart.service';

@Component({
  selector: 'app-home-page',
  templateUrl: './home-page.component.html',
  styleUrls: ['./home-page.component.css']
})
export class HomePageComponent implements OnInit {
  public items$!: Observable<any>
  public item$!: Observable<any>
  private unsub$ = new Subject<void>();
  public form!: FormGroup
  public currentItemToAnalyze: Item;
  public currentItemToDelete: Item;
  public name: string;
  public minPrice: string;
  public maxPrice: string;
  public stockQuantity: string;

  public error$ = new Subject<boolean>();

  constructor(private readonly itemService: ItemService,
    private readonly formBuilder: FormBuilder,
    private readonly modal: NgbModal,
    private readonly notificationService: NotificationService,
    private readonly shoppingCartService: ShoppingCartService) { }

  public ngOnInit(): void {
    this.loadItems();
    this.initializeForms();
  }

  public ngOnDestroy (): void {
    this.unsub$.next()
    this.unsub$.complete()
  }

  private initializeForms (): void {
    this.form = this.formBuilder.group({
      stockQuantityAdded: ['1', [Validators.required]],
    })
  }

  private loadItems(): void {
    this.items$ = this.itemService
    .loadItems()
    .pipe(map(items => items));
  }

  public openViewItemModal (content: any, item: any): void {
    this.currentItemToAnalyze = item;
    this.modal.open(content, { centered: true })
      .result.then(
        () => {
        },
        () => this.modal.dismissAll()
      )
  }

  public filterItemsByParameter(): void {
    if (!this.name && !this.minPrice && !this.maxPrice && !this.stockQuantity) {
      this.loadItems();
    } else {
      let params = new HttpParams();
      this.buildRequestParameters().forEach((value, key) => {
        if (value) {
          params = params.set(key, value);
        }
      });
      this.items$ = this.itemService
      .loadItemByParams(params)
      .pipe(catchError(({ error: httpError }: HttpErrorResponse) => {
        this.error$.next(true)

        return of(null);
      }));
    }
  }

  private buildRequestParameters(): Map<string, string> {

    return new Map<string, string>([
      ["name", this.name],
      ["minPrice", this.minPrice],
      ["maxPrice", this.maxPrice],
      ["stockQuantity", this.stockQuantity]
    ]);
  }

  public addItemOnShoppingCart(item: any): void {
    const addItemPayload: ShoppingCartAddItem = new ShoppingCartAddItem();
    addItemPayload.itemId = this.currentItemToAnalyze.id;
    addItemPayload.amount = item.stockQuantityAdded;

    if (!this.validateItemToAdd(this.currentItemToAnalyze.stockQuantity, addItemPayload)) {
      return;
    }

    this.shoppingCartService.addItemIntoShoppingCart(addItemPayload)
    .pipe(takeUntil(this.unsub$))
    .subscribe(
      () => {
        this.notificationService.success('Que legal!', `Você adicionou ${addItemPayload.amount} ${this.currentItemToAnalyze.name} ao seu carrinho.`)
        this.modal.dismissAll();
      },
      (error: HttpErrorResponse) => {
        if (error.status == 403) {
          this.notificationService.error('Ops!', 'Você não pode executar essa ação.');
        } else {
          this.notificationService.error('Ops!', 'Certifique que o item já não encontra-se no carrinho ou tenha em estoque.');
        }
      }
    )
  }

  private validateItemToAdd(stockQuantity: any, payload: ShoppingCartAddItem): boolean {
    if (payload.amount > stockQuantity) {
      this.notificationService.error('Ops!', 'Não existem suficientes em estoque.');
      return false;
    }

    return true;
  }

}

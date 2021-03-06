import { HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, Subject } from 'rxjs';
import { map, takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/app/core/services/notification.service';
import { Item } from 'src/app/data/dtos/item.model';
import { ItemService } from 'src/app/data/services/item.service';
import { ShoppingCartService } from 'src/app/data/services/shoppingCart.service';


@Component({
  selector: 'app-shopping-cart',
  templateUrl: './shopping-cart.component.html',
  styleUrls: ['./shopping-cart.component.css']
})
export class ShoppingCartComponent implements OnInit {
  public items$!: Observable<any>
  public item$!: Observable<any>
  public shoppingCart$!: Observable<any>
  public currentItemToDelete: Item;
  public currentItemToEdit: any;
  private unsub$ = new Subject<void>();
  public name: string;
  public minPrice: string;
  public maxPrice: string;
  public stockQuantity: string;
  public form!: FormGroup
  public checkoutForm!: FormGroup

  public shoppingCartItems: Item[] = [];

  public error$ = new Subject<boolean>();

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly itemService: ItemService,
    private readonly modal: NgbModal,
    private readonly notificationService: NotificationService,
    private readonly shoppingCartService: ShoppingCartService) { }

  public ngOnInit(): void {
    this.loadShoppingCart();
    this.initializeForms();
  }

  private initializeForms (): void {
    this.form = this.formBuilder.group({
      amount: ['', [Validators.required]]
    });
    this.checkoutForm = this.formBuilder.group({
      notesForDelivery: [''],
      paymentMethod: ['', [Validators.required]]
    });
  }

  public ngOnDestroy (): void {
    this.unsub$.next()
    this.unsub$.complete()
  }

  private loadShoppingCart(): void {
    let that = this;
    this.shoppingCart$ = this.shoppingCartService
    .loadShoppingCart()
    .pipe(map(shoppingCart => shoppingCart));

    this.shoppingCart$.subscribe(function (item) {
      that.shoppingCartItems?.push(item);
    })
  }

  public openViewItemModal (content: any, item: any): void {
    this.item$ = this.itemService
    .loadItemById(item.itemId)
    .pipe(map(item => item));
    this.modal.open(content, { centered: true })
      .result.then(
        () => {
        },
        () => this.modal.dismissAll()
      )
  }

  public openDeleteShoppingCarttemModal (content: any, item: any): void {
    this.currentItemToDelete = item;
    this.modal.open(content, { centered: true })
      .result.then(
        () => {
          this.deleteItemOnShoppingCart(item)
        },
        () => this.modal.dismissAll()
      )
  }

  public openEditShoppingCartItemAmount (content: any, item: any): void {
    this.currentItemToEdit = item;
    this.modal.open(content, { centered: true })
      .result.then(
        () => {
        },
        () => this.modal.dismissAll()
      )
  }

  public openCheckoutShoppingCart (content: any): void {
    this.modal.open(content, { centered: true })
      .result.then(
        () => {
        },
        () => this.modal.dismissAll()
      )
  }

  public updateItemAmont(item: any): void {
    this.shoppingCartService.updateShoppingCartItem(this.currentItemToEdit.itemId, item)
    .pipe(takeUntil(this.unsub$))
    .subscribe(
      () => {
        this.notificationService.success('Aten????o!', this.currentItemToEdit.name + ' foi atualizado do seu carrinho.')
        this.loadShoppingCart()
      },
      (error: HttpErrorResponse) => {
        if (error.status == 403) {
          this.notificationService.error('Ops!', 'Voc?? n??o pode executar essa a????o.')
        } else {
          this.notificationService.error('Ops!', 'Voc?? est?? tentando uma atualiza????o inv??lida.')
        }
      }
    )
  }

  private deleteItemOnShoppingCart(item: any): void {
    this.shoppingCartService.deleteShoppingCartItem(item.itemId)
    .pipe(takeUntil(this.unsub$))
    .subscribe(
      () => {
        this.notificationService.success('Aten????o!', item.name + ' foi removido do seu carrinho.')
        this.loadShoppingCart()
      },
      (error: HttpErrorResponse) => {
        if (error.status == 403) {
          this.notificationService.error('Ops!', 'Voc?? n??o pode executar essa a????o.')
        } else {
          this.notificationService.error('Ops!', 'Algo deu errado. Tente novamente.')
        }
      }
    )
  }

  public checkoutShoppingCart(requestPayload: any): void {
    this.shoppingCartService.checkoutShoppingCart(requestPayload)
    .pipe(takeUntil(this.unsub$))
    .subscribe(
      () => {
        this.notificationService.success('Que legal!', 'Seu carrinho foi finalizado com sucesso. Agora basta ficar de olho no seus pedidos!!')
        this.loadShoppingCart()
      },
      (error: HttpErrorResponse) => {
        if (error.status == 403) {
          this.notificationService.error('Ops!', 'Voc?? n??o pode executar essa a????o.')
        } else {
          this.notificationService.error('Ops!', 'Selecione um m??todo de pagamento e certifique que seu carrinho n??o est?? vazio.')
        }
      }
    )
  }
}

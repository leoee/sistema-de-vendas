import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, Subject } from 'rxjs';
import { map, takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/app/core/services/notification.service';
import { Item } from 'src/app/data/dtos/item.model';
import { ItemService } from 'src/app/data/services/item.service';
import { OrderService } from 'src/app/data/services/order.service';
import { ShoppingCartService } from 'src/app/data/services/shoppingCart.service';

@Component({
  selector: 'app-my-orders',
  templateUrl: './my-orders.component.html',
  styleUrls: ['./my-orders.component.css']
})
export class MyOrdersComponent implements OnInit {
  public items$!: Observable<any>
  public item$!: Observable<any>
  public shoppingCart$!: Observable<any>
  public orders$!: Observable<any>
  public currentItemToDelete: Item;
  public currentItemToEdit: any;
  private unsub$ = new Subject<void>();
  public name: string;
  public minPrice: string;
  public maxPrice: string;
  public stockQuantity: string;
  public form!: FormGroup
  public checkoutForm!: FormGroup

  public mapOrderStatus = new Map<string, string>([
    ["WAITING_PAYMENT", "AGUARDANDO PAGAMENTO"],
    ["PAYMENT_CONFIRMED", "PAGAMENTO CONFIRMADO"],
    ["IN_TRANSIT", "EM TRANSITO"],
    ["DELIVERED", "ENTREGUE"],
    ["CANCELLED", "CANCELADO"]
  ]);

  public mapPaymentStatus = new Map<string, string>([
    ["WAITING_PAYMENT", "AGUARDANDO PAGAMENTO"],
    ["PAYMENT_CAPTURED", "PAGAMENTO CONFIRMADO"]
  ]);

  public mapPaymentMethods = new Map<string, string>([
    ["PIX_ONLINE", "PIX ONLINE"]
  ]);

  public shoppingCartItems: Item[] = [];

  public error$ = new Subject<boolean>();

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly itemService: ItemService,
    private readonly modal: NgbModal,
    private readonly notificationService: NotificationService,
    private readonly orderService: OrderService) { }

  public ngOnInit(): void {
    this.loadOrders();
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

  private loadOrders(): void {
    let that = this;
    this.orders$ = this.orderService
    .loadOrders()
    .pipe(map(orders => orders));
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
    /*this.shoppingCartService.updateShoppingCartItem(this.currentItemToEdit.itemId, item)
    .pipe(takeUntil(this.unsub$))
    .subscribe(
      () => {
        this.notificationService.success('Atenção!', this.currentItemToEdit.name + ' foi atualizado do seu carrinho.')
        this.loadShoppingCart()
      },
      (error: HttpErrorResponse) => {
        if (error.status == 403) {
          this.notificationService.error('Ops!', 'Você não pode executar essa ação.')
        } else {
          this.notificationService.error('Ops!', 'Você está tentando uma atualização inválida.')
        }
      }
    )*/
  }

  private deleteItemOnShoppingCart(item: any): void {
    /*this.shoppingCartService.deleteShoppingCartItem(item.itemId)
    .pipe(takeUntil(this.unsub$))
    .subscribe(
      () => {
        this.notificationService.success('Atenção!', item.name + ' foi removido do seu carrinho.')
        this.loadShoppingCart()
      },
      (error: HttpErrorResponse) => {
        if (error.status == 403) {
          this.notificationService.error('Ops!', 'Você não pode executar essa ação.')
        } else {
          this.notificationService.error('Ops!', 'Algo deu errado. Tente novamente.')
        }
      }
    )*/
  }

  public checkoutShoppingCart(requestPayload: any): void {
    /*this.shoppingCartService.checkoutShoppingCart(requestPayload)
    .pipe(takeUntil(this.unsub$))
    .subscribe(
      () => {
        this.notificationService.success('Que legal!', 'Seu carrinho foi finalizado com sucesso. Agora basta ficar de olho no seus pedidos!!')
        this.loadShoppingCart()
      },
      (error: HttpErrorResponse) => {
        if (error.status == 403) {
          this.notificationService.error('Ops!', 'Você não pode executar essa ação.')
        } else {
          this.notificationService.error('Ops!', 'Selecione um método de pagamento e certifique que seu carrinho não está vazio.')
        }
      }
    )*/
  }
}

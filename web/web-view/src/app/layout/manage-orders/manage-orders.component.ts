import { HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { of, Subject } from 'rxjs';
import { Observable } from 'rxjs/internal/Observable';
import { catchError, map, takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/app/core/services/notification.service';
import { OrderService } from 'src/app/data/services/order.service';

@Component({
  selector: 'app-manage-orders',
  templateUrl: './manage-orders.component.html',
  styleUrls: ['./manage-orders.component.css']
})
export class ManageOrdersComponent implements OnInit {
  public orders$!: Observable<any>
  private unsub$ = new Subject<void>();
  public currentOrderToEdit: any;
  public orderStatus: string;
  public id: string;
  public paymentStatus: string;
  public email: string;
  public form!: FormGroup

  public mapOrderStatus = new Map<string, string>([
    ["OPEN", "ABERTO"],
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

  public error$ = new Subject<boolean>();

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly modal: NgbModal,
    private readonly notificationService: NotificationService,
    private readonly orderService: OrderService) { }

  public ngOnInit(): void {
    this.loadOrders();
    this.initializeForms();
  }

  public ngOnDestroy (): void {
    this.unsub$.next()
    this.unsub$.complete()
  }

  private loadOrders(): void {
    this.orders$ = this.orderService
    .loadAllOrders(null)
    .pipe(map(orders => orders));
  }

  private initializeForms (): void {
    this.form = this.formBuilder.group({
      orderStatus: ['', [Validators.required]]
    });
  }

  public openViewModal (content: any, order: any): void {
    this.currentOrderToEdit = order;
    this.modal.open(content, { centered: true })
      .result.then(
        () => {
        },
        () => this.modal.dismissAll()
      )
  }

  public filterOrdersByParameter(): void {
    if (!this.orderStatus && !this.id && !this.paymentStatus && !this.email) {
      this.loadOrders();
    } else {
      let params = new HttpParams();
      this.buildRequestParameters().forEach((value, key) => {
        if (value) {
          params = params.set(key, value);
        }
      });
      this.orders$ = this.orderService
      .loadAllOrders(params)
      .pipe(catchError(({ error: httpError }: HttpErrorResponse) => {
        this.error$.next(true)

        return of(null);
      }));
    }
  }

  private buildRequestParameters(): Map<string, string> {

    return new Map<string, string>([
      ["id", this.id],
      ["orderStatus", this.orderStatus],
      ["paymentStatus", this.paymentStatus],
      ["email", this.email]
    ]);
  }

  public confirmPayment(order: any): void {
    this.orderService.confirmOrderPayment(order.id)
    .pipe(takeUntil(this.unsub$))
    .subscribe(
      () => {
        this.notificationService.success('Atenção!', `Você acabou de confirmar o pagamento do pedido ${order.id}`);
        this.loadOrders();
      },
      (error: HttpErrorResponse) => {
        if (error.status == 403) {
          this.notificationService.error('Ops!', 'Você não pode executar essa ação.')
        } else {
          this.notificationService.error('Ops!', 'Você está tentando uma atualização inválida.')
        }
      }
    )
  }

  public updateOrderStatus(orderStatus: any): void {
    this.orderService.updateOrder(this.currentOrderToEdit?.id, orderStatus)
    .pipe(takeUntil(this.unsub$))
    .subscribe(
      () => {
        this.notificationService.success('Atenção!', `Você acabou de atualizar o estado do pedido ${this.currentOrderToEdit?.id} para ${orderStatus}`);
        this.loadOrders();
      },
      (error: HttpErrorResponse) => {
        if (error.status == 403) {
          this.notificationService.error('Ops!', 'Você não pode executar essa ação.')
        } else {
          this.notificationService.error('Ops!', 'Você está tentando uma atualização inválida.')
        }
      }
    )
  }
}

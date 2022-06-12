import { Component, OnInit } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, Subject } from 'rxjs';
import { map } from 'rxjs/operators';
import { Item } from 'src/app/data/dtos/item.model';
import { OrderService } from 'src/app/data/services/order.service';

@Component({
  selector: 'app-my-orders',
  templateUrl: './my-orders.component.html',
  styleUrls: ['./my-orders.component.css']
})
export class MyOrdersComponent implements OnInit {
  public orders$!: Observable<any>
  private unsub$ = new Subject<void>();

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
    private readonly modal: NgbModal,
    private readonly orderService: OrderService) { }

  public ngOnInit(): void {
    this.loadOrders();
  }

  public ngOnDestroy (): void {
    this.unsub$.next()
    this.unsub$.complete()
  }

  private loadOrders(): void {
    this.orders$ = this.orderService
    .loadOrders()
    .pipe(map(orders => orders));
  }

  public openViewModal (content: any): void {
    this.modal.open(content, { centered: true })
      .result.then(
        () => {
        },
        () => this.modal.dismissAll()
      )
  }
}

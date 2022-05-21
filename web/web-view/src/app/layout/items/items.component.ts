import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, Subject } from 'rxjs';
import { filter, map, takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/app/core/services/notification.service';
import { Item } from 'src/app/data/dtos/item.model';
import { ItemService } from 'src/app/data/services/item.service';

@Component({
  selector: 'app-items',
  templateUrl: './items.component.html',
  styleUrls: ['./items.component.css']
})
export class ItemsComponent implements OnInit, OnDestroy {
  public items$!: Observable<any>
  public item$!: Observable<any>
  private unsub$ = new Subject<void>();
  public form!: FormGroup
  public currentItemToEdit: Item;
  public currentItemToDelete: Item;
  public itemName: string;

  public error$ = new Subject<boolean>();

  constructor(private readonly itemService: ItemService,
    private readonly formBuilder: FormBuilder,
    private readonly modal: NgbModal,
    private readonly notificationService: NotificationService) { }

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
      stockQuantity: ['', [Validators.required]],
      price: ['', [Validators.required]],
      description: ['', [Validators.required]],
      name: ['', [Validators.required]],
      imageUrl: ['', [Validators.required]],
      specification: ['', [Validators.required]],
    })
  }

  private loadItems(): void {
    this.items$ = this.itemService
    .loadItems()
    .pipe(map(items => items));
  }

  public openEditUserModal (content: any): void {
    this.modal.open(content, { centered: true })
      .result.then(
        () => {},
        () => this.modal.dismissAll()
      )
  }

  public createItem(item: any): void {
    this.itemService.createItem(item)
    .pipe(takeUntil(this.unsub$))
    .subscribe(
      () => {
        this.modal.dismissAll()
        this.form.reset()
        this.notificationService.success('Atenção!', 'Cadastro realizado com sucesso!')
        this.loadItems()
      },
      (error: HttpErrorResponse) => {
        if (error.status == 400) {
          this.notificationService.error('Hmn..!', 'Parece que algum campo não foi preenchido corretamente.')
        } else {
          this.notificationService.error('Hmn..!', 'Algo deu errado. Tente novamente.')
        }
      }
    )
  }

  public deleteItem(item: any): any {
    this.itemService.deleteItem(item.id)
    .pipe(takeUntil(this.unsub$))
    .subscribe(
      () => {
        this.notificationService.success('Atenção!', item.name + ' foi apagado.')
        this.loadItems()
      },
      (error: HttpErrorResponse) => {
        if (error.status == 403) {
          this.notificationService.error('Ops!', 'Você não pode executar essa ação.')
        } else {
          this.notificationService.error('Ops!', 'Algo deu errado. Tente novamente.')
        }
      }
    )
  }

  public openDeleteItemModal (content: any, item: any): void {
    this.currentItemToDelete = item;
    this.modal.open(content, { centered: true })
      .result.then(
        () => {
          this.deleteItem(item)
        },
        () => this.modal.dismissAll()
      )
  }

  public openEditItemModal (content: any, item: any): void {
    this.currentItemToEdit = item;
    this.modal.open(content, { centered: true })
      .result.then(
        () => {
          // this.deleteItem(item)
        },
        () => this.modal.dismissAll()
      )
  }

  public filterItemsByName(): void {
    if (!this.itemName) {
      this.loadItems();
    } else {
      this.items$ = this.itemService
      .loadItemByName(this.itemName)
      .pipe(map(items => items));
    }
  }

  /*public updateUser(item: any, updatedData: any): any {
    for (let property in updatedData.address) {
      if (!updatedData.address[property]) {
        delete updatedData.address[property]
      }
    }

    this.itemService.updateItem(item.id, updatedData)
    .pipe(takeUntil(this.unsub$))
    .subscribe(
      () => {
        this.notificationService.success('Atenção!', 'Informações salvas com sucesso!')
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
  }*/
}

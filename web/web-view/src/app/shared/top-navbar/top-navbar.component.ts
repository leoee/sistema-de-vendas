import { Component, OnInit } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-top-navbar',
  templateUrl: './top-navbar.component.html',
  styleUrls: ['./top-navbar.component.css']
})
export class TopNavbarComponent implements OnInit {

  constructor(
    private readonly modal: NgbModal,
  ) { }

  ngOnInit(): void {
  }

  public openModal (content: any): void {
    this.modal.open(content, { centered: true })
      .result.then(
        () => {},
        () => this.modal.dismissAll()
      )
  }

}

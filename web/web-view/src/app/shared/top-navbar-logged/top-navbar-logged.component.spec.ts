import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TopNavbarLoggedComponent } from './top-navbar-logged.component';

describe('TopNavbarLoggedComponent', () => {
  let component: TopNavbarLoggedComponent;
  let fixture: ComponentFixture<TopNavbarLoggedComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TopNavbarLoggedComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TopNavbarLoggedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

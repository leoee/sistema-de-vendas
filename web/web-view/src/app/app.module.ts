import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { MainComponent } from './layout/main/main.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { CreateUserComponent } from './layout/create-user/create-user.component';
import { TopNavbarComponent } from './shared/top-navbar/top-navbar.component';
import { CoreModule } from './core/core.module'
import { FooterComponent } from './shared/footer/footer.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HashLocationStrategy, LocationStrategy } from '@angular/common';
import { HomePageComponent } from './layout/home-page/home-page.component';
import { TopNavbarLoggedComponent } from './shared/top-navbar-logged/top-navbar-logged.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { LoadingComponent } from './shared/loading/loading.component';
import { ItemService } from './data/services/item.service';
import { ItemsComponent } from './layout/items/items.component';

@NgModule({
  declarations: [
    AppComponent,
    MainComponent,
    CreateUserComponent,
    TopNavbarComponent,
    FooterComponent,
    HomePageComponent,
    TopNavbarLoggedComponent,
    LoadingComponent,
    ItemsComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    NgbModule,
    ReactiveFormsModule,
    FormsModule,
    MatIconModule,
    MatButtonModule,
    
    CoreModule,
    
    BrowserAnimationsModule
  ],
  providers: [
    {
      provide: LocationStrategy,
      useClass: HashLocationStrategy
    },
    ItemService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }

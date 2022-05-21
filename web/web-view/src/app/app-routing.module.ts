import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './core/guards/auth.guard';
import { AuthRoleGuard } from './core/guards/authRole.guard';
import { CreateUserComponent } from './layout/create-user/create-user.component';
import { HomePageComponent } from './layout/home-page/home-page.component';
import { ItemsComponent } from './layout/items/items.component';
import { MainComponent } from './layout/main/main.component';


const routes: Routes = [
  {
    path: 'home',
    component: MainComponent,
    pathMatch: 'full'
  },
  {
    path: 'create-user',
    component: CreateUserComponent,
    pathMatch: 'full'
  },
  {
    path: 'home-page',
    component: HomePageComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'manage-items',
    component: ItemsComponent,
    canActivate: [AuthGuard, AuthRoleGuard]
  },
  {
    path: '**',
    redirectTo: 'home',
    pathMatch: 'full'
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

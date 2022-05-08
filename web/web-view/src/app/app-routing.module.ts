import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CreateUserComponent } from './layout/create-user/create-user.component';
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

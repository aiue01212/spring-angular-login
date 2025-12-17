// src/app/app.routes.ts
import { Routes } from '@angular/router';
import { LoginWrapperComponent } from './login/login-wrapper.component';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },  
  { path: 'login', component: LoginWrapperComponent },     
  { path: 'welcome', component: LoginWrapperComponent }, 
  { path: '**', redirectTo: 'login' } 
];
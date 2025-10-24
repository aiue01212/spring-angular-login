// src/app/app.routes.ts
import { Routes } from '@angular/router';
import { LoginComponent } from './login/login';
import { WelcomeComponent } from './welcome/welcome';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },  // ルートアクセスは login にリダイレクト
  { path: 'login', component: LoginComponent },      // デフォルトはログイン画面
  { path: 'welcome', component: WelcomeComponent }, // ログイン成功後の画面
  { path: '**', redirectTo: 'login' }  // 不正URLもloginに飛ばす（任意）
];
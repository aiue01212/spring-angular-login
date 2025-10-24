import { Component, signal } from '@angular/core';
import { RouterOutlet, provideRouter } from '@angular/router';
import { bootstrapApplication } from '@angular/platform-browser';
import { importProvidersFrom } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';

import { routes } from './app.routes';  // ルート設定

@Component({
  selector: 'app-root',
  standalone: true,  // スタンドアロンコンポーネントにするため必須
  imports: [RouterOutlet],
  //templateUrl: './app.html',
  templateUrl: './app-router-test.html',
  styleUrls: ['./app.css']
})
export class App {
  protected readonly title = signal('login-frontend');
}

// ここでアプリ起動処理を記述
bootstrapApplication(App, {
  providers: [
    provideRouter(routes),               // ルーティングのプロバイダー
    importProvidersFrom(HttpClientModule) // HTTP通信を利用可能に
  ]
})
.catch(err => console.error(err));

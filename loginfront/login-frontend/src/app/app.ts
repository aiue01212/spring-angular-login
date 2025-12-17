import { Component, signal } from '@angular/core';
import { RouterOutlet, provideRouter } from '@angular/router';
import { bootstrapApplication } from '@angular/platform-browser';
import { importProvidersFrom } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';

import { routes } from './app.routes'; 

@Component({
  selector: 'app-root',
  standalone: true,  
  imports: [RouterOutlet],
  templateUrl: './app-router-test.html',
  styleUrls: ['./app.css']
})
export class App {
  protected readonly title = signal('login-frontend');
}

bootstrapApplication(App, {
  providers: [
    provideRouter(routes),             
    importProvidersFrom(HttpClientModule) 
  ]
})
.catch(err => console.error(err));

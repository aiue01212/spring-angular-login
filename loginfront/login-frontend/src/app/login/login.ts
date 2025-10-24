import { Component } from '@angular/core';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { NgForm } from '@angular/forms';

import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterModule, HttpClientModule, CommonModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class LoginComponent {
  errorMessage: string = '';

  constructor(private http: HttpClient, private router: Router) {}

  onSubmit(form: NgForm): void {
    if (form.valid) {
      const loginData = form.value; 

      this.http.post('http://localhost:8080/api/login', loginData, { 
        observe: 'response',
        withCredentials: true  
      }).subscribe({
        next: (response) => {
          if (response.status === 200) {
            this.errorMessage = '';
            // ログイン成功 → ようこそ画面に遷移
            this.router.navigate(['/welcome']);
          }
        },
        error: (error) => {
          if (error.status >= 400 && error.status < 500) {
            this.errorMessage = error.error?.error || 'ユーザIDまたはパスワードが違います';
          } else if (error.status >= 500) {
            this.errorMessage = error.error?.error || 'サーバー内部エラーが発生しました';
          } else {
            this.errorMessage = '不明なエラーが発生しました。';
          }

          // フォーム状態を強制的にリセット
          form.form.markAsPristine();
          form.form.markAsUntouched();
          form.form.updateValueAndValidity();
        }
      });
    } 
  }
}
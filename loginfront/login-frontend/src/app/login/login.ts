import { Component } from '@angular/core';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { NgForm } from '@angular/forms';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

/**
 * ログイン画面コンポーネント。
 * <p>
 * ユーザー名とパスワードを入力し、認証APIにリクエストを送信する。
 * </p>
 */
@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterModule, HttpClientModule, CommonModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class LoginComponent {

  /** エラーメッセージ（ログイン失敗時に表示） */
  errorMessage: string = '';

  /** 認証APIエンドポイント */
  private static readonly LOGIN_API_URL = 'http://localhost:8080/api/login';

  /**
   * コンストラクタ。
   * @param http HTTP通信クライアント
   * @param router ルーター
   */
  constructor(private http: HttpClient, private router: Router) {}

  /**
   * ログインフォーム送信時の処理。
   *
   * @param form ログインフォーム
   */
  onSubmit(form: NgForm): void {
    if (form.valid) {
      const loginData = form.value;

      this.http.post(LoginComponent.LOGIN_API_URL, loginData, {
        observe: 'response',
        withCredentials: true
      }).subscribe({
        next: (response) => {
          if (response.status === 200) {
            this.errorMessage = '';
            this.router.navigate(['/welcome']); // ログイン成功時にようこそ画面へ遷移
          }
        },
        error: (error) => {
          // ステータスコードに応じてエラーメッセージを設定
          if (error.status >= 400 && error.status < 500) {
            this.errorMessage = error.error?.error || 'ユーザIDまたはパスワードが違います';
          } else if (error.status >= 500) {
            this.errorMessage = error.error?.error || 'サーバー内部エラーが発生しました';
          } else {
            this.errorMessage = '不明なエラーが発生しました。';
          }

          // フォーム状態をリセット
          form.form.markAsPristine();
          form.form.markAsUntouched();
          form.form.updateValueAndValidity();
        }
      });
    }
  }
}
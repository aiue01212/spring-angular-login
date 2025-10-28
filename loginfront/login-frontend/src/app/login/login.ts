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

  /** HTTPステータスコード定数 */
  private static readonly HTTP_STATUS = {
    OK: 200,
    BAD_REQUEST_MIN: 400,
    BAD_REQUEST_MAX: 499,
    SERVER_ERROR_MIN: 500
  };

  /** ナビゲーションルート */
  private static readonly ROUTES = {
    WELCOME: '/welcome',
    LOGIN: '/login'
  };

  /** デフォルトエラーメッセージ */
  private static readonly ERROR_MESSAGES = {
    AUTH_FAILED: 'ユーザIDまたはパスワードが違います',
    SERVER_ERROR: 'サーバー内部エラーが発生しました',
    UNKNOWN: '不明なエラーが発生しました。'
  };

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
          if (response.status === LoginComponent.HTTP_STATUS.OK) {
            this.errorMessage = '';
            this.router.navigate([LoginComponent.ROUTES.WELCOME]);
          }
        },
        error: (error) => {
          if (error.status >= LoginComponent.HTTP_STATUS.BAD_REQUEST_MIN &&
              error.status < LoginComponent.HTTP_STATUS.BAD_REQUEST_MAX) {
            this.errorMessage = error.error?.error || LoginComponent.ERROR_MESSAGES.AUTH_FAILED;
          } else if (error.status >= LoginComponent.HTTP_STATUS.SERVER_ERROR_MIN) {
            this.errorMessage = error.error?.error || LoginComponent.ERROR_MESSAGES.SERVER_ERROR;
          } else {
            this.errorMessage = LoginComponent.ERROR_MESSAGES.UNKNOWN;
          }

          form.form.markAsPristine();
          form.form.markAsUntouched();
          form.form.updateValueAndValidity();
        }
      });
    }
  }
}
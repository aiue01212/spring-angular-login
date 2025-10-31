import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Subscription, interval } from 'rxjs';

/**
 * 商品情報の型定義。
 */
interface Product {
  id: number;
  name: string;
  price: number;
}

/**
 * ログイン完了画面コンポーネント。
 * <p>
 * ログイン済みユーザーに対してセッション確認と商品一覧表示を行う。
 * </p>
 */
@Component({
  selector: 'app-welcome',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './welcome.html',
  styleUrls: ['./welcome.css']
})
export class WelcomeComponent implements OnInit, OnDestroy {

  /** セッションチェック間隔（ミリ秒） */
  private static readonly SESSION_CHECK_INTERVAL_MS = 60_000;

  /** APIエンドポイント群 */
  private static readonly API_ENDPOINTS = {
    SESSION_CHECK: 'http://localhost:8080/api/session-check',
    PRODUCTS: 'http://localhost:8080/api/products',
    LOGOUT: 'http://localhost:8080/api/logout'
  };

  /** HTTPステータスコード */
  private static readonly HTTP_STATUS = {
    UNAUTHORIZED: 401
  };

  /** ルートパス */
  private static readonly ROUTES = {
    LOGIN: '/login'
  };

  /** メッセージ定数 */
  private static readonly MESSAGES = {
    SESSION_EXPIRED: 'セッションの有効期限が切れました。再ログインしてください。',
    LOGOUT_FAILED: 'ログアウトに失敗しました。',
    FETCH_PRODUCTS_FAILED: '商品情報の取得に失敗しました',
    SESSION_VALID: 'セッション有効です', 
    SESSION_CHECK_ERROR: 'セッションチェックでエラー',
    FETCH_FAILED: '商品情報の取得に失敗しました。'
  };

  /** セッションチェック用の購読オブジェクト */
  private sessionCheckSubscription?: Subscription;

  /** 商品リスト */
  products: Product[] = [];

  /**
   * コンストラクタ。
   * @param http HTTP通信クライアント
   * @param router ルーター
   */
  constructor(private http: HttpClient, private router: Router) {}

  /**
   * コンポーネント初期化時の処理。
   * <p>
   * セッション確認と商品情報の取得を行う。
   * </p>
   */
  ngOnInit(): void {
    this.sessionCheckSubscription = interval(WelcomeComponent.SESSION_CHECK_INTERVAL_MS).subscribe(() => {
      this.checkSession();
    });

    this.checkSession();
    this.fetchProducts();
  }

  /**
   * コンポーネント破棄時の処理。
   * サブスクリプションを解除する。
   */
  ngOnDestroy(): void {
    this.sessionCheckSubscription?.unsubscribe();
  }

  /**
   * セッション状態を確認する。
   * <p>
   * 無効な場合はログイン画面へリダイレクトする。
   * </p>
   */
  checkSession(): void {
    this.http.get(WelcomeComponent.API_ENDPOINTS.SESSION_CHECK, { withCredentials: true })
      .subscribe({
        next: () => console.log(WelcomeComponent.MESSAGES.SESSION_VALID),
        error: (err) => {
          if (err.status === WelcomeComponent.HTTP_STATUS.UNAUTHORIZED) {
            alert(WelcomeComponent.MESSAGES.SESSION_EXPIRED);
            this.router.navigate([WelcomeComponent.ROUTES.LOGIN]);
          } else {
            console.error(WelcomeComponent.MESSAGES.SESSION_CHECK_ERROR, err);
          }
        }
      });
  }

  /**
   * 商品一覧を取得する。
   */
  fetchProducts(): void {
    this.http.get<{ products: Product[] }>(WelcomeComponent.API_ENDPOINTS.PRODUCTS, { withCredentials: true })
    .subscribe({
      next: (data) => this.products = data.products,
      error: (err) => console.error(WelcomeComponent.MESSAGES.FETCH_PRODUCTS_FAILED, err)
    });
  }

  /**
   * ログアウト処理を行う。
   */
  logout(): void {
    this.http.post(WelcomeComponent.API_ENDPOINTS.LOGOUT, {}, { withCredentials: true })
      .subscribe({
        next: () => this.router.navigate([WelcomeComponent.ROUTES.LOGIN]),
        error: () => alert(WelcomeComponent.MESSAGES.LOGOUT_FAILED)
      });
  }
}
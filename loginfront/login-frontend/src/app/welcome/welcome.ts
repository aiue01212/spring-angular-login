import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Subscription, interval } from 'rxjs';

interface Product {
  id: number;
  name: string;
  price: number;
}

@Component({
  selector: 'app-welcome',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './welcome.html',
  styleUrls: ['./welcome.css']
})
export class WelcomeComponent implements OnInit, OnDestroy {
  private sessionCheckSubscription?: Subscription;
  products: Product[] = [];

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    // 1分ごとにセッション状態をチェック
    this.sessionCheckSubscription = interval(60000).subscribe(() => {
      this.checkSession();
    });

    // 最初のチェックもすぐ行いたい場合は以下も呼ぶ
    this.checkSession();

    // 商品一覧を取得
    this.fetchProducts();
  }

  ngOnDestroy(): void {
    // コンポーネント破棄時にサブスクリプションを解除
    this.sessionCheckSubscription?.unsubscribe();
  }

  checkSession(): void {
    this.http.get('http://localhost:8080/api/session-check', { withCredentials: true })
      .subscribe({
        next: (res) => {
          // セッションあり → 特に処理なし
          console.log('セッション有効です');
        },
        error: (err) => {
          if (err.status === 401) {
            alert('セッションの有効期限が切れました。再ログインしてください。');
            this.router.navigate(['/login']);
          } else {
            console.error('セッションチェックでエラー', err);
          }
        }
      });
  }

  fetchProducts(): void {
    this.http.get<Product[]>('http://localhost:8080/api/products', { withCredentials: true })
      .subscribe({
        next: (data) => {
          this.products = data;
        },
        error: (err) => {
          console.error('商品情報の取得に失敗しました', err);
        }
      });
  }

  logout(): void {
    this.http.post('http://localhost:8080/api/logout', {}, { withCredentials: true }).subscribe({
      next: () => {
        this.router.navigate(['/login']);
      },
      error: () => {
        alert('ログアウトに失敗しました。');
      }
    });
  }
}
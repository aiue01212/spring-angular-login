import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

interface Product {
  id: number;
  name: string;
  price: number;
}

const API_ENDPOINTS = {
  SESSION_CHECK: 'http://localhost:9090/api/session-check',
  PRODUCTS: 'http://localhost:9090/api/products',
  LOGOUT: 'http://localhost:9090/api/logout',
};

const HTTP_STATUS = {
  UNAUTHORIZED: 401,
};

const Welcome: React.FC = () => {
  const navigate = useNavigate();
  const [products, setProducts] = useState<Product[]>([]);

  useEffect(() => {
    // セッションチェック
    const checkSession = async () => {
      try {
        const res = await fetch(API_ENDPOINTS.SESSION_CHECK, {
          method: 'GET',
          credentials: 'include',
        });
        if (res.status === HTTP_STATUS.UNAUTHORIZED) {
          alert('セッションの有効期限が切れました。再ログインしてください。');
          navigate('/login');
        }
      } catch (error) {
        console.error('セッションチェックでエラー', error);
      }
    };

    // 商品取得
    const fetchProducts = async () => {
      try {
        const res = await fetch(API_ENDPOINTS.PRODUCTS, {
          credentials: 'include',
        });
        if (!res.ok) {
          console.error('商品情報の取得に失敗しました');
          return;
        }
        const data = await res.json();
        setProducts(data.products);
      } catch (error) {
        console.error('商品情報の取得に失敗しました', error);
      }
    };

    checkSession();
    fetchProducts();

    const intervalId = setInterval(checkSession, 60_000);
    return () => clearInterval(intervalId);
  }, [navigate]);

  const logout = async () => {
    try {
      const res = await fetch(API_ENDPOINTS.LOGOUT, {
        method: 'POST',
        credentials: 'include',
      });
      if (res.ok) {
        navigate('/login');
      } else {
        alert('ログアウトに失敗しました。');
      }
    } catch {
      alert('ログアウトに失敗しました。');
    }
  };

  return (
    <div className="welcome-container">
      <div className="welcome-box">
        <h2>ようこそ！ログインに成功しました。</h2>
        <p>このページはログイン成功後に表示される画面です。</p>
        <button onClick={logout}>ログアウト</button>
      </div>

      {products.length > 0 && (
        <div className="product-section">
          <h3>商品一覧</h3>
          <div className="product-grid">
            {products.map((product) => (
              <div key={product.id} className="product-card">
                <div className="product-info">
                  <h4>{product.name}</h4>
                  <p className="price">¥{product.price.toLocaleString()}</p>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default Welcome;
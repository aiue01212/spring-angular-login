import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const LOGIN_API_URL = 'http://localhost:9090/api/login';

const HTTP_STATUS = {
  OK: 200,
  BAD_REQUEST_MIN: 400,
  BAD_REQUEST_MAX: 499,
  SERVER_ERROR_MIN: 500,
};

const ROUTES = {
  WELCOME: '/welcome',
  LOGIN: '/login',
};

const ERROR_MESSAGES = {
  AUTH_FAILED: 'ユーザIDまたはパスワードが違います',
  SERVER_ERROR: 'サーバー内部エラーが発生しました',
  UNKNOWN: '不明なエラーが発生しました。',
};

const Login = () => {
  const navigate = useNavigate();

  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');

  const [errorMessage, setErrorMessage] = useState('');

  const isFormValid = username !== '' && password !== '';

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!isFormValid) return;

    try {
      const response = await fetch(LOGIN_API_URL, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        credentials: 'include',
        body: JSON.stringify({
          username,
          password,
        }),
      });

      if (response.status === HTTP_STATUS.OK) {
        setErrorMessage('');
        navigate(ROUTES.WELCOME);
        return;
      }

      const errorBody = await response.json().catch(() => null);

      if (
        response.status >= HTTP_STATUS.BAD_REQUEST_MIN &&
        response.status < HTTP_STATUS.BAD_REQUEST_MAX
      ) {
        setErrorMessage(
          errorBody?.error || ERROR_MESSAGES.AUTH_FAILED
        );
      } else if (response.status >= HTTP_STATUS.SERVER_ERROR_MIN) {
        setErrorMessage(
          errorBody?.error || ERROR_MESSAGES.SERVER_ERROR
        );
      } else {
        setErrorMessage(ERROR_MESSAGES.UNKNOWN);
      }

    } catch {
      setErrorMessage(ERROR_MESSAGES.UNKNOWN);
    }
  };

  return (
    <div className="page-container">
      <div className="login-card">
        <div className="login-form-container">
          <h2 className="title">アカウントログイン</h2>

          <form onSubmit={onSubmit}>
            <div className="form-group floating-label">
              <input
                id="username"
                type="text"
                className="input"
                placeholder=" "
                autoComplete="off"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
              />
              <label htmlFor="username">ユーザーID</label>
            </div>

            <div className="form-group floating-label">
              <input
                id="password"
                type="password"
                className="input"
                placeholder=" "
                autoComplete="off"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
              <label htmlFor="password">パスワード</label>
            </div>

            {errorMessage && (
              <div className="error-message">
                {errorMessage}
              </div>
            )}

            <button
              type="submit"
              className="login-button"
              disabled={!isFormValid}
            >
              ログイン
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default Login;
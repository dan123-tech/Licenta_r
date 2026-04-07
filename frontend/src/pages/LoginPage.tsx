import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { ROUTES } from '../utils/constants';
import './LoginPage.css';

const LoginPage: React.FC = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setIsLoading(true);

    try {
      const jwt = await login(username, password);
      const role = jwt.role;
      if (role === 'ROLE_SUPEROWNER') {
        navigate(ROUTES.ADMIN, { replace: true });
      } else if (role === 'ROLE_ADMIN' || role === 'ROLE_VENDOR') {
        navigate(ROUTES.ADMIN_PRODUCTS, { replace: true });
      } else {
        navigate(ROUTES.PRODUCTS, { replace: true });
      }
    } catch (err: any) {
      const msg =
        err.response?.data?.message ||
        (typeof err.response?.data === 'string' ? err.response.data : null);
      setError(msg || 'Eroare la autentificare');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="login-page">
      <div className="login-container">
        <div className="auth-header">
          <div className="auth-logo" aria-hidden>
            <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
              <path d="M20 12V22H4V12" />
              <path d="M22 7H2v5h20V7z" />
              <path d="M12 22V7" />
              <path d="M12 7H7.5a2.5 2.5 0 0 1 0-5C11 2 12 7 12 7z" />
              <path d="M12 7h4.5a2.5 2.5 0 0 0 0-5C13 2 12 7 12 7z" />
            </svg>
          </div>
          <p className="auth-kicker">Bine ai revenit</p>
          <h2>Autentificare</h2>
          <p className="auth-subtitle">
            Catalogul este vizibil și fără cont. Autentifică-te pentru închirieri, plăți și zona ta
            personală.
          </p>
        </div>
        <form onSubmit={handleSubmit} className="login-form">
          {error && <div className="form-error">{error}</div>}
          
          <div className="form-group">
            <label className="form-label">Username</label>
            <input
              type="text"
              className="form-input"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="Introdu username-ul tau"
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label">Parolă</label>
            <input
              type="password"
              className="form-input"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Introdu parola"
              required
            />
          </div>

          <button 
            type="submit" 
            className="btn btn-primary btn-block"
            disabled={isLoading}
          >
            {isLoading ? 'Se conectează...' : 'Conectare'}
          </button>
        </form>

        <p className="register-link">
          Nu ai cont? <Link to={ROUTES.REGISTER}>Înregistrează-te</Link>
        </p>
      </div>
    </div>
  );
};

export default LoginPage;

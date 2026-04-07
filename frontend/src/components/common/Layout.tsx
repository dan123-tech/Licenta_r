import React from 'react';
import { Link, NavLink } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { ROUTES } from '../../utils/constants';
import ProductsMegaMenu from './ProductsMegaMenu';
import './Layout.css';

interface LayoutProps {
  children: React.ReactNode;
}

const subNavClass = ({ isActive }: { isActive: boolean }) =>
  `app-subnav-link${isActive ? ' is-active' : ''}`;

const Layout: React.FC<LayoutProps> = ({ children }) => {
  const { isAuthenticated, user, logout, isSuperOwner, canEditCatalog } = useAuth();

  return (
    <div className={`app-layout${!isAuthenticated ? ' app-layout--guest-nav' : ''}`}>
      <header className="app-header">
        <div className="app-header-tier1">
          <nav className="app-nav app-nav-primary" aria-label="Navigare principală">
            <div className="nav-brand">
              <Link to={ROUTES.PRODUCTS}>
                <div className="nav-brand-logo" aria-hidden>
                  <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                    <path d="M20 12V22H4V12" />
                    <path d="M22 7H2v5h20V7z" />
                    <path d="M12 22V7" />
                    <path d="M12 7H7.5a2.5 2.5 0 0 1 0-5C11 2 12 7 12 7z" />
                    <path d="M12 7h4.5a2.5 2.5 0 0 0 0-5C13 2 12 7 12 7z" />
                  </svg>
                </div>
                <span className="nav-brand-title">
                  Online <strong>Rental</strong>
                </span>
              </Link>
            </div>

            <div className="nav-links nav-links-primary">
              {!isAuthenticated && (
                <NavLink to={ROUTES.PRODUCTS} end className="nav-catalog-link">
                  Catalog produse
                </NavLink>
              )}
              <ProductsMegaMenu />
              {isAuthenticated && <NavLink to={ROUTES.MY_RENTALS}>Închirieri</NavLink>}
              {isSuperOwner && <NavLink to={ROUTES.ADMIN}>Admin</NavLink>}
              <NavLink to={ROUTES.HELP}>Ajutor</NavLink>
            </div>

            <div className="nav-user-slot">
              {isAuthenticated ? (
                <div className="nav-user-block">
                  <span className="nav-user-divider" aria-hidden />
                  <div className="nav-user-avatar" aria-hidden />
                  <span className="nav-user-name">{user?.username}</span>
                  <button type="button" onClick={logout} className="btn-nav-logout">
                    Deconectare
                  </button>
                </div>
              ) : (
                <div className="nav-user-block nav-user-guest">
                  <NavLink to={ROUTES.LOGIN} className="nav-guest-link">
                    Autentificare
                  </NavLink>
                  <NavLink to={ROUTES.REGISTER} className="btn-nav-register">
                    Înregistrare
                  </NavLink>
                </div>
              )}
            </div>
          </nav>
        </div>

        {isAuthenticated && (
          <div
            className={`app-header-tier2${isSuperOwner ? ' app-header-tier2--super' : ''}`}
            aria-label={isSuperOwner ? 'Navigare super-admin' : 'Navigare rapidă'}
          >
            <nav className="app-nav app-nav-secondary">
              <NavLink to={ROUTES.PRODUCTS} end className={subNavClass}>
                Catalog Produse
              </NavLink>
              <NavLink to={ROUTES.ACCOUNT} className={subNavClass}>
                Contul meu
              </NavLink>
              <NavLink to={ROUTES.CREATE_RENTAL} className={subNavClass}>
                Creare închiriere
              </NavLink>
              {isSuperOwner && (
                <>
                  <NavLink to={ROUTES.ADMIN} end className={subNavClass}>
                    Admin Dashboard
                  </NavLink>
                  <NavLink to={ROUTES.ADMIN_PRODUCTS} className={subNavClass}>
                    Gestionare Produse
                  </NavLink>
                  <NavLink to={ROUTES.ADMIN_RENTALS} className={subNavClass}>
                    Gestionare Închirieri
                  </NavLink>
                  <NavLink to={ROUTES.SUPEROWNER_STATS} className={subNavClass}>
                    Statistici financiare
                  </NavLink>
                </>
              )}
              {!isSuperOwner && canEditCatalog && (
                <NavLink to={ROUTES.ADMIN_PRODUCTS} className={subNavClass}>
                  Gestionare Produse
                </NavLink>
              )}
            </nav>
          </div>
        )}
      </header>

      <main className="app-main">
        {children}
      </main>

      <footer className="app-footer">
        <div className="app-footer-inner">
          <p className="app-footer-flow">2026 Online Rental System · powered by Blehoianu-Robert Ionut</p>
          <nav className="app-footer-links" aria-label="Legături legale și suport">
            <a href="#" className="app-footer-link">
              Privacy Policy
            </a>
            <span className="app-footer-sep" aria-hidden>
              ·
            </span>
            <a href="#" className="app-footer-link">
              Terms and Conditions
            </a>
            <span className="app-footer-sep" aria-hidden>
              ·
            </span>
            <a href="#" className="app-footer-link">
              Support
            </a>
            <span className="app-footer-sep" aria-hidden>
              ·
            </span>
            <button type="button" className="app-footer-link app-footer-link-btn">
              Cookie preferences
            </button>
          </nav>
        </div>
      </footer>
    </div>
  );
};

export default Layout;

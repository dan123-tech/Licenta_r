import React from 'react';
import './LegalPage.css';

const CookiePreferencesPage: React.FC = () => {
  return (
    <div className="legal-page">
      <div className="container legal-container">
        <header className="legal-hero">
          <h1>Cookie preferences</h1>
          <p>Alege cum doresti sa fie folosite cookie-urile in platforma.</p>
        </header>

        <section className="legal-section">
          <h2>Tipuri de cookie-uri</h2>
          <ul>
            <li>Necesare: obligatorii pentru autentificare si securitate.</li>
            <li>Functionale: retin preferintele de utilizare.</li>
            <li>Analitice: ne ajuta sa imbunatatim experienta (optional).</li>
          </ul>
        </section>

        <section className="legal-section">
          <h2>Gestionarea preferintelor</h2>
          <p>
            Poti modifica preferintele de cookie in orice moment din aceasta pagina. Schimbarile se aplica la
            urmatoarea sesiune sau dupa refresh.
          </p>
        </section>
      </div>
    </div>
  );
};

export default CookiePreferencesPage;

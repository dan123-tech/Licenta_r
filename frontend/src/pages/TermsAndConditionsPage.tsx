import React from 'react';
import './LegalPage.css';

const TermsAndConditionsPage: React.FC = () => {
  return (
    <div className="legal-page">
      <div className="container legal-container">
        <header className="legal-hero">
          <h1>Terms and Conditions</h1>
          <p>Prin folosirea platformei, accepti termenii si conditiile de mai jos.</p>
        </header>

        <section className="legal-section">
          <h2>Eligibilitate si cont</h2>
          <p>
            Utilizatorii sunt responsabili pentru confidentialitatea credentialelor de autentificare si pentru
            activitatea realizata din contul propriu.
          </p>
        </section>

        <section className="legal-section">
          <h2>Inchirieri si returnari</h2>
          <p>
            Produsele trebuie utilizate conform specificatiilor si returnate in perioada stabilita. Orice
            deteriorare poate duce la retinerea depozitului conform regulilor platformei.
          </p>
        </section>

        <section className="legal-section">
          <h2>Limitarea raspunderii</h2>
          <p>
            Platforma depune eforturi rezonabile pentru disponibilitate continua, dar nu garanteaza functionare
            neintrerupta in toate situatiile.
          </p>
        </section>
      </div>
    </div>
  );
};

export default TermsAndConditionsPage;

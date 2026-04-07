import React from 'react';
import './LegalPage.css';

const PrivacyPolicyPage: React.FC = () => {
  return (
    <div className="legal-page">
      <div className="container legal-container">
        <header className="legal-hero">
          <h1>Privacy Policy</h1>
          <p>Politica noastra de confidentialitate explica ce date colectam si cum le folosim.</p>
        </header>

        <section className="legal-section">
          <h2>Date colectate</h2>
          <p>
            Putem colecta date de cont (nume, email), informatii de utilizare a platformei si date tehnice
            necesare pentru securitate si functionare.
          </p>
        </section>

        <section className="legal-section">
          <h2>Utilizarea datelor</h2>
          <ul>
            <li>procesarea comenzilor de inchiriere</li>
            <li>comunicari operationale (confirmari, notificari)</li>
            <li>imbunatatirea calitatii serviciului si prevenirea fraudelor</li>
          </ul>
        </section>

        <section className="legal-section">
          <h2>Drepturile tale</h2>
          <p>
            Poti solicita acces, rectificare sau stergere a datelor personale, conform legislatiei aplicabile
            (inclusiv GDPR).
          </p>
        </section>
      </div>
    </div>
  );
};

export default PrivacyPolicyPage;

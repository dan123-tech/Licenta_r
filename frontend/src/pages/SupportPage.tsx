import React from 'react';
import './LegalPage.css';

const SupportPage: React.FC = () => {
  return (
    <div className="legal-page">
      <div className="container legal-container">
        <header className="legal-hero">
          <h1>Support</h1>
          <p>Ai nevoie de ajutor? Echipa noastra de suport iti raspunde cat mai rapid.</p>
        </header>

        <section className="legal-section">
          <h2>Canale de contact</h2>
          <ul>
            <li>Email: support@onlinerental.ro</li>
            <li>Telefon: +40 123 456 789</li>
            <li>Program: Luni - Vineri, 09:00 - 18:00</li>
          </ul>
        </section>

        <section className="legal-section">
          <h2>Timp de raspuns</h2>
          <p>
            Pentru solicitarile standard, timpul mediu de raspuns este de 24 de ore lucratoare. Pentru incidente
            critice, tratam solicitarea cu prioritate.
          </p>
        </section>
      </div>
    </div>
  );
};

export default SupportPage;

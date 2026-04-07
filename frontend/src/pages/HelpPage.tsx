import React from 'react';
import './HelpPage.css';

const HelpPage: React.FC = () => {
  return (
    <div className="help-page">
      <div className="container">
        <header className="help-hero">
          <h1>Centru de Ajutor</h1>
          <p>
            Aici gasesti toate informatiile importante despre inchiriere, plata,
            livrare si returnare.
          </p>
        </header>

        <section className="help-section">
          <h2>Conturi și drepturi</h2>
          <div className="help-cards">
            <article className="help-card">
              <h3>Trebuie să mă loghez ca să văd produsele?</h3>
              <p>
                Nu. Catalogul și detaliile produselor sunt publice. Contul este necesar pentru a crea
                închirieri, a vedea „Închirierile mele” și a plăti.
              </p>
            </article>
            <article className="help-card">
              <h3>Cine poate pune produse „la vânzare” în catalog?</h3>
              <p>
                Doar utilizatorii cu rol de <strong>administrator</strong> (și SuperOwner) pot adăuga,
                edita sau șterge produse și stoc. Clienții și furnizorii înregistrați ca atare nu pot
                publica produse noi în sistem.
              </p>
            </article>
            <article className="help-card">
              <h3>După autentificare, unde ajung?</h3>
              <p>
                Administratorii sunt redirecționați către panoul <strong>Admin</strong>. Utilizatorii
                obișnuiți merg la catalogul de produse; de acolo pot începe o închiriere.
              </p>
            </article>
          </div>
        </section>

        <section className="help-section">
          <h2>FAQ - Intrebari frecvente</h2>
          <div className="help-cards">
            <article className="help-card">
              <h3>Cum inchiriez un produs?</h3>
              <p>
                Intra in pagina produsului, selecteaza perioada de inchiriere si
                finalizeaza comanda din pasii afisati.
              </p>
            </article>
            <article className="help-card">
              <h3>Care este durata unei inchirieri?</h3>
              <p>
                Durata este flexibila si se stabileste in functie de intervalul
                ales la creare.
              </p>
            </article>
            <article className="help-card">
              <h3>Cum returnez produsul?</h3>
              <p>
                Returnarea se face conform termenilor din comanda. Produsul trebuie
                predat in starea in care a fost primit.
              </p>
            </article>
          </div>
        </section>

        <section className="help-section">
          <h2>Contact</h2>
          <ul className="help-list">
            <li>Email: support@onlinerental.ro</li>
            <li>Telefon: +40 123 456 789</li>
            <li>Program: Luni - Vineri, 09:00 - 18:00</li>
          </ul>
        </section>

        <section className="help-section">
          <h2>Despre platforma</h2>
          <p>
            Online Rental System este o platforma pentru inchirierea produselor
            tech. Scopul nostru este sa oferim acces rapid la echipamente moderne,
            fara costul integral al achizitiei.
          </p>
        </section>

        <section className="help-section">
          <h2>Termeni si conditii</h2>
          <p>
            Utilizarea platformei implica acceptarea termenilor de utilizare.
            Produsele trebuie returnate la timp si in conditii corespunzatoare.
          </p>
        </section>

        <section className="help-section">
          <h2>Politica de confidentialitate</h2>
          <p>
            Datele personale sunt procesate conform regulilor GDPR si sunt
            utilizate exclusiv pentru operarea serviciului de inchiriere.
          </p>
        </section>
      </div>
    </div>
  );
};

export default HelpPage;

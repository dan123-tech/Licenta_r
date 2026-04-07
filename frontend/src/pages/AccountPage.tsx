import React, { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { profileService } from '../services/profileService';
import { UserProfile, ProfilePatchPayload } from '../types';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import './AccountPage.css';

const emptyProfile: UserProfile = {
  username: '',
  email: '',
  firstName: '',
  lastName: '',
};

const AccountPage: React.FC = () => {
  const { user } = useAuth();
  const [profile, setProfile] = useState<UserProfile>(emptyProfile);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState<{ type: 'ok' | 'err'; text: string } | null>(null);

  const load = async () => {
    try {
      setLoading(true);
      const p = await profileService.getProfile();
      setProfile(p);
    } catch {
      setMessage({ type: 'err', text: 'Nu s-au putut încărca datele contului.' });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  const setField = (key: keyof UserProfile, value: string) => {
    setProfile((prev) => ({ ...prev, [key]: value }));
  };

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    setMessage(null);
    const payload: ProfilePatchPayload = {
      firstName: profile.firstName || undefined,
      lastName: profile.lastName || undefined,
      addressLine1: profile.addressLine1 || undefined,
      addressLine2: profile.addressLine2 || undefined,
      city: profile.city || undefined,
      postalCode: profile.postalCode || undefined,
      country: profile.country || undefined,
      phone: profile.phone || undefined,
      billingFullName: profile.billingFullName || undefined,
    };
    try {
      setSaving(true);
      const updated = await profileService.patchProfile(payload);
      setProfile(updated);
      setMessage({ type: 'ok', text: 'Modificările au fost salvate.' });
    } catch {
      setMessage({ type: 'err', text: 'Salvare eșuată. Încearcă din nou.' });
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div className="account-page">
        <div className="container">
          <LoadingSpinner />
        </div>
      </div>
    );
  }

  return (
    <div className="account-page">
      <div className="container account-page-inner">
        <header className="account-page-header">
          <h1>Contul meu</h1>
          <p>Date personale, adresă de livrare și informații pentru facturare.</p>
        </header>

        {message && (
          <div className={`account-banner ${message.type === 'ok' ? 'account-banner--ok' : 'account-banner--err'}`}>
            {message.text}
          </div>
        )}

        <form onSubmit={handleSave} className="account-form-grid">
          <section className="account-card">
            <h2>Date cont</h2>
            <p className="account-muted">Username și email nu pot fi modificate aici.</p>
            <div className="account-field">
              <label>Username</label>
              <input type="text" value={profile.username || user?.username || ''} disabled />
            </div>
            <div className="account-field">
              <label>Email</label>
              <input type="email" value={profile.email || user?.email || ''} disabled />
            </div>
            <div className="account-field-row">
              <div className="account-field">
                <label htmlFor="acc-fn">Prenume</label>
                <input
                  id="acc-fn"
                  type="text"
                  value={profile.firstName || ''}
                  onChange={(e) => setField('firstName', e.target.value)}
                  autoComplete="given-name"
                />
              </div>
              <div className="account-field">
                <label htmlFor="acc-ln">Nume</label>
                <input
                  id="acc-ln"
                  type="text"
                  value={profile.lastName || ''}
                  onChange={(e) => setField('lastName', e.target.value)}
                  autoComplete="family-name"
                />
              </div>
            </div>
          </section>

          <section className="account-card">
            <h2>Adresă livrare</h2>
            <p className="account-muted">Folosită ca referință la comenzile cu livrare.</p>
            <div className="account-field">
              <label htmlFor="acc-a1">Adresă (linia 1)</label>
              <input
                id="acc-a1"
                type="text"
                value={profile.addressLine1 || ''}
                onChange={(e) => setField('addressLine1', e.target.value)}
                autoComplete="street-address"
              />
            </div>
            <div className="account-field">
              <label htmlFor="acc-a2">Adresă (linia 2)</label>
              <input
                id="acc-a2"
                type="text"
                value={profile.addressLine2 || ''}
                onChange={(e) => setField('addressLine2', e.target.value)}
              />
            </div>
            <div className="account-field-row">
              <div className="account-field">
                <label htmlFor="acc-city">Localitate</label>
                <input
                  id="acc-city"
                  type="text"
                  value={profile.city || ''}
                  onChange={(e) => setField('city', e.target.value)}
                  autoComplete="address-level2"
                />
              </div>
              <div className="account-field">
                <label htmlFor="acc-post">Cod poștal</label>
                <input
                  id="acc-post"
                  type="text"
                  value={profile.postalCode || ''}
                  onChange={(e) => setField('postalCode', e.target.value)}
                  autoComplete="postal-code"
                />
              </div>
            </div>
            <div className="account-field-row">
              <div className="account-field">
                <label htmlFor="acc-country">Țară</label>
                <input
                  id="acc-country"
                  type="text"
                  value={profile.country || ''}
                  placeholder="România"
                  onChange={(e) => setField('country', e.target.value)}
                  autoComplete="country-name"
                />
              </div>
              <div className="account-field">
                <label htmlFor="acc-phone">Telefon</label>
                <input
                  id="acc-phone"
                  type="tel"
                  value={profile.phone || ''}
                  onChange={(e) => setField('phone', e.target.value)}
                  autoComplete="tel"
                />
              </div>
            </div>
          </section>

          <section className="account-card account-card--wide">
            <h2>Facturare și plată cu cardul</h2>
            <p className="account-muted">
              Datele cardului nu se stochează în aplicație. La finalizarea plății pentru o închiriere vei introduce
              cardul în formularul securizat Stripe. Poți folosi numele de facturare de mai jos pe documente.
            </p>
            <div className="account-field">
              <label htmlFor="acc-bill">Nume pentru facturare</label>
              <input
                id="acc-bill"
                type="text"
                value={profile.billingFullName || ''}
                placeholder="Ex.: Ion Popescu sau Denumire firmă"
                onChange={(e) => setField('billingFullName', e.target.value)}
              />
            </div>
            <div className="account-stripe-note">
              <strong>Adăugare card:</strong> mergi la o rezervare în așteptare și deschide pagina de plată — acolo
              poți introduce datele cardului în siguranță.
            </div>
          </section>

          <div className="account-actions">
            <button type="submit" className="btn btn-primary" disabled={saving}>
              {saving ? 'Se salvează…' : 'Salvează modificările'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default AccountPage;

import React, { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { rentalService } from '../services/rentalService';
import { Rental, RentalReturnWorkflow } from '../types';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { getImageUrl } from '../utils/imageHelper';
import './RentalReturnPhotosPage.css';

const RentalReturnPhotosPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const rentalId = Number(id);
  const [rental, setRental] = useState<Rental | null>(null);
  const [workflow, setWorkflow] = useState<RentalReturnWorkflow | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isUploading, setIsUploading] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isVerifying, setIsVerifying] = useState(false);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  const loadData = async () => {
    try {
      setIsLoading(true);
      const [r, w] = await Promise.all([
        rentalService.getRentalById(rentalId),
        rentalService.getReturnWorkflow(rentalId),
      ]);
      setRental(r);
      setWorkflow(w);
      setError('');
    } catch (err: any) {
      setError(err.response?.data?.message || 'Nu s-au putut încărca datele.');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    if (!Number.isFinite(rentalId)) {
      setError('Închiriere invalidă.');
      setIsLoading(false);
      return;
    }
    loadData();
  }, [rentalId]);

  const handleFiles = async (files: FileList | null) => {
    if (!files?.length) return;
    try {
      setIsUploading(true);
      setMessage('');
      setError('');
      for (const file of Array.from(files)) {
        await rentalService.uploadReturnPhoto(rentalId, file);
      }
      setMessage('Pozele pentru retur au fost încărcate.');
      await loadData();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Eroare la încărcare.');
    } finally {
      setIsUploading(false);
    }
  };

  const handleSubmitReturn = async () => {
    try {
      setIsSubmitting(true);
      setMessage('');
      setError('');
      const resp = await rentalService.submitReturnRequest(rentalId);
      setMessage(resp.message);
      await loadData();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Eroare la trimiterea returului.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleRunVerification = async () => {
    try {
      setIsVerifying(true);
      setMessage('');
      setError('');
      const resp = await rentalService.runHandoverVerification(rentalId, 'RETURN');
      setMessage(resp.message || 'Verificare AI retur finalizată.');
      await loadData();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Eroare la rularea AI.');
    } finally {
      setIsVerifying(false);
    }
  };

  if (isLoading) return <LoadingSpinner />;
  if (error && !rental) return <div className="error-message">{error}</div>;

  const minPhotos = workflow?.minRequiredReturnPhotos ?? 3;
  const uploaded = workflow?.returnPhotoCount ?? 0;
  const baselineUploaded = workflow?.baselinePhotoCount ?? 0;
  const hasStep1 = baselineUploaded > 0;
  const hasStep2 = uploaded >= minPhotos;
  const canRunAi = hasStep1 && hasStep2;
  const canSubmit = uploaded >= minPhotos && !workflow?.returnRequested;

  return (
    <div className="return-photos-page">
      <div className="container return-photos-container">
        <div className="return-photos-header">
          <h1>Poze înainte de retur</h1>
          <p>Încarcă minim {minPhotos} imagini, apoi trimite solicitarea de retur.</p>
          <div className="header-stepper" aria-label="Pași retur">
            <div className={`header-step ${hasStep1 ? 'is-done' : 'is-active'}`}>
              <span className="step-index">1</span>
              <span className="step-label">Încarcă poze primite</span>
            </div>
            <div className={`header-step ${hasStep2 ? 'is-done' : 'is-active'}`}>
              <span className="step-index">2</span>
              <span className="step-label">Încarcă poze predare</span>
            </div>
            <div className={`header-step ${canRunAi ? 'is-active' : ''}`}>
              <span className="step-index">3</span>
              <span className="step-label">Rulează AI</span>
            </div>
            <div className={`header-step ${workflow?.returnRequested ? 'is-done' : ''}`}>
              <span className="step-index">4</span>
              <span className="step-label">Trimite retur</span>
            </div>
            <div className={`header-step ${workflow?.returnRequested ? 'is-active' : ''}`}>
              <span className="step-index">5</span>
              <span className="step-label">Review SuperOwner</span>
            </div>
          </div>
          <div className="return-photos-actions">
            <Link to={`/rentals/${rentalId}`} className="btn btn-secondary btn-sm">
              Înapoi la închiriere
            </Link>
            <Link to={`/rentals/${rentalId}/start-photos`} className="btn btn-outline btn-sm">
              Vezi poze de predare
            </Link>
          </div>
        </div>

        <section className="return-card">
          <h2>{rental?.productName || rental?.inventoryUnit?.product?.name || 'Produs'}</h2>
          <p className="return-card-sub">
            Poze încărcate: <strong>{uploaded}</strong> / minim <strong>{minPhotos}</strong>
          </p>

          <label className="upload-zone">
            <input
              type="file"
              accept="image/*"
              multiple
              disabled={isUploading || Boolean(workflow?.returnRequested)}
              onChange={(e) => void handleFiles(e.target.files)}
            />
            <span>{isUploading ? 'Se încarcă...' : 'Alege poze pentru retur'}</span>
          </label>

          <div className="submit-row">
            <button
              type="button"
              className="btn btn-outline"
              disabled={!canRunAi || isVerifying}
              onClick={() => void handleRunVerification()}
            >
              {isVerifying ? 'Rulează AI...' : 'Rulează verificare AI'}
            </button>
            <button
              type="button"
              className="btn btn-primary"
              disabled={!canSubmit || isSubmitting}
              onClick={() => void handleSubmitReturn()}
            >
              {isSubmitting ? 'Se trimite...' : 'Trimite solicitare retur'}
            </button>
            {workflow?.returnRequested && (
              <span className="pending-tag">Solicitare trimisă. Așteaptă verificarea SuperOwner.</span>
            )}
          </div>

          {message && <div className="message success">{message}</div>}
          {error && <div className="message error">{error}</div>}
          {!canRunAi && (
            <div className="message info">
              Rulează AI devine disponibil la pasul 3, după ce ai poze la pașii 1 și 2.
            </div>
          )}

          {workflow?.latestVerification && (
            <div className="verification-panel">
              <h3>Raport AI retur</h3>
              <p>
                Verdict: <strong>{workflow.latestVerification.verdict}</strong> · Deteriorare nouă:{' '}
                <strong>{workflow.latestVerification.newDamageScore?.toFixed(3) ?? 'n/a'}</strong>
              </p>
              <p>
                OCR: {workflow.latestVerification.ocrText ? workflow.latestVerification.ocrText.slice(0, 140) : 'n/a'}
              </p>
            </div>
          )}

          <div className="images-grid">
            {(workflow?.returnPhotos ?? []).map((img) => (
              <a key={img.id} href={getImageUrl(img.imageUrl)} target="_blank" rel="noreferrer" className="img-tile">
                <img src={getImageUrl(img.imageUrl)} alt="Poză de retur" />
              </a>
            ))}
          </div>
        </section>
      </div>
    </div>
  );
};

export default RentalReturnPhotosPage;

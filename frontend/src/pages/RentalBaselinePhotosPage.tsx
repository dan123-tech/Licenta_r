import React, { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { rentalService } from '../services/rentalService';
import { Rental, RentalReturnWorkflow } from '../types';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { getImageUrl } from '../utils/imageHelper';
import './RentalReturnPhotosPage.css';

const RentalBaselinePhotosPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const rentalId = Number(id);
  const [rental, setRental] = useState<Rental | null>(null);
  const [workflow, setWorkflow] = useState<RentalReturnWorkflow | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isUploading, setIsUploading] = useState(false);
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
    if (!files?.length) {
      return;
    }
    try {
      setIsUploading(true);
      setMessage('');
      setError('');
      for (const file of Array.from(files)) {
        await rentalService.uploadBaselinePhoto(rentalId, file);
      }
      setMessage('Pozele de predare au fost încărcate.');
      await loadData();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Eroare la încărcare.');
    } finally {
      setIsUploading(false);
    }
  };

  const runVerification = async () => {
    try {
      setIsVerifying(true);
      setMessage('');
      setError('');
      const resp = await rentalService.runHandoverVerification(rentalId, 'BASELINE');
      setMessage(resp.message || 'Verificare AI finalizată pentru predare.');
      await loadData();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Eroare la verificarea AI.');
    } finally {
      setIsVerifying(false);
    }
  };

  if (isLoading) return <LoadingSpinner />;
  if (error && !rental) return <div className="error-message">{error}</div>;

  return (
    <div className="return-photos-page">
      <div className="container return-photos-container">
        <div className="return-photos-header">
          <h1>Poze de predare</h1>
          <p>
            Încarcă imagini clare la începutul închirierii pentru comparația AI de la retur.
          </p>
          <div className="return-photos-actions">
            <Link to={`/rentals/${rentalId}`} className="btn btn-secondary btn-sm">
              Înapoi la închiriere
            </Link>
            <Link to={`/rentals/${rentalId}/return-photos`} className="btn btn-primary btn-sm">
              Mergi la poze de retur
            </Link>
          </div>
        </div>

        <section className="return-card">
          <div className="wizard-steps">
            <span className="wizard-step is-active">1. Upload poze</span>
            <span className="wizard-step">2. Rulează AI</span>
            <span className="wizard-step">3. Rezultat</span>
          </div>
          <h2>{rental?.productName || rental?.inventoryUnit?.product?.name || 'Produs'}</h2>
          <p className="return-card-sub">
            Poze baseline încărcate: <strong>{workflow?.baselinePhotoCount ?? 0}</strong>
          </p>

          <label className="upload-zone">
            <input
              type="file"
              accept="image/*"
              multiple
              disabled={isUploading}
              onChange={(e) => void handleFiles(e.target.files)}
            />
            <span>{isUploading ? 'Se încarcă...' : 'Alege poze de predare'}</span>
          </label>

          <div className="submit-row">
            <button
              type="button"
              className="btn btn-primary"
              disabled={isVerifying || (workflow?.baselinePhotoCount ?? 0) === 0}
              onClick={() => void runVerification()}
            >
              {isVerifying ? 'Rulează AI...' : 'Rulează verificare predare'}
            </button>
          </div>

          {message && <div className="message success">{message}</div>}
          {error && <div className="message error">{error}</div>}

          {workflow?.latestVerification && (
            <div className="verification-panel">
              <h3>Rezultat AI predare</h3>
              <p>
                Verdict: <strong>{workflow.latestVerification.verdict}</strong> · Model match:{' '}
                <strong>{workflow.latestVerification.modelMatchScore?.toFixed(3) ?? 'n/a'}</strong>
              </p>
              <p>
                Power on: <strong>{String(workflow.latestVerification.powerOnDetected ?? false)}</strong> · Error code:{' '}
                <strong>{String(workflow.latestVerification.errorCodesDetected ?? false)}</strong>
              </p>
            </div>
          )}

          <div className="images-grid">
            {workflow?.baselinePhotos.map((img) => (
              <a key={img.id} href={getImageUrl(img.imageUrl)} target="_blank" rel="noreferrer" className="img-tile">
                <img src={getImageUrl(img.imageUrl)} alt="Poză de predare" />
              </a>
            ))}
          </div>
        </section>
      </div>
    </div>
  );
};

export default RentalBaselinePhotosPage;

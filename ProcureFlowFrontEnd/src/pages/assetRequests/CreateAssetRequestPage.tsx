import { FormEvent, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { createAssetRequest } from '../../api/assetRequestApi';
import type { AssetPriority, CreateAssetRequestPayload } from '../../types/assetRequest';
import { getApiErrorMessage } from '../../utils/errorUtils';

const priorities: AssetPriority[] = ['LOW', 'MEDIUM', 'HIGH', 'URGENT'];

function CreateAssetRequestPage() {
  const navigate = useNavigate();
  const [form, setForm] = useState<CreateAssetRequestPayload>({
    assetName: '',
    quantity: 1,
    reason: '',
    priority: 'MEDIUM',
    neededByDate: '',
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const updateField = <K extends keyof CreateAssetRequestPayload>(
    field: K,
    value: CreateAssetRequestPayload[K],
  ) => {
    setForm((current) => ({ ...current, [field]: value }));
  };

  const validate = () => {
    if (!form.assetName.trim()) {
      return 'Asset name is required.';
    }

    if (form.quantity < 1) {
      return 'Quantity must be at least 1.';
    }

    if (form.reason.trim().length < 10) {
      return 'Reason must be at least 10 characters.';
    }

    if (!form.neededByDate) {
      return 'Needed by date is required.';
    }

    return '';
  };

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const validationError = validate();

    if (validationError) {
      setError(validationError);
      return;
    }

    setLoading(true);
    setError('');

    try {
      await createAssetRequest({
        ...form,
        assetName: form.assetName.trim(),
        reason: form.reason.trim(),
      });
      navigate('/asset-requests/my');
    } catch (apiError) {
      setError(getApiErrorMessage(apiError, 'Unable to create asset request.'));
    } finally {
      setLoading(false);
    }
  };

  return (
    <section className="page-section">
      <div className="page-heading">
        <div>
          <p className="eyebrow">Asset Request</p>
          <h1>Create Request</h1>
          <p>Submit a new employee asset request for approval.</p>
        </div>
      </div>

      <form className="card-form" onSubmit={handleSubmit}>
        {error && <div className="alert error">{error}</div>}

        <div className="form-grid">
          <label>
            Asset Name
            <input
              type="text"
              value={form.assetName}
              onChange={(event) => updateField('assetName', event.target.value)}
              placeholder="Laptop"
            />
          </label>

          <label>
            Quantity
            <input
              type="number"
              min="1"
              value={form.quantity}
              onChange={(event) => updateField('quantity', Number(event.target.value))}
            />
          </label>

          <label>
            Priority
            <select
              value={form.priority}
              onChange={(event) => updateField('priority', event.target.value as AssetPriority)}
            >
              {priorities.map((priority) => (
                <option key={priority} value={priority}>
                  {priority}
                </option>
              ))}
            </select>
          </label>

          <label>
            Needed By Date
            <input
              type="date"
              value={form.neededByDate}
              onChange={(event) => updateField('neededByDate', event.target.value)}
            />
          </label>
        </div>

        <label>
          Reason
          <textarea
            value={form.reason}
            onChange={(event) => updateField('reason', event.target.value)}
            placeholder="Required for development work"
            rows={5}
          />
        </label>

        <div className="form-actions">
          <button className="secondary-button" type="button" onClick={() => navigate('/dashboard')}>
            Cancel
          </button>
          <button className="primary-button" type="submit" disabled={loading}>
            {loading ? 'Submitting...' : 'Submit Request'}
          </button>
        </div>
      </form>
    </section>
  );
}

export default CreateAssetRequestPage;

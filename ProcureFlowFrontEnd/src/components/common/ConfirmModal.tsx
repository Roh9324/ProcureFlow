import type { ReactNode } from 'react';

interface ConfirmModalProps {
  title: string;
  description?: string;
  children?: ReactNode;
  confirmLabel: string;
  loading?: boolean;
  onCancel: () => void;
  onConfirm: () => void;
}

function ConfirmModal({
  title,
  description,
  children,
  confirmLabel,
  loading = false,
  onCancel,
  onConfirm,
}: ConfirmModalProps) {
  return (
    <div className="modal-backdrop">
      <div className="modal-panel">
        <h2>{title}</h2>
        {description && <p>{description}</p>}
        {children}
        <div className="form-actions">
          <button className="secondary-button" type="button" onClick={onCancel}>
            Cancel
          </button>
          <button className="primary-button" type="button" disabled={loading} onClick={onConfirm}>
            {loading ? 'Working...' : confirmLabel}
          </button>
        </div>
      </div>
    </div>
  );
}

export default ConfirmModal;

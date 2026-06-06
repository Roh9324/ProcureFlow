import { FormEvent, useEffect, useState } from 'react';
import {
  getPendingFinalApprovals,
  submitFinalApprovalDecision,
} from '../../api/finalApprovalApi';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import StatusBadge from '../../components/common/StatusBadge';
import type {
  FinalApprovalDecision,
  FinalApprovalRequest,
} from '../../types/finalApproval';
import { formatDate } from '../../utils/dateUtils';
import { getApiErrorMessage } from '../../utils/errorUtils';
import {
  getApprovalDealerName,
  getApprovalDealerRemarks,
  getApprovalDeliveryDays,
  getApprovalEmployeeEmail,
  getApprovalEmployeeName,
  getApprovalQuotedPrice,
  getApprovalRequestId,
  getApprovalWarranty,
} from './finalApprovalUtils';

interface DecisionModalState {
  request: FinalApprovalRequest;
  decision: FinalApprovalDecision;
}

function PendingApprovalsPage() {
  const [pending, setPending] = useState<FinalApprovalRequest[]>([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [decisionModal, setDecisionModal] = useState<DecisionModalState | null>(null);
  const [decisionReason, setDecisionReason] = useState('');

  const loadPending = async () => {
    setLoading(true);
    setError('');

    try {
      setPending(await getPendingFinalApprovals());
    } catch (apiError) {
      setError(getApiErrorMessage(apiError, 'Unable to load pending approvals.'));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadPending();
  }, []);

  const openDecisionModal = (
    request: FinalApprovalRequest,
    decision: FinalApprovalDecision,
  ) => {
    setDecisionModal({ request, decision });
    setDecisionReason('');
  };

  const handleDecisionSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    if (!decisionModal || decisionReason.trim().length < 5) {
      setError('Enter a decision reason before submitting.');
      return;
    }

    const requestId = getApprovalRequestId(decisionModal.request);

    setSubmitting(true);
    setError('');
    setSuccess('');

    try {
      await submitFinalApprovalDecision(requestId, {
        decision: decisionModal.decision,
        reason: decisionReason.trim(),
      });
      setSuccess(`Request ${decisionModal.decision.toLowerCase()} successfully.`);
      setDecisionModal(null);
      setDecisionReason('');
      await loadPending();
    } catch (apiError) {
      setError(getApiErrorMessage(apiError, 'Unable to submit final decision.'));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <section className="page-section">
      <div className="page-heading">
        <div>
          <p className="eyebrow">Pending Approvals</p>
          <h1>Final Approval Queue</h1>
          <p>Approve or reject dealer-quoted asset requests.</p>
        </div>
      </div>

      {error && <div className="alert error">{error}</div>}
      {success && <div className="alert success">{success}</div>}

      <div className="table-card">
        {loading ? (
          <LoadingSpinner label="Loading pending approvals..." />
        ) : pending.length === 0 ? (
          <div className="empty-state">No pending approvals found.</div>
        ) : (
          <div className="table-scroll">
            <table>
              <thead>
                <tr>
                  <th>Request ID</th>
                  <th>Employee Name</th>
                  <th>Employee Email</th>
                  <th>Asset Name</th>
                  <th>Qty</th>
                  <th>Reason</th>
                  <th>Priority</th>
                  <th>Needed By</th>
                  <th>Dealer</th>
                  <th>Quoted Price</th>
                  <th>Delivery Days</th>
                  <th>Warranty</th>
                  <th>Dealer Remarks</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {pending.map((request) => (
                  <tr key={getApprovalRequestId(request)}>
                    <td>{getApprovalRequestId(request)}</td>
                    <td>{getApprovalEmployeeName(request)}</td>
                    <td>{getApprovalEmployeeEmail(request)}</td>
                    <td>{request.assetName}</td>
                    <td>{request.quantity}</td>
                    <td>{request.reason || '-'}</td>
                    <td>
                      <StatusBadge status={request.priority} />
                    </td>
                    <td>{formatDate(request.neededByDate)}</td>
                    <td>{getApprovalDealerName(request)}</td>
                    <td>{getApprovalQuotedPrice(request) ?? '-'}</td>
                    <td>{getApprovalDeliveryDays(request) ?? '-'}</td>
                    <td>{getApprovalWarranty(request)}</td>
                    <td>{getApprovalDealerRemarks(request)}</td>
                    <td>
                      <div className="table-actions">
                        <button
                          className="small-button"
                          type="button"
                          onClick={() => openDecisionModal(request, 'APPROVED')}
                        >
                          Approve
                        </button>
                        <button
                          className="small-button secondary"
                          type="button"
                          onClick={() => openDecisionModal(request, 'REJECTED')}
                        >
                          Reject
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {decisionModal && (
        <div className="modal-backdrop">
          <form className="modal-panel" onSubmit={handleDecisionSubmit}>
            <h2>{decisionModal.decision === 'APPROVED' ? 'Approve Request' : 'Reject Request'}</h2>
            <p>
              Request #{getApprovalRequestId(decisionModal.request)} -{' '}
              {decisionModal.request.assetName}
            </p>
            <label>
              Reason
              <textarea
                rows={4}
                value={decisionReason}
                onChange={(event) => setDecisionReason(event.target.value)}
              />
            </label>
            <div className="form-actions">
              <button
                className="secondary-button"
                type="button"
                onClick={() => setDecisionModal(null)}
              >
                Cancel
              </button>
              <button className="primary-button" type="submit" disabled={submitting}>
                {submitting ? 'Submitting...' : 'Submit Decision'}
              </button>
            </div>
          </form>
        </div>
      )}
    </section>
  );
}

export default PendingApprovalsPage;

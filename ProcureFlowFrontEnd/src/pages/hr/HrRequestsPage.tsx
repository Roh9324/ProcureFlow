import { FormEvent, useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import {
  closeAssetRequest,
  getAllAssetRequests,
  getFinalApprovedRequests,
  getFinalRejectedRequests,
  markDelivered,
  notifyEmployee,
  sendForFinalApproval,
  sendOrderToDealer,
  startAssetRequestReview,
} from '../../api/assetRequestApi';
import { createDealerQuotation } from '../../api/dealerQuotationApi';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import StatusBadge from '../../components/common/StatusBadge';
import type { AssetRequest } from '../../types/assetRequest';
import type { DealerQuotationPayload } from '../../types/dealerQuotation';
import { formatDate } from '../../utils/dateUtils';
import { getApiErrorMessage } from '../../utils/errorUtils';

type HrRequestMode = 'all' | 'finalApproved' | 'finalRejected';

interface HrRequestsPageProps {
  mode?: HrRequestMode;
}

const getEmployeeName = (request: AssetRequest) =>
  request.employeeName ?? request.employee?.name ?? request.user?.name ?? '-';

const getEmployeeEmail = (request: AssetRequest) =>
  request.employeeEmail ?? request.employee?.email ?? request.user?.email ?? '-';

const pageCopy = {
  all: {
    eyebrow: 'HR Requests',
    title: 'All Employee Requests',
    description: 'Process submitted requests and collect dealer quotation details.',
    loading: 'Loading requests...',
  },
  finalApproved: {
    eyebrow: 'Final Approved',
    title: 'Final Approved Requests',
    description: 'Notify employees, place dealer orders, and complete approved requests.',
    loading: 'Loading final approved requests...',
  },
  finalRejected: {
    eyebrow: 'Final Rejected',
    title: 'Final Rejected Requests',
    description: 'Notify employees and close rejected asset requests.',
    loading: 'Loading final rejected requests...',
  },
};

function HrRequestsPage({ mode = 'all' }: HrRequestsPageProps) {
  const [requests, setRequests] = useState<AssetRequest[]>([]);
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [reviewRequest, setReviewRequest] = useState<AssetRequest | null>(null);
  const [quotationRequest, setQuotationRequest] = useState<AssetRequest | null>(null);
  const [hrRemarks, setHrRemarks] = useState('');
  const [quotation, setQuotation] = useState<DealerQuotationPayload>({
    dealerName: '',
    quotedPrice: 0,
    deliveryDays: 1,
    warrantyDetails: '',
    dealerRemarks: '',
  });

  const copy = pageCopy[mode];

  const loadRequests = useCallback(async () => {
    setLoading(true);
    setError('');

    try {
      if (mode === 'finalApproved') {
        setRequests(await getFinalApprovedRequests());
      } else if (mode === 'finalRejected') {
        setRequests(await getFinalRejectedRequests());
      } else {
        setRequests(await getAllAssetRequests());
      }
    } catch (apiError) {
      setError(getApiErrorMessage(apiError, 'Unable to load employee requests.'));
    } finally {
      setLoading(false);
    }
  }, [mode]);

  useEffect(() => {
    loadRequests();
  }, [loadRequests]);

  const runAction = async (requestId: number, action: () => Promise<unknown>, message: string) => {
    setActionLoading(true);
    setError('');
    setSuccess('');

    try {
      await action();
      setSuccess(message);
      await loadRequests();
    } catch (apiError) {
      setError(getApiErrorMessage(apiError, 'Unable to update request.'));
    } finally {
      setActionLoading(false);
    }
  };

  const handleStartReview = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    if (!reviewRequest || hrRemarks.trim().length < 3) {
      setError('Enter HR remarks before starting review.');
      return;
    }

    setActionLoading(true);
    setError('');
    setSuccess('');

    try {
      await startAssetRequestReview(reviewRequest.id, { hrRemarks: hrRemarks.trim() });
      setSuccess('Request moved to HR review.');
      setReviewRequest(null);
      setHrRemarks('');
      await loadRequests();
    } catch (apiError) {
      setError(getApiErrorMessage(apiError, 'Unable to start HR review.'));
    } finally {
      setActionLoading(false);
    }
  };

  const handleQuotationSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    if (!quotationRequest || !quotation.dealerName.trim() || quotation.quotedPrice <= 0) {
      setError('Enter dealer name and a valid quoted price.');
      return;
    }

    setActionLoading(true);
    setError('');
    setSuccess('');

    try {
      await createDealerQuotation(quotationRequest.id, {
        ...quotation,
        dealerName: quotation.dealerName.trim(),
        warrantyDetails: quotation.warrantyDetails.trim(),
        dealerRemarks: quotation.dealerRemarks.trim(),
      });
      setSuccess('Dealer quotation added.');
      setQuotationRequest(null);
      setQuotation({
        dealerName: '',
        quotedPrice: 0,
        deliveryDays: 1,
        warrantyDetails: '',
        dealerRemarks: '',
      });
      await loadRequests();
    } catch (apiError) {
      setError(getApiErrorMessage(apiError, 'Unable to add dealer quotation.'));
    } finally {
      setActionLoading(false);
    }
  };

  const renderActions = (request: AssetRequest) => {
    const status = request.status;

    return (
      <div className="table-actions">
        {status === 'REQUEST_SUBMITTED' && (
          <button
            className="small-button"
            type="button"
            disabled={actionLoading}
            onClick={() => {
              setReviewRequest(request);
              setHrRemarks(request.hrRemarks ?? '');
            }}
          >
            Start Review
          </button>
        )}
        {status === 'UNDER_HR_REVIEW' && (
          <button
            className="small-button secondary"
            type="button"
            disabled={actionLoading}
            onClick={() => setQuotationRequest(request)}
          >
            Add Quotation
          </button>
        )}
        {status === 'DEALER_QUOTATION_RECEIVED' && (
          <button
            className="small-button"
            type="button"
            disabled={actionLoading}
            onClick={() =>
              runAction(
                request.id,
                () => sendForFinalApproval(request.id),
                'Request sent for final approval.',
              )
            }
          >
            Send For Final Approval
          </button>
        )}
        {status === 'SENT_FOR_FINAL_APPROVAL' && (
          <span className="workflow-note">Waiting For Final Approver</span>
        )}
        {['FINAL_APPROVED', 'FINAL_REJECTED'].includes(status) && (
          <button
            className="small-button"
            type="button"
            disabled={actionLoading}
            onClick={() =>
              runAction(request.id, () => notifyEmployee(request.id), 'Employee notified.')
            }
          >
            Notify Employee
          </button>
        )}
        {status === 'EMPLOYEE_NOTIFIED' && (
          <button
            className="small-button secondary"
            type="button"
            disabled={actionLoading}
            onClick={() =>
              runAction(
                request.id,
                () => sendOrderToDealer(request.id),
                'Order sent to dealer.',
              )
            }
          >
            Send Order To Dealer
          </button>
        )}
        {status === 'EMPLOYEE_NOTIFIED' && (
          <button
            className="small-button secondary"
            type="button"
            disabled={actionLoading}
            onClick={() =>
              runAction(request.id, () => closeAssetRequest(request.id), 'Request closed.')
            }
          >
            Close Request
          </button>
        )}
        {status === 'ORDER_SENT_TO_DEALER' && (
          <button
            className="small-button"
            type="button"
            disabled={actionLoading}
            onClick={() =>
              runAction(request.id, () => markDelivered(request.id), 'Request marked delivered.')
            }
          >
            Mark Delivered
          </button>
        )}
        {status === 'DELIVERED' && (
          <button
            className="small-button secondary"
            type="button"
            disabled={actionLoading}
            onClick={() =>
              runAction(request.id, () => closeAssetRequest(request.id), 'Request closed.')
            }
          >
            Close Request
          </button>
        )}
        {status === 'CLOSED' && <span className="workflow-note complete">Closed</span>}
        <Link className="small-button link-button" to={`/hr/requests/${request.id}/history`}>
          View Timeline
        </Link>
      </div>
    );
  };

  return (
    <section className="page-section">
      <div className="page-heading">
        <div>
          <p className="eyebrow">{copy.eyebrow}</p>
          <h1>{copy.title}</h1>
          <p>{copy.description}</p>
        </div>
      </div>

      {error && <div className="alert error">{error}</div>}
      {success && <div className="alert success">{success}</div>}

      <div className="table-card">
        {loading ? (
          <LoadingSpinner label={copy.loading} />
        ) : requests.length === 0 ? (
          <div className="empty-state">No requests found.</div>
        ) : (
          <div className="table-scroll">
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Employee Name</th>
                  <th>Employee Email</th>
                  <th>Asset Name</th>
                  <th>Qty</th>
                  <th>Priority</th>
                  <th>Needed By</th>
                  <th>Status</th>
                  <th>HR Remarks</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {requests.map((request) => (
                  <tr key={request.id}>
                    <td>{request.id}</td>
                    <td>{getEmployeeName(request)}</td>
                    <td>{getEmployeeEmail(request)}</td>
                    <td>{request.assetName}</td>
                    <td>{request.quantity}</td>
                    <td>{request.priority}</td>
                    <td>{formatDate(request.neededByDate)}</td>
                    <td>
                      <StatusBadge status={request.status} />
                    </td>
                    <td>{request.hrRemarks || '-'}</td>
                    <td>{renderActions(request)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {reviewRequest && (
        <div className="modal-backdrop">
          <form className="modal-panel" onSubmit={handleStartReview}>
            <h2>Start HR Review</h2>
            <p>
              Request #{reviewRequest.id} - {reviewRequest.assetName}
            </p>
            <label>
              HR Remarks
              <textarea
                value={hrRemarks}
                onChange={(event) => setHrRemarks(event.target.value)}
                rows={4}
              />
            </label>
            <div className="form-actions">
              <button
                className="secondary-button"
                type="button"
                onClick={() => setReviewRequest(null)}
              >
                Cancel
              </button>
              <button className="primary-button" type="submit" disabled={actionLoading}>
                {actionLoading ? 'Saving...' : 'Start Review'}
              </button>
            </div>
          </form>
        </div>
      )}

      {quotationRequest && (
        <div className="modal-backdrop">
          <form className="modal-panel" onSubmit={handleQuotationSubmit}>
            <h2>Add Dealer Quotation</h2>
            <p>
              Request #{quotationRequest.id} - {quotationRequest.assetName}
            </p>
            <div className="form-grid">
              <label>
                Dealer Name
                <input
                  value={quotation.dealerName}
                  onChange={(event) =>
                    setQuotation((current) => ({ ...current, dealerName: event.target.value }))
                  }
                />
              </label>
              <label>
                Quoted Price
                <input
                  type="number"
                  min="1"
                  value={quotation.quotedPrice}
                  onChange={(event) =>
                    setQuotation((current) => ({
                      ...current,
                      quotedPrice: Number(event.target.value),
                    }))
                  }
                />
              </label>
              <label>
                Delivery Days
                <input
                  type="number"
                  min="1"
                  value={quotation.deliveryDays}
                  onChange={(event) =>
                    setQuotation((current) => ({
                      ...current,
                      deliveryDays: Number(event.target.value),
                    }))
                  }
                />
              </label>
              <label>
                Warranty Details
                <input
                  value={quotation.warrantyDetails}
                  onChange={(event) =>
                    setQuotation((current) => ({
                      ...current,
                      warrantyDetails: event.target.value,
                    }))
                  }
                />
              </label>
            </div>
            <label>
              Dealer Remarks
              <textarea
                value={quotation.dealerRemarks}
                onChange={(event) =>
                  setQuotation((current) => ({ ...current, dealerRemarks: event.target.value }))
                }
                rows={3}
              />
            </label>
            <div className="form-actions">
              <button
                className="secondary-button"
                type="button"
                onClick={() => setQuotationRequest(null)}
              >
                Cancel
              </button>
              <button className="primary-button" type="submit" disabled={actionLoading}>
                {actionLoading ? 'Saving...' : 'Add Quotation'}
              </button>
            </div>
          </form>
        </div>
      )}
    </section>
  );
}

export default HrRequestsPage;

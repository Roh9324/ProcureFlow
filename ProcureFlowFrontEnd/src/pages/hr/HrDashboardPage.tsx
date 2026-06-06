import { useEffect, useMemo, useState } from 'react';
import { getAllAssetRequests } from '../../api/assetRequestApi';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import StatusBadge from '../../components/common/StatusBadge';
import type { AssetRequest } from '../../types/assetRequest';
import { formatDate } from '../../utils/dateUtils';
import { getApiErrorMessage } from '../../utils/errorUtils';

function HrDashboardPage() {
  const [requests, setRequests] = useState<AssetRequest[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const loadRequests = async () => {
      setLoading(true);
      setError('');

      try {
        setRequests(await getAllAssetRequests());
      } catch (apiError) {
        setError(getApiErrorMessage(apiError, 'Unable to load HR dashboard.'));
      } finally {
        setLoading(false);
      }
    };

    loadRequests();
  }, []);

  const counts = useMemo(
    () => ({
      total: requests.length,
      submitted: requests.filter((request) =>
        ['REQUEST_SUBMITTED', 'SUBMITTED', 'PENDING'].includes(request.status),
      ).length,
      underReview: requests.filter((request) => request.status === 'UNDER_HR_REVIEW').length,
      quotations: requests.filter((request) => request.status === 'DEALER_QUOTATION_RECEIVED')
        .length,
      sentForFinalApproval: requests.filter(
        (request) => request.status === 'SENT_FOR_FINAL_APPROVAL',
      ).length,
      finalApproved: requests.filter((request) => request.status === 'FINAL_APPROVED').length,
      finalRejected: requests.filter((request) => request.status === 'FINAL_REJECTED').length,
      closed: requests.filter((request) => request.status === 'CLOSED').length,
    }),
    [requests],
  );

  return (
    <section className="page-section">
      <div className="page-heading">
        <div>
          <p className="eyebrow">HR Operations</p>
          <h1>HR Dashboard</h1>
          <p>Review employee requests and coordinate dealer quotations.</p>
        </div>
      </div>

      {error && <div className="alert error">{error}</div>}
      {loading ? (
        <LoadingSpinner label="Loading HR dashboard..." />
      ) : (
        <>
          <div className="metric-grid">
            <article className="metric-card">
              <span>Total Employee Requests</span>
              <strong>{counts.total}</strong>
            </article>
            <article className="metric-card">
              <span>Submitted Requests</span>
              <strong>{counts.submitted}</strong>
            </article>
            <article className="metric-card">
              <span>Under Review</span>
              <strong>{counts.underReview}</strong>
            </article>
            <article className="metric-card">
              <span>Dealer Quotations</span>
              <strong>{counts.quotations}</strong>
            </article>
            <article className="metric-card">
              <span>Sent For Final Approval</span>
              <strong>{counts.sentForFinalApproval}</strong>
            </article>
            <article className="metric-card">
              <span>Final Approved</span>
              <strong>{counts.finalApproved}</strong>
            </article>
            <article className="metric-card">
              <span>Final Rejected</span>
              <strong>{counts.finalRejected}</strong>
            </article>
            <article className="metric-card">
              <span>Closed</span>
              <strong>{counts.closed}</strong>
            </article>
          </div>

          <div className="table-card">
            <div className="section-title">
              <h2>Recent Requests</h2>
            </div>
            {requests.length === 0 ? (
              <div className="empty-state">No requests found.</div>
            ) : (
              <div className="table-scroll">
                <table>
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Asset</th>
                      <th>Priority</th>
                      <th>Needed By</th>
                      <th>Status</th>
                    </tr>
                  </thead>
                  <tbody>
                    {requests.slice(0, 6).map((request) => (
                      <tr key={request.id}>
                        <td>{request.id}</td>
                        <td>{request.assetName}</td>
                        <td>{request.priority}</td>
                        <td>{formatDate(request.neededByDate)}</td>
                        <td>
                          <StatusBadge status={request.status} />
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </>
      )}
    </section>
  );
}

export default HrDashboardPage;

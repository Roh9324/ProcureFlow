import { useEffect, useMemo, useState } from 'react';
import { getPendingFinalApprovals } from '../../api/finalApprovalApi';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import StatusBadge from '../../components/common/StatusBadge';
import type { FinalApprovalRequest } from '../../types/finalApproval';
import { formatDate } from '../../utils/dateUtils';
import { getApiErrorMessage } from '../../utils/errorUtils';
import {
  getApprovalEmployeeName,
  getApprovalQuotedPrice,
  getApprovalRequestId,
} from './finalApprovalUtils';

function ApproverDashboardPage() {
  const [pending, setPending] = useState<FinalApprovalRequest[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
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

    loadPending();
  }, []);

  const counts = useMemo(
    () => ({
      pending: pending.length,
      totalQuotedValue: pending.reduce(
        (sum, request) => sum + (getApprovalQuotedPrice(request) ?? 0),
        0,
      ),
      highPriority: pending.filter((request) => ['HIGH', 'URGENT'].includes(request.priority))
        .length,
    }),
    [pending],
  );

  return (
    <section className="page-section">
      <div className="page-heading">
        <div>
          <p className="eyebrow">Final Approval</p>
          <h1>Approver Dashboard</h1>
          <p>Review quoted requests and make final business decisions.</p>
        </div>
      </div>

      {error && <div className="alert error">{error}</div>}
      {loading ? (
        <LoadingSpinner label="Loading approver dashboard..." />
      ) : (
        <>
          <div className="metric-grid">
            <article className="metric-card">
              <span>Pending Approvals</span>
              <strong>{counts.pending}</strong>
            </article>
            <article className="metric-card">
              <span>Total Quoted Value</span>
              <strong>{counts.totalQuotedValue}</strong>
            </article>
            <article className="metric-card">
              <span>High Priority Requests</span>
              <strong>{counts.highPriority}</strong>
            </article>
            <article className="metric-card">
              <span>Recent Pending</span>
              <strong>{pending.slice(0, 5).length}</strong>
            </article>
          </div>

          <div className="table-card">
            <div className="section-title">
              <h2>Recent Pending Approvals</h2>
            </div>
            {pending.length === 0 ? (
              <div className="empty-state">No pending approvals found.</div>
            ) : (
              <div className="table-scroll">
                <table>
                  <thead>
                    <tr>
                      <th>Request ID</th>
                      <th>Employee</th>
                      <th>Asset</th>
                      <th>Priority</th>
                      <th>Needed By</th>
                    </tr>
                  </thead>
                  <tbody>
                    {pending.slice(0, 5).map((request) => (
                      <tr key={getApprovalRequestId(request)}>
                        <td>{getApprovalRequestId(request)}</td>
                        <td>{getApprovalEmployeeName(request)}</td>
                        <td>{request.assetName}</td>
                        <td>
                          <StatusBadge status={request.priority} />
                        </td>
                        <td>{formatDate(request.neededByDate)}</td>
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

export default ApproverDashboardPage;

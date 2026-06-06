import { useEffect, useMemo, useState } from 'react';
import { getMyAssetRequests } from '../../api/assetRequestApi';
import { getCurrentUser } from '../../api/userApi';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import StatusBadge from '../../components/common/StatusBadge';
import type { AssetRequest } from '../../types/assetRequest';
import type { UserProfile } from '../../types/user';
import { formatDate } from '../../utils/dateUtils';
import { getApiErrorMessage } from '../../utils/errorUtils';
import { getStoredUser } from '../../utils/tokenStorage';

function EmployeeDashboardPage() {
  const storedUser = getStoredUser();
  const [user, setUser] = useState<UserProfile | null>(
    storedUser ? { ...storedUser, active: true, emailVerified: true } : null,
  );
  const [requests, setRequests] = useState<AssetRequest[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const loadDashboard = async () => {
      setLoading(true);
      setError('');

      try {
        const [profile, myRequests] = await Promise.all([
          getCurrentUser(),
          getMyAssetRequests(),
        ]);
        setUser(profile);
        setRequests(myRequests);
      } catch (apiError) {
        setError(getApiErrorMessage(apiError, 'Unable to load dashboard data.'));
      } finally {
        setLoading(false);
      }
    };

    loadDashboard();
  }, []);

  const counts = useMemo(() => {
    const submitted = requests.filter((request) =>
      ['REQUEST_SUBMITTED', 'SUBMITTED', 'PENDING'].includes(request.status),
    ).length;
    const underReview = requests.filter((request) => request.status === 'UNDER_HR_REVIEW').length;
    const approved = requests.filter((request) =>
      ['APPROVED', 'FINAL_APPROVED', 'DELIVERED', 'CLOSED'].includes(request.status),
    ).length;
    const rejected = requests.filter((request) =>
      ['REJECTED', 'FINAL_REJECTED'].includes(request.status),
    ).length;

    return { total: requests.length, submitted, underReview, approved, rejected };
  }, [requests]);

  return (
    <section className="page-section">
      <div className="page-heading">
        <div>
          <p className="eyebrow">Employee Dashboard</p>
          <h1>Welcome, {user?.name ?? 'Employee'}</h1>
          <p>Track your asset requests from submission through approval.</p>
        </div>
      </div>

      {error && <div className="alert error">{error}</div>}
      {loading ? (
        <LoadingSpinner label="Loading dashboard..." />
      ) : (
        <>
          <div className="metric-grid">
            <article className="metric-card">
              <span>Total Requests</span>
              <strong>{counts.total}</strong>
            </article>
            <article className="metric-card">
              <span>Submitted Requests</span>
              <strong>{counts.submitted}</strong>
            </article>
            <article className="metric-card">
              <span>Under HR Review</span>
              <strong>{counts.underReview}</strong>
            </article>
            <article className="metric-card">
              <span>Approved / Rejected</span>
              <strong>
                {counts.approved} / {counts.rejected}
              </strong>
            </article>
          </div>

          <div className="table-card">
            <div className="section-title">
              <h2>Recent Requests</h2>
            </div>
            {requests.length === 0 ? (
              <div className="empty-state">No recent requests found.</div>
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
                    {requests.slice(0, 5).map((request) => (
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

export default EmployeeDashboardPage;

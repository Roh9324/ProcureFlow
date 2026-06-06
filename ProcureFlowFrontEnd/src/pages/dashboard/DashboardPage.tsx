import { useEffect, useMemo, useState } from 'react';
import { getMyAssetRequests } from '../../api/assetRequestApi';
import { getCurrentUser } from '../../api/userApi';
import type { AssetRequest } from '../../types/assetRequest';
import type { UserProfile } from '../../types/user';
import { getApiErrorMessage } from '../../utils/errorUtils';
import { getStoredUser } from '../../utils/tokenStorage';

function DashboardPage() {
  const storedUser = getStoredUser();
  const [user, setUser] = useState<UserProfile | null>(
    storedUser
      ? {
          ...storedUser,
          active: true,
          emailVerified: true,
        }
      : null,
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
    const approved = requests.filter((request) => request.status === 'APPROVED').length;
    const rejected = requests.filter((request) => request.status === 'REJECTED').length;
    const submitted = requests.filter((request) =>
      ['SUBMITTED', 'PENDING'].includes(request.status),
    ).length;

    return {
      total: requests.length,
      submitted,
      approved,
      rejected,
    };
  }, [requests]);

  return (
    <section className="page-section">
      <div className="page-heading">
        <div>
          <p className="eyebrow">Dashboard</p>
          <h1>Welcome, {user?.name ?? 'Employee'}</h1>
          <p>Role: {user?.role ?? 'EMPLOYEE'}</p>
        </div>
      </div>

      {error && <div className="alert error">{error}</div>}
      {loading ? (
        <div className="loading-box">Loading dashboard...</div>
      ) : (
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
            <span>Approved Requests</span>
            <strong>{counts.approved}</strong>
          </article>
          <article className="metric-card">
            <span>Rejected Requests</span>
            <strong>{counts.rejected}</strong>
          </article>
        </div>
      )}
    </section>
  );
}

export default DashboardPage;

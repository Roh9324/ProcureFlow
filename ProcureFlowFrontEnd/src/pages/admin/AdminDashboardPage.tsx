import { useEffect, useMemo, useState } from 'react';
import { getAdminUsers } from '../../api/adminApi';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import type { AdminUser } from '../../types/admin';
import { getApiErrorMessage } from '../../utils/errorUtils';

function AdminDashboardPage() {
  const [users, setUsers] = useState<AdminUser[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const loadUsers = async () => {
      setLoading(true);
      setError('');

      try {
        setUsers(await getAdminUsers());
      } catch (apiError) {
        setError(getApiErrorMessage(apiError, 'Unable to load admin dashboard.'));
      } finally {
        setLoading(false);
      }
    };

    loadUsers();
  }, []);

  const counts = useMemo(
    () => ({
      total: users.length,
      active: users.filter((user) => user.active).length,
      verified: users.filter((user) => user.emailVerified).length,
      admins: users.filter((user) => user.role === 'ADMIN').length,
      hr: users.filter((user) => user.role === 'HR_MANAGER').length,
      approvers: users.filter((user) => user.role === 'FINAL_APPROVER').length,
      employees: users.filter((user) => user.role === 'EMPLOYEE').length,
    }),
    [users],
  );

  return (
    <section className="page-section">
      <div className="page-heading">
        <div>
          <p className="eyebrow">Administration</p>
          <h1>Admin Dashboard</h1>
          <p>Manage users, roles, and account access across ProcureFlow.</p>
        </div>
      </div>

      {error && <div className="alert error">{error}</div>}
      {loading ? (
        <LoadingSpinner label="Loading admin dashboard..." />
      ) : (
        <div className="metric-grid admin-grid">
          <article className="metric-card">
            <span>Total Users</span>
            <strong>{counts.total}</strong>
          </article>
          <article className="metric-card">
            <span>Active Users</span>
            <strong>{counts.active}</strong>
          </article>
          <article className="metric-card">
            <span>Verified Users</span>
            <strong>{counts.verified}</strong>
          </article>
          <article className="metric-card">
            <span>Admin Users</span>
            <strong>{counts.admins}</strong>
          </article>
          <article className="metric-card">
            <span>HR Users</span>
            <strong>{counts.hr}</strong>
          </article>
          <article className="metric-card">
            <span>Final Approvers</span>
            <strong>{counts.approvers}</strong>
          </article>
          <article className="metric-card">
            <span>Employee Users</span>
            <strong>{counts.employees}</strong>
          </article>
        </div>
      )}
    </section>
  );
}

export default AdminDashboardPage;

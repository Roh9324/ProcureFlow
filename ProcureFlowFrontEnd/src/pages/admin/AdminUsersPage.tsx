import { useEffect, useState } from 'react';
import {
  getAdminUsers,
  updateAdminUserRole,
  updateAdminUserStatus,
} from '../../api/adminApi';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import type { AdminUser } from '../../types/admin';
import type { UserRole } from '../../types/auth';
import { getApiErrorMessage } from '../../utils/errorUtils';

const roles: UserRole[] = ['EMPLOYEE', 'HR_MANAGER', 'FINAL_APPROVER', 'ADMIN'];

function AdminUsersPage() {
  const [users, setUsers] = useState<AdminUser[]>([]);
  const [loading, setLoading] = useState(true);
  const [updatingUserId, setUpdatingUserId] = useState<number | null>(null);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const loadUsers = async () => {
    setLoading(true);
    setError('');

    try {
      setUsers(await getAdminUsers());
    } catch (apiError) {
      setError(getApiErrorMessage(apiError, 'Unable to load users.'));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadUsers();
  }, []);

  const handleRoleChange = async (userId: number, role: UserRole) => {
    setUpdatingUserId(userId);
    setError('');
    setSuccess('');

    try {
      await updateAdminUserRole(userId, { role });
      setSuccess('User role updated.');
      await loadUsers();
    } catch (apiError) {
      setError(getApiErrorMessage(apiError, 'Unable to update user role.'));
    } finally {
      setUpdatingUserId(null);
    }
  };

  const handleStatusChange = async (userId: number, active: boolean) => {
    setUpdatingUserId(userId);
    setError('');
    setSuccess('');

    try {
      await updateAdminUserStatus(userId, { active });
      setSuccess('User status updated.');
      await loadUsers();
    } catch (apiError) {
      setError(getApiErrorMessage(apiError, 'Unable to update user status.'));
    } finally {
      setUpdatingUserId(null);
    }
  };

  return (
    <section className="page-section">
      <div className="page-heading">
        <div>
          <p className="eyebrow">User Management</p>
          <h1>Users</h1>
          <p>Update account roles and active status.</p>
        </div>
      </div>

      {error && <div className="alert error">{error}</div>}
      {success && <div className="alert success">{success}</div>}

      <div className="table-card">
        {loading ? (
          <LoadingSpinner label="Loading users..." />
        ) : users.length === 0 ? (
          <div className="empty-state">No users found.</div>
        ) : (
          <div className="table-scroll">
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Name</th>
                  <th>Email</th>
                  <th>Role</th>
                  <th>Active</th>
                  <th>Email Verified</th>
                </tr>
              </thead>
              <tbody>
                {users.map((user) => (
                  <tr key={user.id}>
                    <td>{user.id}</td>
                    <td>{user.name}</td>
                    <td>{user.email}</td>
                    <td>
                      <select
                        value={user.role}
                        disabled={updatingUserId === user.id}
                        onChange={(event) =>
                          handleRoleChange(user.id, event.target.value as UserRole)
                        }
                      >
                        {roles.map((role) => (
                          <option key={role} value={role}>
                            {role}
                          </option>
                        ))}
                      </select>
                    </td>
                    <td>
                      <label className="inline-toggle">
                        <input
                          type="checkbox"
                          checked={user.active}
                          disabled={updatingUserId === user.id}
                          onChange={(event) => handleStatusChange(user.id, event.target.checked)}
                        />
                        {user.active ? 'Active' : 'Inactive'}
                      </label>
                    </td>
                    <td>{user.emailVerified ? 'Yes' : 'No'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </section>
  );
}

export default AdminUsersPage;

import { Link } from 'react-router-dom';
import { getDashboardPathForRole } from '../../utils/roleUtils';
import { getStoredUser } from '../../utils/tokenStorage';

function UnauthorizedPage() {
  const user = getStoredUser();

  return (
    <main className="auth-page">
      <section className="auth-panel">
        <div className="auth-header">
          <span className="brand-mark">PF</span>
          <h1>Unauthorized</h1>
          <p>You do not have access to this ProcureFlow area.</p>
        </div>
        <Link className="primary-button" to={getDashboardPathForRole(user?.role)}>
          Go to Dashboard
        </Link>
      </section>
    </main>
  );
}

export default UnauthorizedPage;

import { useEffect, useState } from 'react';
import { getCurrentUser } from '../../api/userApi';
import type { UserProfile } from '../../types/user';
import { getApiErrorMessage } from '../../utils/errorUtils';

function ProfilePage() {
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const loadProfile = async () => {
      setLoading(true);
      setError('');

      try {
        const data = await getCurrentUser();
        setProfile(data);
      } catch (apiError) {
        setError(getApiErrorMessage(apiError, 'Unable to load profile.'));
      } finally {
        setLoading(false);
      }
    };

    loadProfile();
  }, []);

  return (
    <section className="page-section">
      <div className="page-heading">
        <div>
          <p className="eyebrow">Account</p>
          <h1>Profile</h1>
          <p>Your current ProcureFlow account details.</p>
        </div>
      </div>

      {error && <div className="alert error">{error}</div>}

      <article className="profile-card">
        {loading ? (
          <div className="loading-box">Loading profile...</div>
        ) : profile ? (
          <dl className="profile-details">
            <div>
              <dt>Name</dt>
              <dd>{profile.name}</dd>
            </div>
            <div>
              <dt>Email</dt>
              <dd>{profile.email}</dd>
            </div>
            <div>
              <dt>Role</dt>
              <dd>{profile.role}</dd>
            </div>
            <div>
              <dt>Active</dt>
              <dd>{profile.active ? 'Yes' : 'No'}</dd>
            </div>
            <div>
              <dt>Email Verified</dt>
              <dd>{profile.emailVerified ? 'Yes' : 'No'}</dd>
            </div>
          </dl>
        ) : (
          <div className="empty-state">No profile data found.</div>
        )}
      </article>
    </section>
  );
}

export default ProfilePage;

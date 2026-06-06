import { FormEvent, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { login } from '../../api/authApi';
import { getApiErrorMessage } from '../../utils/errorUtils';
import { getDashboardPathForRole } from '../../utils/roleUtils';
import { saveAuthData } from '../../utils/tokenStorage';

interface LoginForm {
  email: string;
  password: string;
}

function LoginPage() {
  const navigate = useNavigate();
  const [form, setForm] = useState<LoginForm>({ email: '', password: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const updateField = (field: keyof LoginForm, value: string) => {
    setForm((current) => ({ ...current, [field]: value }));
  };

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    if (!form.email.includes('@') || !form.password) {
      setError('Enter your email and password.');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const response = await login({
        email: form.email.trim(),
        password: form.password,
      });

      if (!response.token) {
        setError('Login succeeded, but the backend response did not include a JWT token.');
        return;
      }

      saveAuthData(response);
      navigate(getDashboardPathForRole(response.role), { replace: true });
    } catch (apiError) {
      setError(getApiErrorMessage(apiError, 'Login failed. Check your credentials.'));
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="auth-page">
      <section className="auth-panel">
        <div className="auth-header">
          <span className="brand-mark">PF</span>
          <h1>Welcome back</h1>
          <p>Sign in to manage your ProcureFlow requests.</p>
        </div>

        <form className="form-stack" onSubmit={handleSubmit}>
          {error && <div className="alert error">{error}</div>}

          <label>
            Email
            <input
              type="email"
              value={form.email}
              onChange={(event) => updateField('email', event.target.value)}
              placeholder="rohan@example.com"
            />
          </label>

          <label>
            Password
            <input
              type="password"
              value={form.password}
              onChange={(event) => updateField('password', event.target.value)}
              placeholder="Your password"
            />
          </label>

          <button className="primary-button" type="submit" disabled={loading}>
            {loading ? 'Signing in...' : 'Login'}
          </button>
        </form>

        <p className="auth-switch">
          New to ProcureFlow? <Link to="/register">Create an account</Link>
        </p>
      </section>
    </main>
  );
}

export default LoginPage;

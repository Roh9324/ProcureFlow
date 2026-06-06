import { FormEvent, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { verifyOtp } from '../../api/authApi';
import { getApiErrorMessage } from '../../utils/errorUtils';
import { clearOtpEmail, getOtpEmail } from '../../utils/tokenStorage';

function VerifyOtpPage() {
  const navigate = useNavigate();
  const [email, setEmail] = useState(getOtpEmail());
  const [otpCode, setOtpCode] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    if (!email.includes('@')) {
      setError('Enter the email address used during registration.');
      return;
    }

    if (!/^\d{4,8}$/.test(otpCode.trim())) {
      setError('Enter a valid OTP code.');
      return;
    }

    setLoading(true);
    setError('');
    setSuccess('');

    try {
      await verifyOtp({ email: email.trim(), otpCode: otpCode.trim() });
      clearOtpEmail();
      setSuccess('Email verified successfully. Redirecting to login...');
      setTimeout(() => navigate('/login'), 700);
    } catch (apiError) {
      setError(getApiErrorMessage(apiError, 'OTP verification failed. Please try again.'));
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="auth-page">
      <section className="auth-panel">
        <div className="auth-header">
          <span className="brand-mark">PF</span>
          <h1>Verify OTP</h1>
          <p>Enter the verification code sent to your email.</p>
        </div>

        <form className="form-stack" onSubmit={handleSubmit}>
          {error && <div className="alert error">{error}</div>}
          {success && <div className="alert success">{success}</div>}

          <label>
            Email
            <input
              type="email"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              placeholder="rohan@example.com"
            />
          </label>

          <label>
            OTP Code
            <input
              inputMode="numeric"
              value={otpCode}
              onChange={(event) => setOtpCode(event.target.value)}
              placeholder="123456"
            />
          </label>

          <button className="primary-button" type="submit" disabled={loading}>
            {loading ? 'Verifying...' : 'Verify OTP'}
          </button>
        </form>

        <p className="auth-switch">
          Ready to sign in? <Link to="/login">Go to login</Link>
        </p>
      </section>
    </main>
  );
}

export default VerifyOtpPage;

import type { LoginResponse } from '../types/auth';
import type { UserRole } from '../types/auth';

const TOKEN_KEY = 'procureflow_token';
const USER_KEY = 'procureflow_user';
const OTP_EMAIL_KEY = 'procureflow_otp_email';

export interface StoredUser {
  name: string;
  email: string;
  role: UserRole;
}

const normalizeToken = (token: string) => token.replace(/^Bearer\s+/i, '').trim();

export const saveAuthData = (loginResponse: LoginResponse) => {
  const { token, name, email, role } = loginResponse;

  localStorage.setItem(TOKEN_KEY, normalizeToken(token));
  localStorage.setItem(USER_KEY, JSON.stringify({ name, email, role }));
};

export const getToken = () => {
  const token = localStorage.getItem(TOKEN_KEY);
  return token ? normalizeToken(token) : null;
};

export const getStoredUser = (): StoredUser | null => {
  const rawUser = localStorage.getItem(USER_KEY);

  if (!rawUser) {
    return null;
  }

  try {
    return JSON.parse(rawUser) as StoredUser;
  } catch {
    localStorage.removeItem(USER_KEY);
    return null;
  }
};

export const clearAuthData = () => {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(USER_KEY);
};

export const saveOtpEmail = (email: string) => {
  localStorage.setItem(OTP_EMAIL_KEY, email);
};

export const getOtpEmail = () => localStorage.getItem(OTP_EMAIL_KEY) ?? '';

export const clearOtpEmail = () => {
  localStorage.removeItem(OTP_EMAIL_KEY);
};

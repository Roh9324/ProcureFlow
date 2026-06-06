export type UserRole = 'EMPLOYEE' | 'HR_MANAGER' | 'FINAL_APPROVER' | 'ADMIN';

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
}

export interface VerifyOtpRequest {
  email: string;
  otpCode: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  email: string;
  name: string;
  role: UserRole;
}

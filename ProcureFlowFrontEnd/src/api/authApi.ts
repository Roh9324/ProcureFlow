import axiosClient from './axiosClient';
import type {
  LoginRequest,
  LoginResponse,
  RegisterRequest,
  VerifyOtpRequest,
} from '../types/auth';

export const register = async (data: RegisterRequest) => {
  const response = await axiosClient.post('/api/auth/register', data);
  return response.data;
};

export const verifyOtp = async (data: VerifyOtpRequest) => {
  const response = await axiosClient.post('/api/auth/verify-otp', data);
  return response.data;
};

export const login = async (data: LoginRequest): Promise<LoginResponse> => {
  const response = await axiosClient.post<LoginResponse>('/api/auth/login', data);
  return response.data;
};

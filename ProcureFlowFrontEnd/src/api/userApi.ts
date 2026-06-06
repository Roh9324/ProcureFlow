import axiosClient from './axiosClient';
import type { UserProfile } from '../types/user';

export const getCurrentUser = async (): Promise<UserProfile> => {
  const response = await axiosClient.get<UserProfile>('/api/users/me');
  return response.data;
};

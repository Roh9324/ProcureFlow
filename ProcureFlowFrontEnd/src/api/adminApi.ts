import axiosClient from './axiosClient';
import type {
  AdminUser,
  UpdateUserRolePayload,
  UpdateUserStatusPayload,
} from '../types/admin';

export const getAdminUsers = async (): Promise<AdminUser[]> => {
  const response = await axiosClient.get<AdminUser[]>('/api/admin/users');
  return response.data;
};

export const updateAdminUserRole = async (
  userId: number,
  data: UpdateUserRolePayload,
): Promise<AdminUser> => {
  const response = await axiosClient.put<AdminUser>(`/api/admin/users/${userId}/role`, data);
  return response.data;
};

export const updateAdminUserStatus = async (
  userId: number,
  data: UpdateUserStatusPayload,
): Promise<AdminUser> => {
  const response = await axiosClient.put<AdminUser>(`/api/admin/users/${userId}/status`, data);
  return response.data;
};

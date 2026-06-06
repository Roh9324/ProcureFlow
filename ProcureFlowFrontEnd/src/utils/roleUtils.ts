import type { UserRole } from '../types/auth';

export const getDashboardPathForRole = (role?: string | null) => {
  switch (role) {
    case 'HR_MANAGER':
      return '/hr/dashboard';
    case 'FINAL_APPROVER':
      return '/approver/dashboard';
    case 'ADMIN':
      return '/admin/dashboard';
    case 'EMPLOYEE':
    default:
      return '/employee/dashboard';
  }
};

export const isUserRole = (role?: string | null): role is UserRole =>
  role === 'EMPLOYEE' ||
  role === 'HR_MANAGER' ||
  role === 'FINAL_APPROVER' ||
  role === 'ADMIN';

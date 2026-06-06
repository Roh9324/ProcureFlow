import type { UserRole } from './auth';

export interface AdminUser {
  id: number;
  name: string;
  email: string;
  role: UserRole;
  active: boolean;
  emailVerified: boolean;
}

export interface UpdateUserRolePayload {
  role: UserRole;
}

export interface UpdateUserStatusPayload {
  active: boolean;
}

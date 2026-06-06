import type { UserRole } from './auth';

export interface UserProfile {
  id?: number;
  name: string;
  email: string;
  role: UserRole;
  active: boolean;
  emailVerified: boolean;
}

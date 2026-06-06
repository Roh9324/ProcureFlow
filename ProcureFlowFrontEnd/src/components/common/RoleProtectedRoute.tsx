import { Navigate, Outlet, useLocation } from 'react-router-dom';
import type { UserRole } from '../../types/auth';
import { getStoredUser, getToken } from '../../utils/tokenStorage';
import { getDashboardPathForRole } from '../../utils/roleUtils';

interface RoleProtectedRouteProps {
  allowedRoles: UserRole[];
}

function RoleProtectedRoute({ allowedRoles }: RoleProtectedRouteProps) {
  const location = useLocation();
  const token = getToken();
  const user = getStoredUser();

  if (!token) {
    return <Navigate to="/login" replace state={{ from: location }} />;
  }

  if (!user || !allowedRoles.includes(user.role)) {
    return <Navigate to={getDashboardPathForRole(user?.role)} replace />;
  }

  return <Outlet />;
}

export default RoleProtectedRoute;

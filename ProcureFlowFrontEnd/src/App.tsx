import { Navigate, Route, Routes } from 'react-router-dom';
import RoleProtectedRoute from './components/common/RoleProtectedRoute';
import RoleBasedLayout from './components/layout/RoleBasedLayout';
import AdminDashboardPage from './pages/admin/AdminDashboardPage';
import AdminProfilePage from './pages/admin/AdminProfilePage';
import AdminUsersPage from './pages/admin/AdminUsersPage';
import ApproverDashboardPage from './pages/approver/ApproverDashboardPage';
import ApproverProfilePage from './pages/approver/ApproverProfilePage';
import PendingApprovalsPage from './pages/approver/PendingApprovalsPage';
import LoginPage from './pages/auth/LoginPage';
import RegisterPage from './pages/auth/RegisterPage';
import VerifyOtpPage from './pages/auth/VerifyOtpPage';
import UnauthorizedPage from './pages/common/UnauthorizedPage';
import AssetRequestHistoryPage from './pages/employee/AssetRequestHistoryPage';
import CreateAssetRequestPage from './pages/employee/CreateAssetRequestPage';
import EmployeeDashboardPage from './pages/employee/EmployeeDashboardPage';
import EmployeeProfilePage from './pages/employee/EmployeeProfilePage';
import MyAssetRequestsPage from './pages/employee/MyAssetRequestsPage';
import HrDashboardPage from './pages/hr/HrDashboardPage';
import HrRequestHistoryPage from './pages/hr/HrRequestHistoryPage';
import HrRequestsPage from './pages/hr/HrRequestsPage';
import { getDashboardPathForRole } from './utils/roleUtils';
import { getStoredUser } from './utils/tokenStorage';

function DashboardRedirect() {
  const user = getStoredUser();
  return <Navigate to={getDashboardPathForRole(user?.role)} replace />;
}

function App() {
  return (
    <Routes>
      <Route path="/" element={<DashboardRedirect />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route path="/verify-otp" element={<VerifyOtpPage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/unauthorized" element={<UnauthorizedPage />} />

      <Route element={<RoleProtectedRoute allowedRoles={['EMPLOYEE']} />}>
        <Route element={<RoleBasedLayout role="EMPLOYEE" />}>
          <Route path="/employee/dashboard" element={<EmployeeDashboardPage />} />
          <Route path="/employee/asset-requests/new" element={<CreateAssetRequestPage />} />
          <Route path="/employee/asset-requests/my" element={<MyAssetRequestsPage />} />
          <Route
            path="/employee/asset-requests/:id/history"
            element={<AssetRequestHistoryPage />}
          />
          <Route path="/employee/profile" element={<EmployeeProfilePage />} />
          <Route path="/dashboard" element={<Navigate to="/employee/dashboard" replace />} />
          <Route
            path="/asset-requests/new"
            element={<Navigate to="/employee/asset-requests/new" replace />}
          />
          <Route
            path="/asset-requests/my"
            element={<Navigate to="/employee/asset-requests/my" replace />}
          />
          <Route path="/profile" element={<Navigate to="/employee/profile" replace />} />
        </Route>
      </Route>

      <Route element={<RoleProtectedRoute allowedRoles={['HR_MANAGER']} />}>
        <Route element={<RoleBasedLayout role="HR_MANAGER" />}>
          <Route path="/hr/dashboard" element={<HrDashboardPage />} />
          <Route path="/hr/requests" element={<HrRequestsPage />} />
          <Route path="/hr/requests/:id/history" element={<HrRequestHistoryPage />} />
          <Route
            path="/hr/final-approved"
            element={<HrRequestsPage mode="finalApproved" />}
          />
          <Route
            path="/hr/final-rejected"
            element={<HrRequestsPage mode="finalRejected" />}
          />
          <Route path="/hr/profile" element={<EmployeeProfilePage />} />
        </Route>
      </Route>

      <Route element={<RoleProtectedRoute allowedRoles={['ADMIN']} />}>
        <Route element={<RoleBasedLayout role="ADMIN" />}>
          <Route path="/admin/dashboard" element={<AdminDashboardPage />} />
          <Route path="/admin/users" element={<AdminUsersPage />} />
          <Route path="/admin/profile" element={<AdminProfilePage />} />
        </Route>
      </Route>

      <Route element={<RoleProtectedRoute allowedRoles={['FINAL_APPROVER']} />}>
        <Route element={<RoleBasedLayout role="FINAL_APPROVER" />}>
          <Route path="/approver/dashboard" element={<ApproverDashboardPage />} />
          <Route path="/approver/pending" element={<PendingApprovalsPage />} />
          <Route path="/approver/profile" element={<ApproverProfilePage />} />
        </Route>
      </Route>

      <Route path="*" element={<DashboardRedirect />} />
    </Routes>
  );
}

export default App;

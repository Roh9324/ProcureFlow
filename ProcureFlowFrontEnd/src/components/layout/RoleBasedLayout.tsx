import { Outlet } from 'react-router-dom';
import type { UserRole } from '../../types/auth';
import AdminSidebar from './AdminSidebar';
import ApproverSidebar from './ApproverSidebar';
import EmployeeSidebar from './EmployeeSidebar';
import HrSidebar from './HrSidebar';
import Navbar from './Navbar';

interface RoleBasedLayoutProps {
  role: UserRole;
}

function RoleBasedLayout({ role }: RoleBasedLayoutProps) {
  const sidebarByRole = {
    EMPLOYEE: <EmployeeSidebar />,
    HR_MANAGER: <HrSidebar />,
    FINAL_APPROVER: <ApproverSidebar />,
    ADMIN: <AdminSidebar />,
  };

  return (
    <div className={`app-shell role-${role.toLowerCase()}`}>
      {sidebarByRole[role]}
      <div className="app-main">
        <Navbar />
        <main className="content-area">
          <Outlet />
        </main>
      </div>
    </div>
  );
}

export default RoleBasedLayout;

import Sidebar from './Sidebar';

function EmployeeSidebar() {
  return (
    <Sidebar
      subtitle="Employee Portal"
      navItems={[
        { label: 'Dashboard', to: '/employee/dashboard' },
        { label: 'Create Request', to: '/employee/asset-requests/new' },
        { label: 'My Requests', to: '/employee/asset-requests/my' },
        { label: 'Profile', to: '/employee/profile' },
      ]}
    />
  );
}

export default EmployeeSidebar;

import Sidebar from './Sidebar';

function AdminSidebar() {
  return (
    <Sidebar
      subtitle="Administration"
      navItems={[
        { label: 'Admin Dashboard', to: '/admin/dashboard' },
        { label: 'Users', to: '/admin/users' },
        { label: 'Profile', to: '/admin/profile' },
      ]}
    />
  );
}

export default AdminSidebar;

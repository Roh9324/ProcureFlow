import Sidebar from './Sidebar';

function HrSidebar() {
  return (
    <Sidebar
      subtitle="HR Operations"
      navItems={[
        { label: 'HR Dashboard', to: '/hr/dashboard' },
        { label: 'All Requests', to: '/hr/requests' },
        { label: 'Final Approved', to: '/hr/final-approved' },
        { label: 'Final Rejected', to: '/hr/final-rejected' },
        { label: 'Profile', to: '/hr/profile' },
      ]}
    />
  );
}

export default HrSidebar;

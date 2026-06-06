import Sidebar from './Sidebar';

function ApproverSidebar() {
  return (
    <Sidebar
      subtitle="Final Approval"
      navItems={[
        { label: 'Approver Dashboard', to: '/approver/dashboard' },
        { label: 'Pending Approvals', to: '/approver/pending' },
        { label: 'Profile', to: '/approver/profile' },
      ]}
    />
  );
}

export default ApproverSidebar;

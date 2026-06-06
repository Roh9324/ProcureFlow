import { NavLink, useNavigate } from 'react-router-dom';
import { clearAuthData } from '../../utils/tokenStorage';

export interface SidebarItem {
  label: string;
  to: string;
}

interface SidebarProps {
  navItems?: SidebarItem[];
  subtitle?: string;
}

const defaultNavItems = [
  { label: 'Dashboard', to: '/employee/dashboard' },
  { label: 'Create Request', to: '/employee/asset-requests/new' },
  { label: 'My Requests', to: '/employee/asset-requests/my' },
  { label: 'Profile', to: '/employee/profile' },
];

function Sidebar({ navItems = defaultNavItems, subtitle = 'Asset Requests' }: SidebarProps) {
  const navigate = useNavigate();

  const handleLogout = () => {
    clearAuthData();
    navigate('/login', { replace: true });
  };

  return (
    <aside className="sidebar">
      <div className="sidebar-brand">
        <span className="brand-mark">PF</span>
        <div>
          <h1>ProcureFlow</h1>
          <p>{subtitle}</p>
        </div>
      </div>

      <nav className="sidebar-nav" aria-label="Main navigation">
        {navItems.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}
          >
            {item.label}
          </NavLink>
        ))}
      </nav>

      <button className="logout-button" type="button" onClick={handleLogout}>
        Logout
      </button>
    </aside>
  );
}

export default Sidebar;

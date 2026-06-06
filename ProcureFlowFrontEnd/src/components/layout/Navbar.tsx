import { getStoredUser } from '../../utils/tokenStorage';

function Navbar() {
  const user = getStoredUser();

  return (
    <header className="top-navbar">
      <div>
        <p className="eyebrow">Employee Portal</p>
        <h2>Asset Request Management</h2>
      </div>
      <div className="navbar-user">
        <span>{user?.name ?? 'User'}</span>
        <small>{user?.role ?? 'EMPLOYEE'}</small>
      </div>
    </header>
  );
}

export default Navbar;

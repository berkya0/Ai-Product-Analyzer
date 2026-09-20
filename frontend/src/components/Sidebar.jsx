import logo from "../assets/logo.png";
import { SlHome } from "react-icons/sl";
import { RxDashboard } from "react-icons/rx";
import { IoGitCompare } from "react-icons/io5";
import { FiSettings, FiLogOut } from "react-icons/fi"; 
import { NavLink, useNavigate } from "react-router-dom";
import { logout } from "../services/authenticationService";

function Sidebar() {
  const navigate = useNavigate();

  const handleLogout = async () => {
    // 1. Backend ve localStorage üzerindeki token'ları temizler
    await logout();
    
    // 2. Kullanıcıyı login ekranına yönlendirir
    navigate("/login", { replace: true });
  };

  // NavLink'in kendi { isActive } parametresini kullanıyoruz
  const getLinkClasses = ({ isActive }) => {
    const baseClasses = "flex items-center gap-3 rounded-lg px-4 py-3 transition-colors";
    
    if (isActive) {
      return `${baseClasses} bg-slate-800 text-white`; 
    }
    
    return `${baseClasses} text-slate-300 hover:bg-slate-800 hover:text-white`;
  };

  return (
    <aside className="font-[inter] w-62 min-h-screen bg-[#0F172A] text-white p-6 rounded-r-3xl flex flex-col justify-between">
      
      {/* Üst Alan: Logo ve Menü */}
      <div>
        <img
          src={logo}
          alt="Provega"
          className="w-54 h-15 mb-6"
        />

        <nav className="mt-3 space-y-3">
          <NavLink to="/" className={getLinkClasses}>
            <SlHome className="h-5 w-5" />
            <span>Ana Sayfa</span>
          </NavLink>

          <NavLink to="/dashboard" className={getLinkClasses}>
            <RxDashboard className="h-5 w-5" />
            <span>Dashboard</span>
          </NavLink>

          <NavLink to="/compare" className={getLinkClasses}>
            <IoGitCompare className="h-5 w-5" />
            <span>Karşılaştır</span>
          </NavLink>

          <NavLink to="/settings" className={getLinkClasses}>
            <FiSettings className="h-5 w-5" />
            <span>Ayarlar</span>
          </NavLink>
        </nav>
      </div>

      {/* Alt Alan: Çıkış Yap Butonu */}
      <button
        onClick={handleLogout}
        className="flex items-center gap-3 rounded-lg px-4 py-3 bg-red-500/10 text-red-400 hover:bg-red-500/20 hover:text-red-300 transition-colors w-full font-medium cursor-pointer border border-red-500/20 mt-6"
      >
        <FiLogOut className="h-5 w-5" />
        <span>Çıkış Yap</span>
      </button>

    </aside>
  );
}

export default Sidebar;
import logo from "../assets/logo.png"
import { SlHome } from "react-icons/sl";
import { RxDashboard } from "react-icons/rx";
import { IoGitCompare } from "react-icons/io5";
import { FiSettings } from "react-icons/fi"; 

function Sidebar() {
  const currentPath = window.location.pathname;

  const getLinkClasses = (path) => {
    const baseClasses = "flex items-center gap-3 rounded-lg px-4 py-3";
    
    if (currentPath === path) {
      return `${baseClasses} bg-slate-800 text-white`; 
    }
    
    return `${baseClasses} text-slate-300 hover:bg-slate-800 hover:text-white transition-colors`;
  };

  return (
    <aside className="font-[inter] w-62 min-h-screen bg-[#0F172A] text-white p-6 rounded-r-3xl">
      
      <img
        src={logo}
        alt="Provega"
        className="w-54 h-15 mb-6"
      />

      <nav className="mt-3 space-y-3">
        <a href="/" className={getLinkClasses("/")}>
          <SlHome className="h-5 w-5" />
          <span>Ana Sayfa</span>
        </a>

        <a href="/dashboard" className={getLinkClasses("/dashboard")}>
          <RxDashboard className="h-5 w-5" />
          <span>Dashboard</span>
        </a>

        <a href="/compare" className={getLinkClasses("/compare")}>
          <IoGitCompare className="h-5 w-5" />
          <span>Karşılaştır</span>
        </a>

        {/* Ayarlar butonu buraya taşındı */}
        <a href="/settings" className={getLinkClasses("/settings")}>
          <FiSettings className="h-5 w-5" />
          <span>Ayarlar</span>
        </a>
      </nav>

    </aside>
  );
}

export default Sidebar;
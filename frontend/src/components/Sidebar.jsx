import logo from "../assets/logo.png"
import { SlHome } from "react-icons/sl";
import { RxDashboard } from "react-icons/rx";
import { IoGitCompare } from "react-icons/io5";

function Sidebar() {
  // Mevcut sayfanın yolunu (URL'in son kısmını) alıyoruz
  const currentPath = window.location.pathname;

  // Linkin aktif olup olmamasına göre class döndüren yardımcı fonksiyon
  const getLinkClasses = (path) => {
    const baseClasses = "flex items-center gap-3 rounded-lg px-4 py-3";
    
    // Eğer bulunduğumuz sayfa linkin sayfasına eşitse, koyu arka plan (aktif) ver
    if (currentPath === path) {
      return `${baseClasses} bg-slate-800 text-white`; 
    }
    
    // Eşit değilse üzerine gelince (hover) koyu olacak şekilde ayarla
    return `${baseClasses} text-slate-300 hover:bg-slate-800 hover:text-white transition-colors`;
  };

  return (
    <aside className="font-[inter] w-62 min-h-screen bg-[#0F172A] text-white p-6 rounded-r-3xl">
      
      <img
        src={logo}
        alt="Provega"
        className="w-54 h-15"
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
      </nav>

    </aside>
  );
}

export default Sidebar;
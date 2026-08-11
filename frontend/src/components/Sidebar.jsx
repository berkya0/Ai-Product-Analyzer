import logo from "../assets/logo.png"
import { SlHome } from "react-icons/sl";
import { RxDashboard } from "react-icons/rx";
import { IoGitCompare } from "react-icons/io5";
function Sidebar() {
  return (
    <aside className="font-[inter] w-62 min-h-screen bg-[#0F172A] text-white p-6 rounded-r-3xl">
      
      <img
        src={logo}
        alt="Provega"
        className="w-54 h-15"
      />

      <nav className="mt-3 space-y-3">
        <a
          href="/"
          className="flex items-center gap-3 rounded-lg px-4 py-3 bg-slate-800"
        >
          <SlHome className="h-5 w-5" />
         <span>Ana Sayfa</span>
        </a>

        <a
          href="/dashboard"
           className="flex items-center gap-3 rounded-lg px-4 py-3  hover:bg-slate-800"
        >
          <RxDashboard className="h-5 w-5" />
         <span>Dashboard</span>
        </a>

        <a
          href="/compare"
          className="flex items-center gap-3 rounded-lg px-4 py-3  hover:bg-slate-800"
        >
            <IoGitCompare className ="h-5 w-5" />
            <span className="">Karşılaştır</span>
        
        </a>

      </nav>

    </aside>
  )
}

export default Sidebar
import React from "react";
import { FiSearch } from "react-icons/fi";

function Searchbar({ placeholder = "Arama yapın...", className = "", value,onChange,onSearch }) {
  return (
 
    <div className={`flex gap-3 ${className}`}>
     
      <div className="relative flex-1">
        <FiSearch className="absolute left-4 top-1/2 -translate-y-1/2 text-[#747373] w-5 h-5" />
        <input
          type="text"
          placeholder={placeholder} 
          value={value}
          onChange={onChange}
          className="w-full bg-white border border-slate-200 rounded-xl py-3 pl-11 pr-4 text-sm text-[#747373] placeholder-[#747373] focus:outline-none focus:ring-2 focus:ring-slate-300 shadow-sm transition"
        />
      </div>

      <button 
        onClick={onSearch}
        className="bg-[#0F172A] text-white px-6 py-3 rounded-xl font-medium text-sm hover:bg-slate-800 transition cursor-pointer shrink-0"
      >
        Analiz Et
      </button>
    </div>
  );
}

export default Searchbar;
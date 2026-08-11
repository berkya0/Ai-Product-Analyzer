import React from "react";
import { FaStar } from "react-icons/fa";
import { FiCpu } from "react-icons/fi"; // AI sembolü olarak işlemci ikonu

function AiPreferenceCard() {
  return (
    <div className="bg-[#FFFFFC] rounded-2xl border border-[#E6C84A] p-5 shadow-sm flex flex-col sm:flex-row items-start sm:items-center font-['Montserrat']">
      
      {/* 1. SOL SÜTUN: İkon ve Özellik Listeleri */}
      <div className="flex-1 space-y-4">
        
        {/* AI Logosu / Mavi İkon Kutusu */}
        <div className="w-12 h-10 bg-blue-50 border border-blue-200 rounded-xl flex items-center justify-center text-blue-600">
          <FiCpu className="w-12 h-12" />
        </div>

        {/* Neden Alınır? (Olumlu Liste) */}
        <div>
          <h4 className="font-bold text-slate-900 text-sm mb-2">
            Neden Alınır?
          </h4>
          <ul className="space-y-1.5 text-xs text-slate-700 font-medium">
            <li className="flex items-center gap-2">
              <span className="w-2.5 h-2.5 rounded-full bg-emerald-500 shrink-0"></span>
              AMOLED ekran
            </li>
            <li className="flex items-center gap-2">
              <span className="w-2.5 h-2.5 rounded-full bg-emerald-500 shrink-0"></span>
              Güçlü performans
            </li>
            <li className="flex items-center gap-2">
              <span className="w-2.5 h-2.5 rounded-full bg-emerald-500 shrink-0"></span>
              Uzun pil ömrü
            </li>
          </ul>
        </div>

        {/* Dikkat Edilmesi Gerek! (Olumsuz Liste) */}
        <div>
          <h4 className="font-bold text-slate-900 text-sm mb-2">
            Dikkat Edilmesi Gerek !
          </h4>
          <ul className="space-y-1.5 text-xs text-slate-700 font-medium">
            <li className="flex items-center gap-2">
              <span className="w-2.5 h-2.5 rounded-full bg-red-500 shrink-0"></span>
              Şarj hızı
            </li>
            <li className="flex items-center gap-2">
              <span className="w-2.5 h-2.5 rounded-full bg-red-500 shrink-0"></span>
              Gece kamerası
            </li>
          </ul>
        </div>

      </div>

      {/* 2. SAĞ SÜTUN: Skor Göstergesi (Donut Grafik) */}
      <div className="relative w-36 h-36 flex items-center justify-center shrink-0 self-center">
        
        {/* Dış Renkli Halka (Conic Gradient Numarası) */}
        <div 
          className="w-full h-full rounded-full p-3.5 flex items-center justify-center"
          style={{
            background: `conic-gradient(from 210deg, #ef4444 0% 18%, #facc15 18% 45%, #22c55e 45% 85%, #e2e8f0 85% 100%)`
          }}
        >
          {/* İçteki Beyaz Daire (Grafiğin Ortasını Delip Halka Yapar) */}
          <div className="w-full h-full bg-white rounded-full flex items-center justify-center gap-1 shadow-sm">
            <FaStar className="text-amber-400 text-lg" />
            <span className="font-bold text-xl text-slate-900">4.5</span>
          </div>
        </div>

      </div>

    </div>
  );
}

export default AiPreferenceCard;
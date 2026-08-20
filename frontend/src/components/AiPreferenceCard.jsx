import React from "react";
import { FaStar } from "react-icons/fa";
import { FiCpu } from "react-icons/fi"; // AI sembolü olarak işlemci ikonu

function AiPreferenceCard({analysis}) {
   if (!analysis) {
    return null;
  }
  const score=analysis.aiScore;
  const scorePercentage = (analysis.aiScore / 5) * 100;
  const pros=analysis.highlights.filter(
    (highlight) => highlight.commentType==="PRO");
  const cons=analysis.highlights.filter(
    (highlight) => highlight.commentType==="CON");

    let scoreColor;
    if (score < 2) {
      scoreColor = "#ef4444";
    } else if (scorePercentage < 3.5) {
    scoreColor = "#facc15";
    } else {
    scoreColor = "#22c55e";
  } 
  return (
    <div className="bg-[#FFFFFC] rounded-2xl border border-[#E6C84A] p-6 shadow-sm grid grid-cols-1 md:grid-cols-[1fr_160px] gap-6 items-center font-['Montserrat']">
      
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
           {pros.map((highlight,index)=>(
            <li key={index} className="flex items-center gap-2">
               <span className="w-2.5 h-2.5 rounded-full bg-emerald-500 shrink-0"></span>
               {highlight.aiComments}
            </li>
           ))}
          </ul>
        </div>

        {/* Dikkat Edilmesi Gerek! (Olumsuz Liste) */}
        <div>
          <h4 className="font-bold text-slate-900 text-sm mb-2">
            Dikkat Edilmesi Gerek !
          </h4>
          <ul className="space-y-1.5 text-xs text-slate-700 font-medium">
            {cons.map((highlight, index) => (
           <li key={index} className="flex items-center gap-2">
            <span className="w-2.5 h-2.5 rounded-full bg-red-500 shrink-0"></span>
            {highlight.aiComments}
          </li>
             ))}
          </ul>
        </div>

      </div>

      {/* 2. SAĞ SÜTUN: Skor Göstergesi (Donut Grafik) */}
      <div className="relative w-36 h-36 flex items-center justify-center shrink-0 self-center">
        
        {/* Dış Renkli Halka (Conic Gradient Numarası) */}
        <div 
          className="w-full h-full rounded-full p-3.5 flex items-center justify-center"
          style={{
          background: `conic-gradient(
          from 210deg,
          ${scoreColor} 0% ${scorePercentage}%,
           #e2e8f0 ${scorePercentage}% 100%
            )`
         }}
        >
          {/* İçteki Beyaz Daire (Grafiğin Ortasını Delip Halka Yapar) */}
          <div className="w-full h-full bg-white rounded-full flex items-center justify-center gap-1 shadow-sm">
            <FaStar className="text-amber-400 text-lg" />
            <span className="font-bold text-xl text-slate-900">{analysis.aiScore}</span>
          </div>
        </div>

      </div>

    </div>
  );
}

export default AiPreferenceCard;
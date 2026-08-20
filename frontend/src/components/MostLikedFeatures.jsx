import React from "react";

function MostLikedFeatures({ analysis, type }) {
  if (!analysis) {
    return null;
  }

  const getBarColor = (feature) => {
    if (feature.loveType === "LOVED") {
      if (feature.percentage >= 90) return "bg-[#0BC505]";
      if (feature.percentage >= 75) return "bg-[#FFEB00]";
      if (feature.percentage >= 50) return "bg-[#FFAE4C]";
      return "bg-[#FF0303]";
    }

    if (feature.percentage >= 90) return "bg-[#FF0303]";
    if (feature.percentage >= 75) return "bg-[#FFAE4C]";
    if (feature.percentage >= 50) return "bg-[#FFEB00]";
    return "bg-[#0BC505]";
  };

  const isLoved = type === "LOVED";
  const config = {
    title: isLoved
      ? "En çok sevilen özellikler"
      : "En çok şikayet edilen özellikler",

    commentTitle: isLoved
      ? "En çok beğeni alan olumlu yorum"
      : "En çok beğeni alan olumsuz yorum",

    comment: isLoved
      ? analysis.topPositiveComment
      : analysis.topNegativeComment,

    icon: isLoved ? "👍" : "👎",

    cardClass: isLoved
      ? "bg-[#F0FDF4] border-[#22C55E]"
      : "bg-[#FFF1F2] border-[#EF4444]",
      
    trackBg: isLoved ? "bg-green-100" : "bg-red-100",
  };

  const features = analysis.featureResults?.filter(
    (feature) => feature.loveType === type
  ) || [];

  return (
    <div className={`${config.cardClass} rounded-2xl border p-5 shadow-sm font-['Montserrat'] flex flex-col justify-between`}>
      <div>
        {/* Başlık */}
        <h3 className="font-bold text-slate-900 text-base mb-4">
          {config.title}
        </h3>

        {/* Özellikler - CSS Grid Yapısı */}
        <div className="space-y-3">
          {features.map((feature, index) => (
            <div 
              key={index} 
              className="grid grid-cols-[140px_1fr_50px] items-center gap-3 text-sm"
            >
              {/* 1. Sütun: Özellik Adı */}
              <span className="font-semibold text-slate-800 truncate" title={feature.featureName}>
                {feature.featureName}
              </span>

              {/* 2. Sütun: Progress Bar Mantığı */}
              <div className={`w-full h-4 ${config.trackBg} overflow-hidden`}>
                <div
                  className={`h-full ${getBarColor(feature)} transition-all duration-500`}
                  style={{ width: `${feature.percentage}%` }}
                ></div>
              </div>

              {/* 3. Sütun: Yüzde Değeri */}
              <span className="font-bold text-slate-700 text-right">
                %{feature.percentage}
              </span>
            </div>
          ))}
        </div>
      </div>

      {/* Yorum Alanı */}
      <div className="mt-6 pt-4 border-t border-slate-200/60">
        <div className="flex items-center gap-2">
          <span className="text-base">{config.icon}</span>
          <span className="font-bold text-sm text-slate-800">
            {config.commentTitle}
          </span>
        </div>
        <p className="text-[#747373] text-xs mt-2 italic pl-6 leading-relaxed">
          "{config.comment}"
        </p>
      </div>
    </div>
  );
}

export default MostLikedFeatures;
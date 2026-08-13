import React from "react";

function MostLikedFeatures({analysis,type}) {
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
};
  const features = analysis.featureResults.filter(
    (feature) => feature.loveType === type
  );

  return (
    
    <div className={`${config.cardClass} rounded-2xl border p-4`}>
        <div className="space-y-3">
            {features.map((feature)=>(
         <div className="flex items-center gap-3">
    <span className="font-bold">{feature.featureName}</span>
    <div className="w-[226px] h-[20px] bg-[#F0FDF4] ">
        <div className={`h-full ${getBarColor(feature)}`}
         style={{ width: `${feature.percentage}%` }}
         ></div>
    </div>
    <span className="font-bold">{feature.percentage+"%"}</span>
    </div> 
  
    ))}
    </div>
    <div className="flex items-center gap-2 mt-4">
  <span>{config.icon}</span>

  <span className="font-bold text-sm">
    {config.commentTitle}
  </span>
</div>
     <div className="text-[#747373] text-[12px] mt-4 ">
        "{config.comment}"</div>
        
    </div>
  
  );
}

export default MostLikedFeatures;
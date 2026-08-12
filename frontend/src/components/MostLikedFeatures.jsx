import React from "react";

function MostLikedFeatures() {
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
    const features = [
  {
    featureName:"Batarya" , percentage: 90, loveType:"LOVED"//enum
  },
  {
    featureName:"Kamera" , percentage: 55,loveType:"LOVED"
  },
  {
    featureName:"Ekran" , percentage: 49,loveType:"COMPLAINED"
  },
  {
    featureName:"Fiyat" , percentage: 70,loveType:"LOVED"
  },

  ];
  return (
    
    <div className=" bg-[#F0FDF4] rounded-2xl border border-[#22C55E] p-4 ">
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

     <div className="text-[#747373] text-[12px] mt-4 ">
        "Bataryası mükemmel tüm günü çıkartıyor"</div>
        
    </div>
  
  );
}

export default MostLikedFeatures;
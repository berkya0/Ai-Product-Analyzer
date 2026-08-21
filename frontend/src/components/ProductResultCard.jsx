import React from "react";
import { FiBell, FiBellOff } from "react-icons/fi";
import { FaStar, FaStarHalfAlt } from "react-icons/fa";

function ProductResultCard({ product,aiSummary,onToggleFollow}) {
  // Dışarıdan prop gelmezse görseldeki verileri varsayılan olarak kullanır
  const data = product || {
    name: "Samsung Galaxy A54 128GB 8GB Ram",
    price: "17.999 TL",
    rating: 4.1,
    reviewCount: 120,
    summary:
      "Ürün kullanıcılar tarafından genel olarak fiyat/performans dengesi, şık tasarımı ve 120Hz Super AMOLED ekranının canlılığıyla oldukça beğenilmiş. Ana kamerasının gündüz çekim performansı ve batarya ömrü olumlu değerlendirilmiştir.",
    imageUrl:
      "https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?q=80&w=600&auto=format&fit=crop", // Örnek telefon görseli
  };

  return (
    <div className="bg-white rounded-2xl border border-[#D0E1F9] p-4 shadow-sm flex flex-col sm:flex-row gap-5 font-['Montserrat']">
      
      {/* 1. SOL TARAF: Ürün Görseli */}
      <div className="w-full sm:w-48 h-56 sm:h-auto shrink-0 rounded-xl overflow-hidden bg-slate-100">
        <img
          src={data.imageUrl}
          alt={data.name}
          className="w-full h-full object-cover"
        />
      </div>

      {/* 2. SAĞ TARAF: Ürün Detayları */}
      <div className="flex-1 flex flex-col justify-between py-1">
        <div>
          {/* Başlık ve Altındaki Çizgi */}
          <h3 className="text-lg font-bold text-slate-900 border-b-2 border-slate-900 pb-1.5 leading-snug">
            {data.name}
          </h3>

          {/* Fiyat ve Alarm (Zil) İkonu */}
          <div className="flex items-center justify-between mt-3">
            <span className="text-xl font-extrabold text-slate-900">
              {data.price} TL
            </span>
           <button 
              title={data?.isFollowing ? "Takipten Çıkar" : "Fiyat Takibi Oluştur"}
              className={`p-2 rounded-lg transition cursor-pointer flex items-center justify-center ${
                data?.isFollowing 
                  ? "bg-blue-50 text-blue-600 hover:bg-blue-100" 
                  : "text-slate-700 hover:bg-slate-100"
              }`}
              onClick={() => onToggleFollow && onToggleFollow(data?.id, data?.isFollowing)}
            >
              {data?.isFollowing ? (
                <FiBellOff className="w-6 h-6" />
              ) : (
                <FiBell className="w-6 h-6" />
              )}
            </button>
          </div>

          {/* Yıldız Puanı ve Değerlendirme Sayısı */}
          {/* Yıldız Puanı ve Değerlendirme Sayısı */}
<div className="flex items-center gap-1.5 mt-2">
  <div className="flex text-amber-400 gap-0.5 text-sm">
    {[1, 2, 3, 4, 5].map((star) => {
      if (data.rating >= star) {
        return <FaStar key={star} />;
      }

      if (data.rating >= star - 0.5) {
        return <FaStarHalfAlt key={star} />;
      }

      return (
        <FaStar
          key={star}
          className="text-slate-300"
        />
      );
    })}
  </div>

  <span className="text-xs font-semibold text-slate-700">
    ({data.rating}/5.0)
  </span>

  <span className="text-[11px] text-slate-400 ml-1">
    {data.reviewCount} Değerlendirme
  </span>
</div>

        {/* Hızlı Özet Bölümü */}
        <div className="mt-4">
          <h4 className="font-bold text-slate-800 text-sm mb-1">
            Hızlı Özet
          </h4>
          <p className="text-xs text-slate-500 leading-relaxed">
            {aiSummary}
          </p>
        </div>

      </div>
    </div>
    </div>
  );
}

export default ProductResultCard;
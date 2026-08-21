import React from 'react';
import { 
  Calendar, 
  Check, 
  X, 
  Bell, 
  BellOff, 
  RefreshCw, 
  Trash2 
} from 'lucide-react';

function DashboardProducts({ item, onDelete, onRefresh, onToggleMute }) {
  function getScoreBadgeClass(score) {
    if (score < 60) return 'bg-red-200 text-red-600';
    if (score < 80) return 'bg-yellow-200 text-yellow-700';
    return 'bg-green-200 text-green-700';
  }

  return (
    <div className="grid grid-cols-[1fr_100px_160px_130px_120px] items-center p-4 bg-white hover:shadow-md transition-shadow gap-7">
      
      {/* 1. Sol Kısım: Görsel, Başlık ve Tarih */}
      <div className="flex items-center space-x-4 min-w-[280px]">
        <img 
          src={item?.imageUrl} 
          alt={item?.name} 
          className="w-14 h-14 object-cover rounded-xl border border-gray-100"
        />
        <div>
          <h3 className="font-semibold text-gray-900 text-base">{item?.name}</h3>
          <div className="flex items-center space-x-1 text-xs text-gray-400 mt-1">
            <Calendar className="w-3.5 h-3.5" />
            <span>{item?.updatedAt}</span>
          </div>
        </div>
      </div>

      {/* 2. Skor Rozeti */}
      <div className="flex justify-center min-w-[60px]">
        <span className={`px-3 py-1.5 rounded-xl font-bold text-sm ${getScoreBadgeClass(item?.aiScore*100/5)}`}>
          {item?.aiScore}
        </span>
      </div>

      {/* 3. Takip Durumu */}
      <div className="flex items-center space-x-2 min-w-[140px]">
        <span className={`w-3 h-3 rounded-full ${item?.isFollowing ? 'bg-blue-500' : 'bg-orange-500'}`} />
        <span className="text-sm font-medium text-gray-500">
          {item?.isFollowing ? 'Takip ediliyor' : 'Takipde Değil'}
        </span>
      </div>

      {/* 4. Başarı / Başarısızlık Durumu */}
      <div className="flex items-center space-x-1.5 min-w-[110px]">
        {item?.status === 'SUCCESS' ? (
          <>
            <Check className="w-5 h-5 text-green-500 stroke-[3]" />
            <span className="text-sm font-semibold text-green-500">Başarılı</span>
          </>
        ) : (
          <>
            <X className="w-5 h-5 text-red-500 stroke-[3]" />
            <span className="text-sm font-semibold text-red-500">Başarısız</span>
          </>
        )}
      </div>

      {/* 5. Aksiyon Butonları */}
      <div className="flex items-center space-x-4 text-slate-700">
        <button 
          onClick={() => onToggleMute(item?.id)}
          className="hover:text-black transition-colors"
          title={item?.isFollowing ? "Takipten çık" : "Takibe al"}
        >
          {item?.isFollowing ? <BellOff className="w-5 h-5" /> : <Bell className="w-5 h-5" />}
        </button>

        <button 
           onClick={() => {
        console.log("ITEM:", item);
        console.log("PRODUCT URL:", item?.productUrl);
        onRefresh(item?.id, item?.productUrl);
    }}
          className="hover:text-black transition-colors"
          title="Yenile"
        >
          <RefreshCw className="w-5 h-5" />
        </button>

        <button 
          onClick={() => onDelete(item?.id)}
          className="hover:text-red-600 transition-colors"
          title="Sil"
        >
          <Trash2 className="w-5 h-5" />
        </button>
      </div>

    </div>
  );
}
export default DashboardProducts;
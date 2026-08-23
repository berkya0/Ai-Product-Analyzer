import React from 'react';
import { Bot, X, Trophy, Star } from 'lucide-react';

export default function CompareResultCard({ results, onRemoveProduct }) {
  if (!Array.isArray(results) || results.length === 0) return null;

  const scoreOf = (item) => Math.round((item.aiScore ?? 0) * 20);
  const bestScoreId = results.reduce((best, it) =>
    scoreOf(it) > scoreOf(best) ? it : best, results[0]).id;
  const pricedItems = results.filter((it) => it.price);
  const bestPriceId = pricedItems.length > 1
    ? pricedItems.reduce((best, it) => Number(it.price) < Number(best.price) ? it : best, pricedItems[0]).id
    : null;

  const scoreColors = (score) => {
    if (score >= 80) return { ring: '#10b981', bg: 'bg-emerald-50', text: 'text-emerald-700' };
    if (score >= 60) return { ring: '#f59e0b', bg: 'bg-amber-50', text: 'text-amber-700' };
    return { ring: '#f43f5e', bg: 'bg-rose-50', text: 'text-rose-700' };
  };

  const cols = `180px repeat(${results.length}, minmax(160px, 1fr))`;

  return (
    <div className="w-full bg-white border border-slate-200 rounded-2xl shadow-sm font-['Montserrat'] overflow-hidden">

      {/* HEADER: ürün görseli + isim + kaldır */}
      <div className="grid gap-2 px-6 pt-6 pb-5 border-b border-slate-100" style={{ gridTemplateColumns: cols }}>
        <div className="flex items-end">
          <div className="w-11 h-11 rounded-xl bg-slate-900 text-white flex items-center justify-center">
            <Bot className="w-5 h-5" />
          </div>
        </div>
        {results.map((item) => (
          <div key={item.id} className="flex flex-col items-center text-center gap-2 relative">
            {onRemoveProduct && (
              <button
                onClick={() => onRemoveProduct(item.id)}
                className="absolute -top-1 -right-1 w-6 h-6 rounded-full bg-slate-50 hover:bg-rose-50 text-slate-400 hover:text-rose-500 flex items-center justify-center transition-colors"
                title="Ürünü çıkar"
              >
                <X className="w-3.5 h-3.5" />
              </button>
            )}
            <div className="relative">
              <img
                src={item.imageUrl}
                alt={item.name}
                className="w-16 h-16 object-cover rounded-xl border border-slate-200"
              />
              {item.id === bestScoreId && (
                <div className="absolute -bottom-2 left-1/2 -translate-x-1/2 bg-emerald-500 text-white rounded-full p-1 shadow-sm">
                  <Trophy className="w-3 h-3" />
                </div>
              )}
            </div>
            <span className="font-semibold text-slate-800 text-sm leading-tight line-clamp-2 px-1">
              {item.name}
            </span>
          </div>
        ))}
      </div>

      {/* AI SKOR */}
      <div className="grid gap-2 px-6 py-4 border-b border-slate-100 items-center" style={{ gridTemplateColumns: cols }}>
        <div className="text-sm font-medium text-slate-500">AI skoru</div>
        {results.map((item) => {
          const score = scoreOf(item);
          const c = scoreColors(score);
          return (
            <div key={item.id} className="flex justify-center">
              <div className={`relative w-14 h-14 rounded-full ${c.bg} flex items-center justify-center`}>
                <svg className="absolute inset-0 -rotate-90" viewBox="0 0 56 56">
                  <circle cx="28" cy="28" r="24" fill="none" stroke="#e5e7eb" strokeWidth="4" />
                  <circle
                    cx="28" cy="28" r="24" fill="none" stroke={c.ring} strokeWidth="4"
                    strokeDasharray={`${(score / 100) * 150.8} 150.8`}
                    strokeLinecap="round"
                  />
                </svg>
                <span className={`font-bold text-sm ${c.text}`}>{score}</span>
              </div>
            </div>
          );
        })}
      </div>

      {/* FİYAT */}
      <div className="grid gap-2 px-6 py-4 border-b border-slate-100 items-center" style={{ gridTemplateColumns: cols }}>
        <div className="text-sm font-medium text-slate-500">Fiyat</div>
        {results.map((item) => (
          <div key={item.id} className="flex justify-center">
            <span className={`text-sm font-bold px-2.5 py-1 rounded-lg ${
              item.id === bestPriceId ? 'bg-sky-50 text-sky-700' : 'text-slate-700'
            }`}>
              {item.price ? `${Number(item.price).toLocaleString('tr-TR')} TL` : 'Belirtilmedi'}
            </span>
          </div>
        ))}
      </div>

      {/* DEĞERLENDİRME */}
      <div className="grid gap-2 px-6 py-4 border-b border-slate-100 items-center" style={{ gridTemplateColumns: cols }}>
        <div className="text-sm font-medium text-slate-500">Değerlendirme</div>
        {results.map((item) => {
          const score = Math.min(5, Math.max(0, Math.round(item.rating || 4)));
          return (
            <div key={item.id} className="flex flex-col items-center gap-0.5">
              <div className="flex gap-0.5">
                {Array.from({ length: 5 }).map((_, i) => (
                  <Star key={i} className={`w-3.5 h-3.5 ${i < score ? 'fill-amber-400 text-amber-400' : 'text-slate-200'}`} />
                ))}
              </div>
              <span className="text-[11px] text-slate-400 font-medium">
                {item.reviewCount || 0} yorum
              </span>
            </div>
          );
        })}
      </div>

      {/* AI ÖZETİ */}
      <div className="grid gap-2 px-6 py-5 items-start bg-slate-50/60" style={{ gridTemplateColumns: cols }}>
        <div className="text-sm font-medium text-slate-500 pt-1">AI özeti</div>
        {results.map((item) => (
          <p key={item.id} className="text-xs text-slate-500 leading-relaxed">
            {item.summary || 'Bu ürün için henüz yapay zeka özeti bulunmuyor.'}
          </p>
        ))}
      </div>
    </div>
  );
}

// --- Önizleme için örnek veri ---
const sample = [
  {
    id: 1,
    name: 'JBL Kablosuz Kulaklık',
    imageUrl: 'https://placehold.co/200x200/1e293b/white?text=JBL',
    aiScore: 3.5,
    price: 11400,
    rating: 4,
    reviewCount: 100,
    summary: 'Kullanıcılar tarafından genel olarak fiyat/performans dengesi ve şık tasarımı olumlu değerlendirilmiş. Batarya ömrü iyi.',
  },
  {
    id: 2,
    name: 'El Vantilatörü',
    imageUrl: 'https://placehold.co/200x200/0ea5e9/white?text=Fan',
    aiScore: 4.25,
    price: 9800,
    rating: 5,
    reviewCount: 100,
    summary: 'Taşınabilir olması ve şarj süresi kullanıcılar tarafından öne çıkarılmış. Ses seviyesi bazı yorumlarda eleştirilmiş.',
  },
];

export function CompareResultCardPreview() {
  const [items, setItems] = React.useState(sample);
  return (
    <div className="p-8 bg-slate-100 min-h-screen flex items-start justify-center">
      <div className="w-full max-w-2xl">
        <CompareResultCard
          results={items}
          onRemoveProduct={(id) => setItems((prev) => prev.filter((i) => i.id !== id))}
        />
      </div>
    </div>
  );
}
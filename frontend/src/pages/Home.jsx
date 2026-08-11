import '@fontsource/montserrat';
import Searchbar from '../components/Searchbar';
import ProductResultCard from '../components/ProductResultCard';
import AiPreferenceCard from '../components/AiPreferenceCard'; // 1. EKSİK IMPORT EKLENDİ

function Home() {
  return (
    <main className="flex-1 min-h-screen font-['Montserrat'] bg-[#F8FAFC] p-8">
      
      {/* 1. ÜST BÖLÜM (Başlık ve Açıklama) */}
      <header className="mb-6">
        <h1 className="text-black font-semibold text-2xl">
          Ana Sayfa
        </h1>
        <p className="text-sm text-[#747373] font-semibold mt-1">
          Analiz etmek istediğin ürünün linkini gir
        </p>
      </header>

      {/* 2. ARAMA BARI */}
      <Searchbar 
        placeholder="Ürünün linkini gir" 
        className="max-w-5xl" 
      />

      {/* 3. KARTLAR ALANI (Gereksiz div, p-8 ve fazla başlık silindi) */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mt-8">
        
        {/* Sol Taraf: Ürün Sonuç */}
        <div>
          <h2 className="text-slate-700 font-bold mb-3 text-lg">
            Ürün Sonuç
          </h2>
          <ProductResultCard />
        </div>

        {/* Sağ Taraf: AI Tercihi */}
        <div>
          <h2 className="text-slate-700 font-bold mb-3 text-lg">
            AI Tercihi
          </h2>
          <AiPreferenceCard />
        </div>

      </div>

    </main>
  );
}

export default Home;
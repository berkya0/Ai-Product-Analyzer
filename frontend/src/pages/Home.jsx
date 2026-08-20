import '@fontsource/montserrat';
import Searchbar from '../components/Searchbar';
import ProductResultCard from '../components/ProductResultCard';
import AiPreferenceCard from '../components/AiPreferenceCard'; 
import MostLikedFeatures from '../components/MostLikedFeatures';
import { scrapProduct } from "../services/productService";
import { useState } from "react";

function Home() {
  const [productUrl, setProductUrl] = useState("");
  const [product, setProduct] = useState(null);
  const [analysis, setAnalysis] = useState(null);
  
  // UX için yüklenme durumunu takip edeceğimiz state
  const [isLoading, setIsLoading] = useState(false); 
  
  const handleScrap = async () => {
    if (!productUrl) return; // URL boşsa işlem yapma

    try {
      setIsLoading(true); // Yüklenme başladı
      
      const data = await scrapProduct(productUrl);
      
      // Backend'den gelen yeni DTO yapısına göre verileri parçalıyoruz
      setProduct(data.product);
      setAnalysis(data.analysis);

    } catch (error) {
      console.error("Analiz sırasında hata oluştu:", error);
      alert("Ürün analiz edilemedi. Lütfen linki kontrol edin.");
    } finally {
      setIsLoading(false); // Başarılı veya başarısız, yüklenme bitti
    }
  };

  return (
    <main className="flex-1 min-h-screen font-['Montserrat'] bg-[#F8FAFC] p-8">
      
      <header className="mb-6">
        <h1 className="text-black font-semibold text-2xl">
          Ana Sayfa
        </h1>
        <p className="text-sm text-[#747373] font-semibold mt-1">
          Analiz etmek istediğin ürünün linkini gir
        </p>
      </header>

      <Searchbar 
        placeholder="Ürünün linkini gir" 
        className="max-w-5xl" 
        value={productUrl}
        onChange={(e)=>setProductUrl(e.target.value)}
        onSearch={handleScrap}
        showButton={true}
      />
    
      {/* Yükleniyorsa mesaj göster */}
      {isLoading && (
        <div className="mt-10 text-center font-semibold text-slate-600 text-lg">
          Ürün inceleniyor ve yapay zeka yorumları analiz ediyor, lütfen bekleyin...
        </div>
      )}

      {/* Yüklenme bittiyse ve veriler geldiyse kartları göster */}
      {!isLoading && product && analysis && (
        <>
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 mt-8">
            <div>
              <h2 className="text-slate-700 font-bold mb-3 text-lg">
                Ürün Sonuç
              </h2>
              {/* aiSummary state'i yerine doğrudan analysis.summary yolluyoruz */}
              <ProductResultCard product={product} aiSummary={analysis.summary}/>
            </div>

            <div>
              <h2 className="text-slate-700 font-bold mb-3 text-lg">
                AI Tercihi
              </h2>
              <AiPreferenceCard analysis={analysis}/>
            </div>
          </div>

          <div className='grid grid-cols-1 lg:grid-cols-2 gap-10 mr-50 mt-8'>
            <div>
              <h2 className="text-slate-700 font-bold mb-3 text-lg">
                En Çok Sevilen Özellikler
              </h2>
              <MostLikedFeatures analysis={analysis} type="LOVED"/>
            </div>
            <div>
              <h2 className="text-slate-700 font-bold mb-3 text-lg">
                En Çok Şikayet Edilen Özellikler
              </h2>
              <MostLikedFeatures analysis={analysis} type="COMPLAINED"/>
            </div>
          </div>
        </>
      )}
    </main>
  );
}

export default Home;
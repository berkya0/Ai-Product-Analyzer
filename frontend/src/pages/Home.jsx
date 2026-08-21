import '@fontsource/montserrat';
import Searchbar from '../components/Searchbar';
import ProductResultCard from '../components/ProductResultCard';
import AiPreferenceCard from '../components/AiPreferenceCard'; 
import MostLikedFeatures from '../components/MostLikedFeatures';
import { scrapProduct } from "../services/productService";
import { useState } from "react";
import { useToggleFollow } from "../hooks/useToggleFollow"; 

function Home() {
  const [productUrl, setProductUrl] = useState("");
  const [product, setProduct] = useState(null);
  const [analysis, setAnalysis] = useState(null);
  const [isLoading, setIsLoading] = useState(false); 
  
  const { toggle } = useToggleFollow();

  const handleScrap = async () => {
    if (!productUrl) return; 

    try {
      setIsLoading(true); 
      
      const data = await scrapProduct(productUrl);
      
      setProduct(data.product);
      setAnalysis(data.analysis);

    } catch (error) {
      console.error("Analiz sırasında hata oluştu:", error);
      alert("Ürün analiz edilemedi. Lütfen linki kontrol edin.");
    } finally {
      setIsLoading(false); 
    }
  };

  const handleToggleFollow = (id, currentIsFollowing) => {
    toggle(id, currentIsFollowing, (newFollowingStatus) => {
        
        setProduct(prev => ({ ...prev, isFollowing: newFollowingStatus }));
    });
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
    
      {isLoading && (
        <div className="mt-10 text-center font-semibold text-slate-600 text-lg">
          Ürün inceleniyor ve yapay zeka yorumları analiz ediyor, lütfen bekleyin...
        </div>
      )}

      {!isLoading && product && analysis && (
        <>
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 mt-8">
            <div>
              {/* handleToggleMute FONKSİYONUNU KARTA PROP OLARAK GÖNDERİYORUZ */}
              <ProductResultCard 
                product={product} 
                aiSummary={analysis.summary} 
                onToggleFollow={handleToggleFollow} 
              />
            </div>

            <div>
              <AiPreferenceCard analysis={analysis}/>
            </div>
          </div>

          <div className='grid grid-cols-1 lg:grid-cols-2 gap-10 mr-50 mt-8'>
            <div>
              <MostLikedFeatures analysis={analysis} type="LOVED"/>
            </div>
            <div>
              <MostLikedFeatures analysis={analysis} type="COMPLAINED"/>
            </div>
          </div>
        </>
      )}
    </main>
  );
}

export default Home;
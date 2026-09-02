import '@fontsource/montserrat';
import Searchbar from '../components/Searchbar';
import ProductResultCard from '../components/ProductResultCard';
import AiPreferenceCard from '../components/AiPreferenceCard'; 
import MostLikedFeatures from '../components/MostLikedFeatures';
import { useState, useEffect } from "react";
import { useToggleFollow } from "../hooks/useToggleFollow"; 
import { useLocation } from 'react-router-dom';
import WordPressPublisherCard from '../components/WordPressPublisherCard';

import { pollProductAnalysis, getLatestAnalyzedProduct, getAnalyzedProductById } from "../services/productService";

function Home() {
  const location = useLocation();
  const passedProductId = location.state?.productId;

  const [productUrl, setProductUrl] = useState("");
  const [product, setProduct] = useState(null);
  const [analysis, setAnalysis] = useState(null);
  const [isLoading, setIsLoading] = useState(false); 
  
  const { toggle } = useToggleFollow();

  useEffect(() => {
    async function loadInitialProduct() {
      try {
        setIsLoading(true); 
        let data;

        // EĞER DASHBOARD'DAN ID GELDİYSE ÖZEL ÜRÜNÜ ÇEK
        if (passedProductId) {
            data = await getAnalyzedProductById(passedProductId);
        } 
        // GELMEDİYSE (Sayfaya normal girildiyse) EN SON ÜRÜNÜ ÇEK
        else {
            data = await getLatestAnalyzedProduct();
        }
        
        if (data && data.product && data.analysis) {
          setProduct(data.product);
          setAnalysis(data.analysis);
        }
      } catch (error) {
        console.error("Ürün yüklenirken hata oluştu:", error);
      } finally {
        setIsLoading(false); 
      }
    }

    loadInitialProduct();
  }, [passedProductId]);


  const [loadingMessage, setLoadingMessage] = useState("Ürün verileri yükleniyor, lütfen bekleyin...");

  const handleScrap = async () => {
    if (!productUrl) return; 

    try {
      setIsLoading(true); 
      setLoadingMessage("Analiz sıraya alındı, veriler kazınıyor...");
      
      // pollProductAnalysis fonksiyonu işlem bitene kadar arkada her 3 saniyede bir /status atar
      const data = await pollProductAnalysis(productUrl, (status, message) => {
          setLoadingMessage(message);  
      });
      
      setProduct(data.product);
      setAnalysis(data.analysis);

    } catch (error) {
      console.error("Analiz sırasında hata oluştu:", error);
      alert(error.message || "Ürün analiz edilemedi. Lütfen linki kontrol edin.");
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
          Ürün verileri yükleniyor, lütfen bekleyin...
        </div>
      )}

      {!isLoading && product && analysis && (
        <>
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 mt-8">
            <div>
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
          {/* 🌟 YENİ EKLENEN WORDPRESS ÖNİZLEME VE YAYINLAMA PANELI */}
          <WordPressPublisherCard product={product} analysis={analysis} />
        </>
      )}
    </main>
  );
}

export default Home;
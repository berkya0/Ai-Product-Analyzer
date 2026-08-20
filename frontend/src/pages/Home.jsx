import '@fontsource/montserrat';
import Searchbar from '../components/Searchbar';
import ProductResultCard from '../components/ProductResultCard';
import AiPreferenceCard from '../components/AiPreferenceCard'; 
import MostLikedFeatures from '../components/MostLikedFeatures';
import { scrapProduct, getAnalysis } from "../services/productService";
import { useState } from "react";


function Home() {
  const [productUrl, setProductUrl] = useState("");
  const [product, setProduct] = useState(null);
  const [analysis, setAnalysis] = useState(null);
  const [aiSummary,setAiSummary]=useState("");
  
  const handleScrap = async () => {
    const data = await scrapProduct(productUrl);
    setProduct(data);

    const analysisData = await getAnalysis(data.id);
    setAnalysis(analysisData);
    setAiSummary(analysisData.summary)

    console.log(analysisData);
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
    

      { //KARTLAR ALANI
      }
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 mt-8">
        
        <div>
          <h2 className="text-slate-700 font-bold mb-3 text-lg">
            Ürün Sonuç
          </h2>
          <ProductResultCard product={product} aiSummary={aiSummary}/>
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

     

    </main>
  );
}

export default Home;
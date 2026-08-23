import { useEffect, useState } from "react";
import '@fontsource/montserrat';
import Searchbar from "../components/Searchbar";
import CompareProductList from "../components/CompareProductList";
import CompareResultCard from "../components/CompareResultCard"; // Sağ taraftaki karşılaştırma kartın
import { fetchProducts } from "../services/dashboardService";
import { compareProducts } from "../services/productService";

function Compare() {
    // Sol Taraftaki Ürün Listesi State'leri
    const [products, setProducts] = useState([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [loading, setLoading] = useState(false);
    const [searchTerm, setSearchTerm] = useState("");
    const pageSize = 8;

    // Sağ Taraftaki Karşılaştırma State'leri
    const [selectedProducts, setSelectedProducts] = useState([]); // Seçilen ürün objeleri (max 2)
    const [compareResults, setCompareResults] = useState(null);
    const [isComparing, setIsComparing] = useState(false);

    // 1. Sol taraftaki ürün listesini çekme (Dashboard ile aynı sonsuz kaydırma mantığı)
    useEffect(() => {
        async function loadProducts() {
            setLoading(true);
            try {
                const productsData = await fetchProducts(currentPage, pageSize);
                setProducts((prev) => {
                    if (currentPage === 0) return productsData.content || [];
                    return [...prev, ...(productsData.content || [])];
                });
            } catch (error) {
                console.error("Ürünler yüklenirken hata oluştu:", error);
            } finally {
                setLoading(false);
            }
        }
        loadProducts();
    }, [currentPage]);

    // 2. Sol taraftaki scroll ile sayfalama
    const handleScroll = (e) => {
        const { scrollTop, scrollHeight, clientHeight } = e.target;
        if (scrollHeight - scrollTop <= clientHeight + 5 && !loading) {
            setCurrentPage((prev) => prev + 1);
        }
    };

    // 3. Ürün Seçme / Seçimden Çıkarma
    const handleSelectProduct = (product) => {
        const isAlreadySelected = selectedProducts.some((p) => p.id === product.id);

        if (isAlreadySelected) {
            // Zaten seçiliyse listeden çıkar
            const updated = selectedProducts.filter((p) => p.id !== product.id);
            setSelectedProducts(updated);
            if (updated.length < 2) setCompareResults(null);
        } else {
            // Yeni seçildiyse ekle (max 2)
            if (selectedProducts.length >= 2) {
                alert("En fazla 2 ürün karşılaştırabilirsiniz.");
                return;
            }
            setSelectedProducts([...selectedProducts, product]);
        }
    };

    // 4. İki ürün seçildiğinde otomatik olarak Backend POST /api/products/compare isteği at
    useEffect(() => {
        async function fetchComparison() {
            if (selectedProducts.length === 2) {
                setIsComparing(true);
                try {
                    const productIds = selectedProducts.map((p) => p.id);
                    const results = await compareProducts(productIds);
                    setCompareResults(results);
                } catch (error) {
                    console.error("Karşılaştırma sırasında hata:", error);
                    alert("Karşılaştırma verileri çekilemedi.");
                } finally {
                    setIsComparing(false);
                }
            }
        }
        fetchComparison();
    }, [selectedProducts]);

    // Arama filtresi
    const filteredProducts = products.filter((p) =>
        p.name.toLowerCase().includes(searchTerm.toLowerCase())
    );

    return (
        <main className="flex-1 min-h-screen font-['Montserrat'] bg-[#F8FAFC] p-8">
            {/* Header Yapısı (Home.jsx ile aynı hiyerarşide) */}
            <header className="mb-6">
                <h1 className="text-black font-semibold text-2xl">Karşılaştırma</h1>
                <p className="text-sm text-[#747373] font-semibold mt-1">
                    Seçeceğiniz ürünleri özelliklerine ve satıcısına göre kıyaslayabilirsiniz
                </p>
            </header>

            {/* Arama Çubuğu */}
            <Searchbar
                placeholder="Ürün ara..."
                className="mb-6"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                showButton={false}
            />

            {/* Sol Liste ve Sağ Sonuç Kartı Düzeni */}
            <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
                
                {/* SOL TARAF: Ürün Liste Paneli (4 Kolon) */}
                <div 
                    onScroll={handleScroll}
                    className="lg:col-span-4 max-h-[650px] overflow-y-auto pr-2 custom-scrollbar"
                >
                    <CompareProductList
                        products={filteredProducts}
                        selectedProductIds={selectedProducts.map((p) => p.id)}
                        onSelectProduct={handleSelectProduct}
                    />
                    {loading && (
                        <div className="text-center py-3 text-sm text-slate-500 font-semibold">
                            Ürünler yükleniyor...
                        </div>
                    )}
                </div>

                {/* SAĞ TARAF: Karşılaştırma Sonucu Paneli (8 Kolon) */}
                <div className="lg:col-span-8">
                    {isComparing ? (
                        <div className="bg-white p-12 rounded-2xl border border-slate-100 text-center font-semibold text-slate-500">
                            Ürünler kıyaslanıyor, lütfen bekleyin...
                        </div>
                    ) : compareResults ? (
                        <CompareResultCard results={compareResults} />
                    ) : (
                        <div className="bg-white p-12 rounded-2xl border border-blue-100 text-center text-slate-400 font-medium">
                            {selectedProducts.length === 0
                                ? "Karşılaştırma yapmak için sol taraftan 2 ürün seçin."
                                : "Karşılaştırmayı başlatmak için 1 ürün daha seçin."}
                        </div>
                    )}
                </div>

            </div>
        </main>
    );
}

export default Compare;
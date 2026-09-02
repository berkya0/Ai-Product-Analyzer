import PageHeader from "../components/PageHeader";
import StateCards from "../components/StateCards";
import { useEffect, useState, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { fetchStates, fetchProducts, reAnalyzeProduct } from "../services/dashboardService"; 
import DashboardProducts from "../components/DashboardProductCard";

import '@fontsource/montserrat';
import Searchbar from "../components/Searchbar";
import { deleteProduct } from "../services/productService";
import { useToggleFollow } from "../hooks/useToggleFollow";

function Dashboard() {
    const navigate = useNavigate();
    const [dashboardStats, setDashboardStats] = useState(null);
    const [dashboardProducts, setDashboardProducts] = useState(null);
    const [currentPage, setCurrentPage] = useState(0);
    const [loading, setLoading] = useState(false);
    
    const pageSize = 100; 
    const scrollContainerRef = useRef(null); 

    const { toggle } = useToggleFollow();
    
    const handleProductClick = (id) => {
        navigate('/', { state: { productId: id } }); 
    };

    useEffect(() => {
        async function loadDashboard() {
            const data = await fetchStates();
            setDashboardStats(data);
        }
        loadDashboard();
    }, []);

    useEffect(() => {
        async function loadProducts() {
            setLoading(true);
            const productsData = await fetchProducts(currentPage, pageSize);
            
            setDashboardProducts((prev) => {
                if (!prev || currentPage === 0) return productsData;
                return {
                    ...productsData,
                    content: [...prev.content, ...productsData.content]
                };
            });
            setLoading(false);
        }
        loadProducts();
        
    }, [currentPage]);

    // container dolmadıysa (scrollbar çıkmadıysa) otomatik sonraki sayfayı çek
    useEffect(() => {
        if (!dashboardProducts || loading) return;
        const container = scrollContainerRef.current;
        if (!container) return;

        const yuklenenUrunSayisi = dashboardProducts.content?.length || 0;
        const toplamUrunSayisi = dashboardProducts.totalElements || 0;

        if (
            container.scrollHeight <= container.clientHeight &&
            yuklenenUrunSayisi < toplamUrunSayisi
        ) {
            setCurrentPage((prev) => prev + 1);
        }
    }, [dashboardProducts, loading]);

    const handleScroll = (e) => {
        const { scrollTop, scrollHeight, clientHeight } = e.currentTarget;
        const kalanPiksel = scrollHeight - (scrollTop + clientHeight);

        if (kalanPiksel <= 50 && !loading) {
            const yuklenenUrunSayisi = dashboardProducts?.content?.length || 0;
            const toplamUrunSayisi = dashboardProducts?.totalElements || 0;

            if (dashboardProducts && yuklenenUrunSayisi < toplamUrunSayisi) {
                console.log("🚀 Daha fazla ürün var, yeni sayfa isteniyor. Mevcut sayfa:", currentPage + 1);
                setCurrentPage((prev) => prev + 1);
            } else {
                console.log("🛑 Tüm ürünler zaten listelendi, başka ürün yok.");
            }
        }
    };

    async function handleDelete(id) {
        try {
            await deleteProduct(id);
            setDashboardProducts(prev => ({
                ...prev,
                content: prev.content.filter(product => product.id !== id)
            }));
        } catch (error) {
            console.error(error);
        }
    }

    async function handleReAnalyze(id, productUrl) {
        try {
            const updatedData = await reAnalyzeProduct(productUrl);
            setDashboardProducts(prev => ({
                ...prev,
                content: prev.content.map(product => 
                    product.id === id ? { ...product, ...updatedData } : product
                )
            }));
            console.log("Ürün başarıyla güncellendi!");
        } catch (error) {
            console.error("Yeniden analiz sırasında hata:", error);
            alert("Analiz güncellenirken bir hata oluştu: " + error.message);
        }
    }

    const handleToggleMute = (id, currentIsFollowing) => {
        toggle(id, currentIsFollowing, (newFollowingStatus) => {
            setDashboardProducts(prev => ({
                ...prev,
                content: prev.content.map(product => 
                    product.id === id ? { ...product, isFollowing: newFollowingStatus } : product
                )
            }));

            setDashboardStats(prevStats => {
                if (!prevStats) return prevStats; 
                return {
                    ...prevStats,
                    totalFollowedAnalysis: newFollowingStatus 
                        ? prevStats.totalFollowedAnalysis + 1 
                        : prevStats.totalFollowedAnalysis - 1
                };
            });
        });
    };

    return (
        <div className="font-[Montserrat] p-8 min-h-screen flex flex-col gap-5">
            <PageHeader />
            
            <div className="flex gap-16 mt-6">
                <StateCards title="Toplam Analiz" value={dashboardStats?.totalAnalysis} color="#FFFEEC" borderColor="#E6C84A" />
                <StateCards title="Başarılı" value={dashboardStats?.successfulAnalysis} color="#F0FDF4" borderColor="#22C55E" />
                <StateCards title="Başarısız" value={dashboardStats?.failedAnalysis} color="#FFF1F2" borderColor="#EF4444" />
                <StateCards title="Takip Edilen" value={dashboardStats?.totalFollowedAnalysis} color="#EDF4FB" borderColor="#4A6FA5" />
            </div>

            <Searchbar className="mt-15" />
            
            <div 
                ref={scrollContainerRef}
                onScroll={handleScroll}
                className="flex flex-col gap-2 mt-4 max-h-[550px] overflow-y-auto pr-2 custom-scrollbar"
            >
                {dashboardProducts?.content?.map((product, index) => (
                    <DashboardProducts 
                        key={`${product.id}-${index}`} 
                        item={product} 
                        onDelete={handleDelete} 
                        onRefresh={handleReAnalyze} 
                        onToggleMute={handleToggleMute}
                        onProductClick={handleProductClick} 
                    />
                ))}

                {loading && (
                    <div className="text-center py-3 text-sm text-slate-500 font-semibold">
                        Ürünler yükleniyor...
                    </div>
                )}
            </div>
        </div>
    );
}

export default Dashboard;
import PageHeader from "../components/PageHeader";
import StateCards from "../components/StateCards";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { fetchStates, fetchProducts, reAnalyzeProduct } from "../services/dashboardService"; 
import DashboardProducts from "../components/DashboardProductCard";

import '@fontsource/montserrat';
import Searchbar from "../components/Searchbar";
import { deleteProduct } from "../services/productService";
import { useToggleFollow } from "../hooks/useToggleFollow"; // HOOK'U IMPORT ETTİK

function Dashboard() {
    const navigate = useNavigate();
    const [dashboardStats, setDashboardStats] = useState(null);
    const [dashboardProducts, setDashboardProducts] = useState(null);
    const [currentPage, setCurrentPage] = useState(0);
    const [loading, setLoading] = useState(false);
    const pageSize = 6;

    // HOOK'U ÇAĞIRIYORUZ
    const { toggle } = useToggleFollow();
    const handleProductClick = (id) => {
        // Anasayfaya gidiyoruz ve arkadan gizlice productId'yi yolluyoruz
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

    const handleScroll = (e) => {
        const { scrollTop, scrollHeight, clientHeight } = e.target;
        
        if (scrollHeight - scrollTop <= clientHeight + 5 && !loading) {
            if (dashboardProducts && currentPage < dashboardProducts.totalPages) {
                setCurrentPage((prev) => prev + 1);
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

    // YENİ VE TERTEMİZ HOOK KULLANIMI
    const handleToggleMute = (id, currentIsFollowing) => {
        toggle(id, currentIsFollowing, (newFollowingStatus) => {
            // 1. Tablodaki ürünü güncelle
            setDashboardProducts(prev => ({
                ...prev,
                content: prev.content.map(product => 
                    product.id === id ? { ...product, isFollowing: newFollowingStatus } : product
                )
            }));

            // 2. Renkli istatistik kartını güncelle
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
            
            {/* İŞTE DEĞİŞİKLİK YAPTIĞIMIZ YER BURASI */}
            <div 
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
                        onProductClick={handleProductClick} // Yeni eklediğimiz yönlendirme prop'u
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
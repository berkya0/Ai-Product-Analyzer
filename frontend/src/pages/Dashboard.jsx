import PageHeader from "../components/PageHeader";
import StateCards from "../components/StateCards";
import { useEffect, useState } from "react";
import { fetchStates, fetchProducts,reAnalyzeProduct} from "../services/dashboardService";
import DashboardProducts from "../components/DashboardProductCard";

import '@fontsource/montserrat';
import Searchbar from "../components/Searchbar";
import { deleteProduct } from "../services/productService";

function Dashboard() {
    const [dashboardStats, setDashboardStats] = useState(null);
    const [dashboardProducts, setDashboardProducts] = useState(null);
    const [currentPage, setCurrentPage] = useState(0);
    const [loading, setLoading] = useState(false);
    const pageSize = 6;

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

    // Scroll olayını dinleyen fonksiyon
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
            
            //Tost mesajı eklenebilir ileride
            console.log("Ürün başarıyla güncellendi!");

        } catch (error) {
            console.error("Yeniden analiz sırasında hata:", error);
            alert("Analiz güncellenirken bir hata oluştu: " + error.message);
        }
    }

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
            
            {/* Scroll Eklenen Ürün Listesi Kapsayıcısı */}
            <div 
                onScroll={handleScroll}
                className="flex flex-col gap-2 mt-4 max-h-[550px] overflow-y-auto pr-2 custom-scrollbar"
            >
                {dashboardProducts?.content?.map((product, index) => (
                    <DashboardProducts key={`${product.id}-${index}`} item={product} onDelete={handleDelete} onRefresh={handleReAnalyze}/>
                ))}

                {/* Yükleniyor Göstergesi */}
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
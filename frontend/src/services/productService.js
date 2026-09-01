// export async function scrapProduct(productUrl) {
//     const response = await fetch(
//         `http://localhost:8080/ai/analyze`,
//         {
//             method: "POST",
//             headers: {
//                 "Content-Type": "application/json", 
//             },
//             body: JSON.stringify({ productUrl: productUrl }) 
//         }
//     );
    
//     const data = await response.json();

//     if (!response.ok) {
//         throw new Error(data.message || "Bir hata oluştu");
//     }
    
//     return data;
// }

// 1. Adım: Analizi başlatır ve arka plan ID'sini (productId) döner
export async function startAnalysis(productUrl) {
    const response = await fetch(
        `http://localhost:8080/ai/analyze`,
        {
            method: "POST",
            headers: {
                "Content-Type": "application/json", 
            },
            body: JSON.stringify({ productUrl: productUrl }) 
        }
    );
    
    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.message || "Analiz başlatılamadı");
    }
    
    return data; // { message, productId, status: "PENDING" } döner
}

// 2. Adım: Belirli bir productId'nin anlık durumunu sorgular
export async function checkAnalysisStatus(productId) {
    const response = await fetch(`http://localhost:8080/ai/status/${productId}`);
    
    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.message || "Durum sorgulanamadı.");
    }
    
    return data; 
}
// --- WORDPRESS ENTEGRASYON SERVİSLERİ ---

// Aktif siteleri getir (Dropdown için)
export async function getActiveSites() {
    const response = await fetch("http://localhost:8080/api/sites");
    if (!response.ok) {
        throw new Error("Siteler yüklenemedi.");
    }
    return await response.json();
}

// Yayınlamadan önce HTML çıktısını önizlemek için
export async function getPreviewHtml(combinedData) {
    const response = await fetch("http://localhost:8080/api/wordpress/preview", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(combinedData),
    });

    if (!response.ok) {
        throw new Error("Önizleme oluşturulamadı.");
    }
    return await response.text(); // Backend ResponseEntity<String> döndüğü için text() alıyoruz
}

// Seçilen siteye WordPress üzerinden makaleyi fırlatmak için


export async function publishToWordPress(siteId, combinedData) {
    const response = await fetch(`http://localhost:8080/api/wordpress/publish/${siteId}`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(combinedData),
    });

    // Yanıtı önce düz metin olarak alıyoruz
    const responseText = await response.text();

    // 1. HATA DURUMU
    if (!response.ok) {
        try {
            const errorData = JSON.parse(responseText);
            throw new Error(errorData.message || "WordPress'e yayınlama başarısız oldu.");
        } catch (e) {
            throw new Error(responseText || "WordPress'e yayınlama başarısız oldu.");
        }
    }

    // 2. BAŞARI DURUMU (Eksik olan ve eklemen gereken yer burası!)
    // WordPress'ten gelen JSON metnini parse edip dışarı döndürüyoruz ki ID ve Link'i görebilelim
    try {
        return responseText ? JSON.parse(responseText) : { success: true };
    } catch (e) {
        return { success: true, rawResponse: responseText };
    }
}
// Kayıtlı bir WordPress sitesini silmek için
export async function deleteSite(siteId) {
    const response = await fetch(`http://localhost:8080/api/sites/${siteId}`, {
        method: "DELETE",
    });
    if (!response.ok) {
        throw new Error("Site silinirken hata oluştu");
    }
    return true;
}

// Yeni site kaydetmek istersen (İleride bir modal yaparsan kullanabilirsin)
export async function addSite(siteData) {
    const response = await fetch("http://localhost:8080/api/sites", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(siteData),
    });
    if (!response.ok) throw new Error("Site kaydedilemedi.");
    return await response.text();
}

// 3. Adım: Kullanıcının butonuna basınca arka arkaya sorgu atacak (Polling) akış yöneticisi
export async function pollProductAnalysis(productUrl, onProgress, intervalMs = 3000) {
    // 1. Analizi tetikle ve ID al
    const initData = await startAnalysis(productUrl);
    const productId = initData.productId;

    if (onProgress) {
        onProgress("PENDING", "Analiz sıraya alındı, veriler kazınıyor...");
    }

    // 2. Döngü (Promise tabanlı polling) başlat
    return new Promise((resolve, reject) => {
        const timer = setInterval(async () => {
            try {
                const statusData = await checkAnalysisStatus(productId);

                // Eğer hala PENDING durumundaysa döngü devam etsin
                if (statusData.status === "PENDING") {
                    return; 
                }

                // Eğer FAILED olduysa döngüyü durdur ve hata fırlat
                if (statusData.status === "FAILED") {
                    clearInterval(timer);
                    reject(new Error("Analiz sırasında bir hata oluştu."));
                    return;
                }

                // Eğer işlem bittiyse (SUCCESS / Veri geldiyse) döngüyü durdur ve sonucu dön
                clearInterval(timer);
                resolve(statusData); // product ve analysis verilerini içeren combined response

            } catch (error) {
                clearInterval(timer);
                reject(error);
            }
        }, intervalMs);
    });
}


export async function deleteProduct(productId) {
    const response =await fetch(
         `http://localhost:8080/product/delete/${productId}`,
         {
            method:"DELETE",
         }
         
    );
    if (!response.ok) {
        throw new Error("Ürün bulunamadı");
    }
    
    
}
export const getLatestAnalyzedProduct = async () => {
    try {
       
        const response = await fetch("http://localhost:8080/ai/latest");
        
        if (!response.ok) {
            throw new Error("Son analiz edilen ürün getirilemedi.");
        }

        const data = await response.json();
        
    
        if (!data || !data.product) {
            return null;
        }

        return {
            product: data.product, 
            analysis: data.analysis 
        };

    } catch (error) {
        console.error("En son analiz edilen ürün çekilirken hata oluştu:", error);
        throw error;
    }
};

export async function compareProducts(productIds) {
    const response = await fetch("http://localhost:8080/product/compare", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({ productIds }),
    });

    if (!response.ok) {
        throw new Error("Karşılaştırma isteği başarısız oldu.");
    }

    return await response.json();
}
export const getAnalyzedProductById = async (id) => {
    try {
        // Backend'de bu id'ye ait ürünü ve analizini dönecek API adresi
        const response = await fetch(`http://localhost:8080/ai/product/${id}`);
        if (!response.ok) throw new Error("Ürün getirilemedi.");
        const data = await response.json();
        
        return { product: data.product, analysis: data.analysis };
    } catch (error) {
        console.error("ID ile ürün çekilirken hata:", error);
        throw error;
    }
};
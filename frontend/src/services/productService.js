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
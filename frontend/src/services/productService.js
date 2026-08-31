export async function scrapProduct(productUrl) {
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
        throw new Error(data.message || "Bir hata oluştu");
    }
    
    return data;
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
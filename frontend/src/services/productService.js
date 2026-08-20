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
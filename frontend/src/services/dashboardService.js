export async function fetchStates(){
     const response = await fetch(
        `http://localhost:8080/dashboard/get-cards`,
        {
            method: "GET",
        }
    );
    const data = await response.json();
    if(!response.ok){
        throw new Error(data.message);

    }
    return data;

}

export async function fetchProducts(page = 0, size = 10) {
  try {
    const response = await fetch(
      `http://localhost:8080/dashboard/get-products?page=${page}&size=${size}`,
      {
        method: "GET",
      }
    );

    if (!response.ok) {
      throw new Error(`HTTP Hatası! Statü: ${response.status}`);
    }

    const data = await response.json();
    return data; 
  } catch (error) {
    console.error("Ürünler çekilirken hata oluştu:", error);
  }
}
export async function reAnalyzeProduct(productUrl) {
  try {
    const response = await fetch(
      `http://localhost:8080/ai/re-analyze`, 
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ productUrl: productUrl }), 
      }
    );

    if (!response.ok) {
        const errorData = await response.json().catch(() => null); 
        throw new Error(errorData?.message || `HTTP Hatası! Statü: ${response.status}`);
    }

    const data = await response.json();
    return data;
    
  } catch (error) {
    console.error("Yeniden analiz isteği sırasında hata oluştu:", error);
    throw error; 
  }
}


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
        headers: {
          "Content-Type": "application/json",
        },
      }
    );

    if (!response.ok) {
      throw new Error(`HTTP Hatası! Statü: ${response.status}`);
    }

    const data = await response.json();

    // Spring Data Page kullandığın için elemanlar data.content içindedir
    const products = data.content || [];

    console.log("Gelen Sayfa Bilgisi:", {
      totalElements: data.totalElements,
      totalPages: data.totalPages,
      currentPage: data.number,
    });

    // Döngü ile konsola yazdırma
    products.forEach((product, index) => {
      console.log(`--- Ürün #${index + 1} ---`);
      console.log("ID:", product.id);
      console.log("Adı:", product.name);
      console.log("Görsel:", product.imageUrl);
      console.log("AI Skor:", product.aiScore);
      console.log("Takipte mi:", product.isFollowing);
      console.log("Statü:", product.status);
      console.log("Tarih:", product.updatedAt);
    });

    return data; // İleride UI tarafında (data.content, data.totalPages vb.) kullanabilmek için döndürüyoruz
  } catch (error) {
    console.error("Ürünler çekilirken hata oluştu:", error);
  }
}
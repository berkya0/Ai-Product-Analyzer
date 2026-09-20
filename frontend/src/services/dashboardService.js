import { fetchWithAuth } from "./apiClient";

/**
 * Dashboard kart verilerini (istatistikleri) getirir
 */
export async function fetchStates() {
  return await fetchWithAuth("/dashboard/cards");
}

/**
 * Dashboard için sayfalanmış ürün listesini getirir
 */
export async function fetchProducts(page = 0, size = 10) {
  try {
    return await fetchWithAuth(`/dashboard/products?page=${page}&size=${size}`);
  } catch (error) {
    console.error("Ürünler çekilirken hata oluştu:", error);
    throw error;
  }
}

/**
 * Ürünün yorumlarını yeniden analiz eder
 */
export async function reAnalyzeProduct(productUrl) {
  return await fetchWithAuth("/ai/re-analyze", {
    method: "POST",
    body: JSON.stringify({ productUrl }),
  });
}

/**
 * Ürünün takip durumunu günceller (PATCH /api/product/set-following/{productId}?isFollowing=true/false)
 */
export async function setProductFollowing(productId, isFollowing) {
  try {
    return await fetchWithAuth(`/product/set-following/${productId}?isFollowing=${isFollowing}`, {
      method: "PATCH",
    });
  } catch (error) {
    console.error("Takip durumu güncellenirken hata oluştu:", error);
    throw error;
  }
}
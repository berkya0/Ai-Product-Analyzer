import { fetchWithAuth } from "./apiClient";

// 1. Analiz Başlatma
export async function startAnalysis(productUrl) {
  return await fetchWithAuth("/ai/analyze", {
    method: "POST",
    body: JSON.stringify({ productUrl }),
  });
}

// 2. Analiz Durum Sorgulama
export async function checkAnalysisStatus(productId) {
  return await fetchWithAuth(`/ai/status/${productId}`);
}

// 3. Polling ile Analiz Takibi
export async function pollProductAnalysis(productUrl, onProgress, intervalMs = 3000) {
  const initData = await startAnalysis(productUrl);
  const productId = initData.productId;

  if (onProgress) {
    onProgress("PENDING", "Analiz sıraya alındı, veriler kazınıyor...");
  }

  return new Promise((resolve, reject) => {
    const timer = setInterval(async () => {
      try {
        const statusData = await checkAnalysisStatus(productId);

        if (statusData.status === "PENDING") return;

        if (statusData.status === "FAILED") {
          clearInterval(timer);
          reject(new Error("Analiz sırasında bir hata oluştu."));
          return;
        }

        clearInterval(timer);
        resolve(statusData);
      } catch (error) {
        clearInterval(timer);
        reject(error);
      }
    }, intervalMs);
  });
}

// En son analiz edilen ürün
export async function getLatestAnalyzedProduct() {
  const data = await fetchWithAuth("/ai/latest");
  if (!data || !data.product) return null;
  return { product: data.product, analysis: data.analysis };
}

// ID'ye göre ürün getirme
export async function getAnalyzedProductById(id) {
  const data = await fetchWithAuth(`/ai/product/${id}`);
  return { product: data.product, analysis: data.analysis };
}

// Ürün Silme
export async function deleteProduct(productId) {
  return await fetchWithAuth(`/product/delete/${productId}`, {
    method: "DELETE",
  });
}

// Ürün Karşılaştırma
export async function compareProducts(productIds) {
  return await fetchWithAuth("/product/compare", {
    method: "POST",
    body: JSON.stringify({ productIds }),
  });
}

/* ==========================================================================
   WORDPRESS & SITE ENTEGRASYONLARI
   ========================================================================== */

// Siteleri Getir
export async function getActiveSites() {
  return await fetchWithAuth("/sites");
}

// Yeni Site Ekle
export async function addSite(siteData) {
  return await fetchWithAuth(
    "/sites",
    {
      method: "POST",
      body: JSON.stringify(siteData),
    },
    "text"
  );
}

// Site Sil
export async function deleteSite(siteId) {
  return await fetchWithAuth(`/sites/${siteId}`, {
    method: "DELETE",
  });
}

// HTML Önizleme Oluştur
export async function getPreviewHtml(combinedData) {
  return await fetchWithAuth(
    "/wordpress/preview",
    {
      method: "POST",
      body: JSON.stringify(combinedData),
    },
    "text" // HTML yanıt döndüğü için text seçildi
  );
}

// WordPress'e Gönder (Title ve Status parametreleri eklendi)
export async function publishToWordPress(siteId, combinedData, title = "", status = "publish") {
  const params = new URLSearchParams();
  if (title) params.append("title", title);
  if (status) params.append("status", status);

  const queryString = params.toString() ? `?${params.toString()}` : "";

  return await fetchWithAuth(`/wordpress/publish/${siteId}${queryString}`, {
    method: "POST",
    body: JSON.stringify(combinedData),
  });
}
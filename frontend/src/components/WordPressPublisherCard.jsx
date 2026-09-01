import React, { useState, useEffect } from "react";
import { getActiveSites, getPreviewHtml, publishToWordPress } from "../services/productService";
import { FiGlobe, FiEye, FiSend } from "react-icons/fi";

function WordPressPublisherCard({ product, analysis }) {
  const [sites, setSites] = useState([]);
  const [selectedSiteId, setSelectedSiteId] = useState("");
  const [previewHtml, setPreviewHtml] = useState("");
  const [isPreviewLoading, setIsPreviewLoading] = useState(false);
  const [isPublishing, setIsPublishing] = useState(false);

  // Sayfa açıldığında kayıtlı aktif siteleri çekiyoruz
  useEffect(() => {
    async function loadSites() {
      try {
        const data = await getActiveSites();
        setSites(data);
        if (data.length > 0) {
          setSelectedSiteId(data[0].id);
        }
      } catch (error) {
        console.error("Siteler yüklenirken hata:", error);
      }
    }
    loadSites();
  }, []);

  const combinedData = { product, analysis };

  // 1. Önizleme Butonu (HTML yapısını test etme)
  const handlePreview = async () => {
    try {
      setIsPreviewLoading(true);
      const html = await getPreviewHtml(combinedData);
      setPreviewHtml(html);
    } catch (error) {
      console.error("Önizleme hatası:", error);
      alert("Önizleme alınamadı.");
    } finally {
      setIsPreviewLoading(false);
    }
  };

  // 2. WordPress'e Yayınla Butonu
  const handlePublish = async () => {
    if (!selectedSiteId) {
      alert("Lütfen yayınlanacak bir site seçin.");
      return;
    }

    try {
      setIsPublishing(true);
      await publishToWordPress(selectedSiteId, combinedData);
      alert("Makale başarıyla WordPress sitesine yayınlandı! 🚀");
    } catch (error) {
      console.error("Yayınlama hatası:", error);
      alert("Yayınlama başarısız: " + error.message);
    } finally {
      setIsPublishing(false);
    }
  };

  return (
    <div className="bg-white rounded-2xl border border-slate-200 p-6 shadow-sm font-['Montserrat'] mt-8">
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 pb-4 border-b border-slate-100">
        <div>
          <h3 className="font-bold text-slate-900 text-lg flex items-center gap-2">
            <FiGlobe className="text-blue-600" /> WordPress Entegrasyonu
          </h3>
          <p className="text-xs text-slate-500 mt-0.5">
            Bu analizi WordPress sitene blog yazısı olarak aktarabilir ve önizleyebilirsin.
          </p>
        </div>

        {/* Site Seçim Dropdown ve Butonlar */}
        <div className="flex flex-wrap items-center gap-3 w-full sm:w-auto">
          <select
            value={selectedSiteId}
            onChange={(e) => setSelectedSiteId(e.target.value)}
            className="bg-slate-50 border border-slate-200 rounded-xl px-3 py-2.5 text-sm text-slate-700 focus:outline-none focus:ring-2 focus:ring-slate-300"
          >
            {sites.length === 0 ? (
              <option value="">Kayıtlı site bulunamadı</option>
            ) : (
              sites.map((site) => (
                <option key={site.id} value={site.id}>
                  {site.siteName}
                </option>
              ))
            )}
          </select>

          <button
            onClick={handlePreview}
            disabled={isPreviewLoading}
            className="bg-slate-100 hover:bg-slate-200 text-slate-700 px-4 py-2.5 rounded-xl font-medium text-sm transition cursor-pointer flex items-center gap-2"
          >
            <FiEye /> {isPreviewLoading ? "Yükleniyor..." : "Önizle"}
          </button>

          <button
            onClick={handlePublish}
            disabled={isPublishing || sites.length === 0}
            className="bg-[#0F172A] hover:bg-slate-800 text-white px-5 py-2.5 rounded-xl font-medium text-sm transition cursor-pointer flex items-center gap-2 shadow-sm disabled:opacity-50"
          >
            <FiSend /> {isPublishing ? "Yayınlanıyor..." : "WordPress'e Yayınla"}
          </button>
        </div>
      </div>

      {/* Canlı Önizleme Alanı (Eğer önizleme butonuna basıldıysa görünür) */}
      {previewHtml && (
        <div className="mt-6">
          <div className="flex items-center justify-between mb-3">
            <span className="text-xs font-bold text-slate-400 uppercase tracking-wider">
              Canlı HTML Önizleme Alanı (Sitede Nasıl Görünecek?)
            </span>
            <button 
              onClick={() => setPreviewHtml("")}
              className="text-xs text-red-500 font-semibold hover:underline"
            >
              Önizlemeyi Kapat
            </button>
          </div>

          <div className="border border-slate-200 rounded-xl p-6 bg-slate-50/50 overflow-x-auto">
            {/* Arka plandan gelen şık inline CSS'li HTML'i doğrudan ekrana basıyoruz */}
            <div dangerouslySetInnerHTML={{ __html: previewHtml }} />
          </div>
        </div>
      )}
    </div>
  );
}

export default WordPressPublisherCard;
import React, { useState, useEffect } from "react";
import { getActiveSites, getPreviewHtml, publishToWordPress } from "../services/productService";
import { FiGlobe, FiEye, FiSend } from "react-icons/fi";

function WordPressPublisherCard({ product, analysis }) {
  const [sites, setSites] = useState([]);
  const [selectedSiteId, setSelectedSiteId] = useState("");
  const [customTitle, setCustomTitle] = useState("");
  const [postStatus, setPostStatus] = useState("publish");
  const [previewHtml, setPreviewHtml] = useState("");
  const [isPreviewLoading, setIsPreviewLoading] = useState(false);
  const [isPublishing, setIsPublishing] = useState(false);

  useEffect(() => {
    if (product && product.name) {
      setCustomTitle(`${product.name} AI Destekli Detaylı Analizi`);
    }
  }, [product]);

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

  const handlePublish = async () => {
    if (!selectedSiteId) {
      alert("Lütfen yayınlanacak bir site seçin.");
      return;
    }

    try {
      setIsPublishing(true);
      const result = await publishToWordPress(selectedSiteId, combinedData, customTitle, postStatus);
      console.log("WordPress'ten gelen tam yanıt:", result);
      alert(`Makale başarıyla WordPress sitesine gönderildi! 🚀\nDurum: ${postStatus}`);
    } catch (error) {
      console.error("Yayınlama hatası:", error);
      alert("Yayınlama başarısız: " + error.message);
    } finally {
      setIsPublishing(false);
    }
  };

  return (
    <div className="bg-white rounded-2xl border border-slate-200 p-6 shadow-sm font-['Montserrat'] mt-8">
      {/* Üst Bilgi Başlığı */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 pb-4 border-b border-slate-100">
        <div>
          <h3 className="font-bold text-slate-900 text-lg flex items-center gap-2">
            <FiGlobe className="text-blue-600" /> WordPress Entegrasyonu
          </h3>
          <p className="text-xs text-slate-500 mt-0.5">
            Bu analizi WordPress sitene blog yazısı olarak aktarabilir ve önizleyebilirsin.
          </p>
        </div>
      </div>

      {/* Ana Kontrol Paneli (Bütünlük Korundu) */}
      <div className="mt-5 space-y-4">
        {/* Üst Satır: Başlık ve Statü Alanı */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-4 bg-slate-50/70 p-4 rounded-xl border border-slate-200/60">
          <div className="lg:col-span-2 flex flex-col gap-1.5">
            <label className="text-xs font-bold text-slate-600 uppercase tracking-wider">Yazı Başlığı</label>
            <input
              type="text"
              value={customTitle}
              onChange={(e) => setCustomTitle(e.target.value)}
              placeholder="Yazı başlığını girin..."
              className="bg-white border border-slate-200 rounded-xl px-3.5 py-2.5 text-sm text-slate-800 focus:outline-none focus:ring-2 focus:ring-slate-900/10 focus:border-slate-400 transition"
            />
          </div>

          <div className="flex flex-col gap-1.5">
            <label className="text-xs font-bold text-slate-600 uppercase tracking-wider">Yayın Durumu</label>
            <select
              value={postStatus}
              onChange={(e) => setPostStatus(e.target.value)}
              className="bg-white border border-slate-200 rounded-xl px-3.5 py-2.5 text-sm text-slate-800 focus:outline-none focus:ring-2 focus:ring-slate-900/10 focus:border-slate-400 transition cursor-pointer"
            >
              <option value="publish">Hemen Yayınla (Publish)</option>
              <option value="pending">Onay Bekliyor (Pending)</option>
              <option value="draft">Taslak (Draft)</option>
            </select>
          </div>
        </div>

        {/* Alt Satır: Hedef Site, Önizle ve Gönder Butonları */}
        <div className="flex flex-col sm:flex-row items-stretch sm:items-center justify-between gap-4 pt-2">
          <div className="flex items-center gap-3">
            <span className="text-xs font-bold text-slate-500 uppercase tracking-wider">Hedef Site:</span>
            <select
              value={selectedSiteId}
              onChange={(e) => setSelectedSiteId(e.target.value)}
              className="bg-slate-50 border border-slate-200 rounded-xl px-3.5 py-2.5 text-sm font-medium text-slate-800 focus:outline-none focus:ring-2 focus:ring-slate-900/10 focus:border-slate-400 transition cursor-pointer min-w-[180px]"
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
          </div>

          <div className="flex items-center gap-3 justify-end">
            <button
              onClick={handlePreview}
              disabled={isPreviewLoading}
              className="bg-slate-100 hover:bg-slate-200 text-slate-700 px-4 py-2.5 rounded-xl font-medium text-sm transition cursor-pointer flex items-center gap-2 active:scale-95"
            >
              <FiEye /> {isPreviewLoading ? "Yükleniyor..." : "Önizle"}
            </button>

            <button
              onClick={handlePublish}
              disabled={isPublishing || sites.length === 0}
              className="bg-[#0F172A] hover:bg-slate-800 text-white px-5 py-2.5 rounded-xl font-medium text-sm transition cursor-pointer flex items-center gap-2 shadow-sm disabled:opacity-50 active:scale-95"
            >
              <FiSend /> {isPublishing ? "Gönderiliyor..." : "WordPress'e Gönder"}
            </button>
          </div>
        </div>
      </div>

      {/* Canlı Önizleme Alanı */}
      {previewHtml && (
        <div className="mt-6 pt-6 border-t border-slate-100">
          <div className="flex items-center justify-between mb-3">
            <span className="text-xs font-bold text-slate-400 uppercase tracking-wider">
              Canlı HTML Önizleme Alanı (Sitede Nasıl Görünecek?)
            </span>
            <button 
              onClick={() => setPreviewHtml("")}
              className="text-xs text-red-500 font-semibold hover:underline cursor-pointer"
            >
              Önizlemeyi Kapat
            </button>
          </div>

          <div className="border border-slate-200 rounded-xl p-6 bg-slate-50/50 overflow-x-auto shadow-inner">
            <div dangerouslySetInnerHTML={{ __html: previewHtml }} />
          </div>
        </div>
      )}
    </div>
  );
}

export default WordPressPublisherCard;
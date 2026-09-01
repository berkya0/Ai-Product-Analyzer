import React, { useState, useEffect } from "react";
import { FiTrash2, FiGlobe, FiUser, FiCheckCircle } from "react-icons/fi";
import { deleteSite } from "../services/productService";

function SiteList({ sites, onSiteDeleted }) {
  const [loadingId, setLoadingId] = useState(null);

  const handleDelete = async (siteId, siteName) => {
    if (!window.confirm(`"${siteName}" sitesini silmek istediğinize emin misiniz?`)) {
      return;
    }

    try {
      setLoadingId(siteId);
      await deleteSite(siteId); 
      if (onSiteDeleted) {
        onSiteDeleted(siteId); 
      }
    } catch (error) {
      console.error("Site silme hatası:", error);
      alert("Site silinirken bir hata oluştu.");
    } finally {
      setLoadingId(null);
    }
  };

  if (!sites || sites.length === 0) {
    return (
      <div className="mt-8 bg-white rounded-2xl border border-slate-200 p-8 text-center text-slate-500 font-['Montserrat'] shadow-sm">
        Henüz kaydedilmiş bir WordPress sitesi bulunmuyor.
      </div>
    );
  }

  return (
    <div className="mt-8 bg-white rounded-2xl border border-slate-200 p-6 shadow-sm font-['Montserrat']">
      <div className="mb-4 pb-3 border-b border-slate-100">
        <h3 className="font-bold text-slate-900 text-lg flex items-center gap-2">
          <FiGlobe className="text-blue-600" /> Kayıtlı Siteler ({sites.length})
        </h3>
        <p className="text-xs text-slate-500 mt-0.5">
          Sisteme entegre edilmiş WordPress sitelerini buradan yönetebilir ve silebilirsin.
        </p>
      </div>

      <div className="overflow-x-auto">
        <table className="w-full text-left border-collapse">
          <thead>
            <tr className="border-b border-slate-100 text-xs font-bold text-slate-400 uppercase tracking-wider">
              <th className="py-3 px-4">Site Adı</th>
              <th className="py-3 px-4">Site Linki</th>
              <th className="py-3 px-4">Kullanıcı Adı</th>
              <th className="py-3 px-4">Durum</th>
              <th className="py-3 px-4 text-right">İşlem</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100 text-sm text-slate-700">
            {sites.map((site) => (
              <tr key={site.id} className="hover:bg-slate-50/50 transition">
                {/* Site Adı */}
                <td className="py-3.5 px-4 font-semibold text-slate-900 flex items-center gap-2">
                  <span className="w-2 h-2 rounded-full bg-emerald-500 inline-block"></span>
                  {site.siteName}
                </td>

                {/* Site Linki */}
                <td className="py-3.5 px-4 text-blue-600 truncate max-w-[200px]">
                  <a href={site.siteUrl} target="_blank" rel="noopener noreferrer" className="hover:underline">
                    {site.siteUrl}
                  </a>
                </td>

                {/* Kullanıcı Adı */}
                <td className="py-3.5 px-4 text-slate-600 flex items-center gap-1.5 pt-4">
                  <FiUser className="text-slate-400 text-xs" /> {site.username}
                </td>

                {/* Status Durumu */}
                <td className="py-3.5 px-4">
                  <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-medium bg-emerald-50 text-emerald-700 border border-emerald-200/60">
                    <FiCheckCircle className="text-emerald-500" /> Aktif
                  </span>
                </td>

                {/* Silme Tuşu */}
                <td className="py-3.5 px-4 text-right">
                  <button
                    onClick={() => handleDelete(site.id, site.siteName)}
                    disabled={loadingId === site.id}
                    className="bg-red-50 hover:bg-red-100 text-red-600 p-2 rounded-xl transition cursor-pointer disabled:opacity-50 inline-flex items-center justify-center"
                    title="Siteyi Sil"
                  >
                    <FiTrash2 className="text-sm" />
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default SiteList;
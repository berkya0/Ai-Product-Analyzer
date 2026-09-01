import React, { useState, useEffect } from 'react';
import AddSiteForm from '../components/AddSiteForm';
import SiteList from '../components/SiteList';
import { getActiveSites } from '../services/productService';

function Settings() {
  const [sites, setSites] = useState([]);

  // Kayıtlı siteleri sayfaya ilk girildiğinde yüklüyoruz
  const loadSites = async () => {
    try {
      const data = await getActiveSites();
      setSites(data);
    } catch (error) {
      console.error("Siteler yüklenirken hata:", error);
    }
  };

  useEffect(() => {
    loadSites();
  }, []);

  // Yeni site eklendiğinde veya silindiğinde listeyi anlık yenilemek için fonksiyon
  const handleSiteChange = () => {
    loadSites();
  };

  return (
    <main className="w-full h-full p-8 font-[inter] bg-slate-50 min-h-screen">
      <div className="max-w-5xl mx-auto">
        
        {/* Sayfa Başlığı */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-slate-800">Ayarlar</h1>
          <p className="text-slate-500 mt-2">Sistem yapılandırmalarını ve WordPress entegrasyonlarını buradan yönetebilirsiniz.</p>
        </div>

        {/* WordPress Site Ekleme Formu Componenti (Yeni site eklendiğinde listeyi tetiklemesi için prop geçebilirsin) */}
        <AddSiteForm onSiteAdded={handleSiteChange} />

        {/* Kayıtlı Siteleri Listeleyen Komponent */}
        <SiteList sites={sites} onSiteDeleted={handleSiteChange} />
        
      </div>
    </main>
  );
}

export default Settings;
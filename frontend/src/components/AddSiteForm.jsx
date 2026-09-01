import React, { useState } from 'react';
import { addSite }  from "../services/productService"; 

function AddSiteForm() {
  const [formData, setFormData] = useState({
    siteName: '',
    siteUrl: '',
    username: '',
    appPassword: ''
  });
  
  const [status, setStatus] = useState({ type: '', message: '' });
  const [isLoading, setIsLoading] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setStatus({ type: '', message: '' });

    try {
      // Backend'e form verisini gönderiyoruz
      const responseText = await addSite(formData);
      setStatus({ type: 'success', message: responseText });
      
      // Başarılı olunca formu temizle
      setFormData({ siteName: '', siteUrl: '', username: '', appPassword: '' });
    } catch (error) {
      setStatus({ type: 'error', message: error.message || 'Site eklenirken bir hata oluştu.' });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="bg-white p-6 rounded-2xl shadow-sm border border-slate-200">
      <h2 className="text-xl font-bold text-slate-800 mb-6">Yeni WordPress Sitesi Ekle</h2>
      
      {status.message && (
        <div className={`p-4 mb-6 rounded-lg text-sm ${status.type === 'success' ? 'bg-green-50 text-green-700 border border-green-200' : 'bg-red-50 text-red-700 border border-red-200'}`}>
          {status.message}
        </div>
      )}

      <form onSubmit={handleSubmit} className="space-y-5">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
          {/* Site Adı */}
          <div>
            <label className="block text-sm font-semibold text-slate-700 mb-1">Site Adı</label>
            <input
              type="text"
              name="siteName"
              value={formData.siteName}
              onChange={handleChange}
              placeholder="Örn: Teknoloji Blogu"
              required
              className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-colors"
            />
          </div>

          {/* Site URL */}
          <div>
            <label className="block text-sm font-semibold text-slate-700 mb-1">Site URL</label>
            <input
              type="url"
              name="siteUrl"
              value={formData.siteUrl}
              onChange={handleChange}
              placeholder="Örn: https://benimsitem.com"
              required
              className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-colors"
            />
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
          {/* Kullanıcı Adı */}
          <div>
            <label className="block text-sm font-semibold text-slate-700 mb-1">WordPress Kullanıcı Adı</label>
            <input
              type="text"
              name="username"
              value={formData.username}
              onChange={handleChange}
              placeholder="Örn: admin"
              required
              className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-colors"
            />
          </div>

          {/* App Password */}
          <div>
            <label className="block text-sm font-semibold text-slate-700 mb-1">Application Password</label>
            <input
              type="password"
              name="appPassword"
              value={formData.appPassword}
              onChange={handleChange}
              placeholder="••••••••••••••••"
              required
              className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-colors"
            />
            <p className="text-xs text-slate-500 mt-1">WordPress profili altından oluşturduğunuz uygulama şifresi.</p>
          </div>
        </div>

        <div className="flex justify-end mt-8">
          <button
            type="submit"
            disabled={isLoading}
            className={`px-6 py-2.5 rounded-lg text-white font-medium transition-colors ${
              isLoading ? 'bg-blue-400 cursor-not-allowed' : 'bg-[#0F172A] hover:bg-slate-800'
            }`}
          >
            {isLoading ? 'Kaydediliyor...' : 'Siteyi Kaydet'}
          </button>
        </div>
      </form>
    </div>
  );
}

export default AddSiteForm;
import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { FiUser, FiMail, FiLock, FiEye, FiEyeOff, FiArrowRight, FiCheck, FiAtSign } from "react-icons/fi";
import logo from "../assets/logo.png";
import { register } from "../services/authenticationService";

function Register() {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    fullName: "",
    username: "",
    email: "",
    password: "",
    confirmPassword: "",
  });

  const [showPassword, setShowPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorMessage("");

    if (formData.password !== formData.confirmPassword) {
      setErrorMessage("Şifreler birbiriyle eşleşmiyor.");
      return;
    }

    setIsLoading(true);

    try {
      // Backend /api/auth/register endpoint'ine istek atar
      await register({
        fullName: formData.fullName,
        username: formData.username,
        email: formData.email,
        password: formData.password,
      });

      setIsLoading(false);
      alert("Hesabınız başarıyla oluşturuldu!");
      navigate("/"); // Ana sayfaya yönlendirir
    } catch (error) {
      setIsLoading(false);
      setErrorMessage(error.message || "Kayıt olunurken bir hata oluştu.");
    }
  };

  return (
    <div className="min-h-screen font-['Montserrat'] bg-[#F8FAFC] flex items-center justify-center p-4 sm:p-6">
      <div className="w-full max-w-4xl bg-white rounded-3xl shadow-xl border border-slate-100 overflow-hidden grid grid-cols-1 md:grid-cols-2">
        
        {/* Sol Taraf - Görsel ve Bilgi Alanı */}
        <div className="bg-[#0F172A] p-8 sm:p-12 text-white flex flex-col justify-between relative overflow-hidden">
          <div className="z-10">
            <img src={logo} alt="Provega" className="w-44 mb-8" />
            <h2 className="text-2xl sm:text-3xl font-bold leading-tight">
              Aramıza Katılın 🚀
            </h2>
            <p className="text-slate-400 text-sm mt-4 leading-relaxed">
              Dakikalar içinde hesabınızı oluşturun, ürün analizlerinizi yönetin ve iş akışlarınızı otomatize edin.
            </p>

            <ul className="mt-8 space-y-3 text-xs text-slate-300">
              <li className="flex items-center gap-2">
                <span className="bg-emerald-500/20 text-emerald-400 p-1 rounded-full"><FiCheck /></span>
                Yapay Zeka Destekli Anlık Ürün Analizleri
              </li>
              <li className="flex items-center gap-2">
                <span className="bg-emerald-500/20 text-emerald-400 p-1 rounded-full"><FiCheck /></span>
                Tek Tıkla WordPress Entegrasyonu
              </li>
              <li className="flex items-center gap-2">
                <span className="bg-emerald-500/20 text-emerald-400 p-1 rounded-full"><FiCheck /></span>
                Detaylı Fiyat ve Rakip Takibi
              </li>
            </ul>
          </div>

          <div className="z-10 mt-8 text-xs text-slate-500">
            © {new Date().getFullYear()} Provega. Tüm hakları saklıdır.
          </div>

          <div className="absolute -bottom-16 -left-16 w-64 h-64 bg-blue-600/10 rounded-full blur-3xl pointer-events-none" />
        </div>

        {/* Sağ Taraf - Form Alanı */}
        <div className="p-8 sm:p-12 flex flex-col justify-center">
          <div className="mb-6">
            <h1 className="text-2xl font-bold text-slate-900">Hesap Oluştur</h1>
            <p className="text-sm text-slate-500 mt-1">
              Aşağıdaki bilgileri doldurarak hemen başlayın.
            </p>
          </div>

          {errorMessage && (
            <div className="mb-4 p-3.5 bg-red-50 border border-red-200 text-red-600 text-xs font-semibold rounded-xl">
              {errorMessage}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            {/* Ad Soyad */}
            <div className="flex flex-col gap-1.5">
              <label className="text-xs font-bold text-slate-600 uppercase tracking-wider">
                Ad Soyad
              </label>
              <div className="relative">
                <FiUser className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400 text-lg" />
                <input
                  type="text"
                  name="fullName"
                  required
                  value={formData.fullName}
                  onChange={handleChange}
                  placeholder="Ahmet Yılmaz"
                  className="w-full bg-slate-50 border border-slate-200 rounded-xl pl-10 pr-4 py-2.5 text-sm text-slate-800 focus:outline-none focus:ring-2 focus:ring-slate-900/10 focus:border-slate-400 transition"
                />
              </div>
            </div>

            {/* Kullanıcı Adı */}
            <div className="flex flex-col gap-1.5">
              <label className="text-xs font-bold text-slate-600 uppercase tracking-wider">
                Kullanıcı Adı
              </label>
              <div className="relative">
                <FiAtSign className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400 text-lg" />
                <input
                  type="text"
                  name="username"
                  required
                  value={formData.username}
                  onChange={handleChange}
                  placeholder="ahmetyilmaz"
                  className="w-full bg-slate-50 border border-slate-200 rounded-xl pl-10 pr-4 py-2.5 text-sm text-slate-800 focus:outline-none focus:ring-2 focus:ring-slate-900/10 focus:border-slate-400 transition"
                />
              </div>
            </div>

            {/* E-posta */}
            <div className="flex flex-col gap-1.5">
              <label className="text-xs font-bold text-slate-600 uppercase tracking-wider">
                E-Posta Adresi
              </label>
              <div className="relative">
                <FiMail className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400 text-lg" />
                <input
                  type="email"
                  name="email"
                  required
                  value={formData.email}
                  onChange={handleChange}
                  placeholder="ornek@domain.com"
                  className="w-full bg-slate-50 border border-slate-200 rounded-xl pl-10 pr-4 py-2.5 text-sm text-slate-800 focus:outline-none focus:ring-2 focus:ring-slate-900/10 focus:border-slate-400 transition"
                />
              </div>
            </div>

            {/* Şifre */}
            <div className="flex flex-col gap-1.5">
              <label className="text-xs font-bold text-slate-600 uppercase tracking-wider">
                Şifre
              </label>
              <div className="relative">
                <FiLock className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400 text-lg" />
                <input
                  type={showPassword ? "text" : "password"}
                  name="password"
                  required
                  value={formData.password}
                  onChange={handleChange}
                  placeholder="••••••••"
                  className="w-full bg-slate-50 border border-slate-200 rounded-xl pl-10 pr-10 py-2.5 text-sm text-slate-800 focus:outline-none focus:ring-2 focus:ring-slate-900/10 focus:border-slate-400 transition"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-3.5 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600 transition"
                >
                  {showPassword ? <FiEyeOff /> : <FiEye />}
                </button>
              </div>
            </div>

            {/* Şifre Tekrarı */}
            <div className="flex flex-col gap-1.5">
              <label className="text-xs font-bold text-slate-600 uppercase tracking-wider">
                Şifre Tekrarı
              </label>
              <div className="relative">
                <FiLock className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400 text-lg" />
                <input
                  type={showPassword ? "text" : "password"}
                  name="confirmPassword"
                  required
                  value={formData.confirmPassword}
                  onChange={handleChange}
                  placeholder="••••••••"
                  className="w-full bg-slate-50 border border-slate-200 rounded-xl pl-10 pr-4 py-2.5 text-sm text-slate-800 focus:outline-none focus:ring-2 focus:ring-slate-900/10 focus:border-slate-400 transition"
                />
              </div>
            </div>

            {/* Submit Button */}
            <button
              type="submit"
              disabled={isLoading}
              className="w-full mt-2 bg-[#0F172A] hover:bg-slate-800 text-white font-medium py-3 px-4 rounded-xl transition cursor-pointer flex items-center justify-center gap-2 active:scale-[0.99] shadow-sm disabled:opacity-50"
            >
              {isLoading ? (
                "Kaydediliyor..."
              ) : (
                <>
                  <span>Kayıt Ol</span>
                  <FiArrowRight className="text-lg" />
                </>
              )}
            </button>
          </form>

          {/* Giriş Yap Yönlendirmesi */}
          <div className="mt-6 text-center text-xs text-slate-500">
            Zaten hesabınız var mı?{" "}
            <Link
              to="/login"
              className="font-bold text-slate-900 hover:underline"
            >
              Giriş Yapın
            </Link>
          </div>
        </div>

      </div>
    </div>
  );
}

export default Register;
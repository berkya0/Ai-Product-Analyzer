import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { FiUser, FiLock, FiEye, FiEyeOff, FiArrowRight } from "react-icons/fi";
import logo from "../assets/logo.png";
import { login } from "../services/authenticationService";

function Login() {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    username: "",
    password: "",
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
    setIsLoading(true);

    try {
      // Backend /api/auth/login endpoint'ine istek atar
      await login(formData);
      setIsLoading(false);
      
      // Giriş başarılı olursa ana sayfaya yönlendirir
      navigate("/");
    } catch (error) {
      setIsLoading(false);
      setErrorMessage(error.message || "Giriş yapılırken bir hata oluştu.");
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
               Ürün Analiz Platformu
            </h2>
            <p className="text-slate-400 text-sm mt-4 leading-relaxed">
              Pazar yeri analizlerini, kullanıcı yorumlarını ve rakip verilerini saniyeler içinde raporlayın ve WordPress sitenizde yayınlayın.
            </p>
          </div>

          <div className="z-10 mt-12 text-xs text-slate-500">
            © {new Date().getFullYear()} Provega. Tüm hakları saklıdır.
          </div>

          <div className="absolute -bottom-16 -left-16 w-64 h-64 bg-blue-600/10 rounded-full blur-3xl pointer-events-none" />
        </div>

        {/* Sağ Taraf - Form Alanı */}
        <div className="p-8 sm:p-12 flex flex-col justify-center">
          <div className="mb-8">
            <h1 className="text-2xl font-bold text-slate-900">Hoş Geldiniz</h1>
            <p className="text-sm text-slate-500 mt-1">
              Hesabınıza erişmek için bilgilerinizi girin.
            </p>
          </div>

          {errorMessage && (
            <div className="mb-4 p-3.5 bg-red-50 border border-red-200 text-red-600 text-xs font-semibold rounded-xl">
              {errorMessage}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-5">
            {/* Kullanıcı Adı */}
            <div className="flex flex-col gap-1.5">
              <label className="text-xs font-bold text-slate-600 uppercase tracking-wider">
                Kullanıcı Adı
              </label>
              <div className="relative">
                <FiUser className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400 text-lg" />
                <input
                  type="text"
                  name="username"
                  required
                  value={formData.username}
                  onChange={handleChange}
                  placeholder="kullanici_adi"
                  className="w-full bg-slate-50 border border-slate-200 rounded-xl pl-10 pr-4 py-3 text-sm text-slate-800 focus:outline-none focus:ring-2 focus:ring-slate-900/10 focus:border-slate-400 transition"
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
                  className="w-full bg-slate-50 border border-slate-200 rounded-xl pl-10 pr-10 py-3 text-sm text-slate-800 focus:outline-none focus:ring-2 focus:ring-slate-900/10 focus:border-slate-400 transition"
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

            {/* Submit Button */}
            <button
              type="submit"
              disabled={isLoading}
              className="w-full mt-2 bg-[#0F172A] hover:bg-slate-800 text-white font-medium py-3 px-4 rounded-xl transition cursor-pointer flex items-center justify-center gap-2 active:scale-[0.99] shadow-sm disabled:opacity-50"
            >
              {isLoading ? (
                "Giriş Yapılıyor..."
              ) : (
                <>
                  <span>Giriş Yap</span>
                  <FiArrowRight className="text-lg" />
                </>
              )}
            </button>
          </form>

          {/* Kayıt Ol Yönlendirmesi */}
          <div className="mt-8 text-center text-xs text-slate-500">
            Hesabınız yok mu?{" "}
            <Link
              to="/register"
              className="font-bold text-slate-900 hover:underline"
            >
              Hemen Kayıt Olun
            </Link>
          </div>
        </div>

      </div>
    </div>
  );
}

export default Login;
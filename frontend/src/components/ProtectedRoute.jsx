import { Navigate, Outlet } from "react-router-dom";
import { getAccessToken, logout } from "../services/authenticationService";

function isTokenExpired(token) {
  try {
    // JWT token'ın payload bölümünü (2. kısım) decode eder
    const payloadBase64 = token.split(".")[1];
    const decodedJson = atob(payloadBase64);
    const decoded = JSON.parse(decodedJson);
    
    // exp zaman damgası (saniye) ile şu anki zamanı kıyaslar
    return decoded.exp * 1000 < Date.now();
  } catch (e) {
    return true; // Parse edilemiyorsa geçersiz/süresi dolmuş kabul et
  }
}

function ProtectedRoute() {
  const token = getAccessToken();

  const isInvalidToken = 
    !token || 
    token === "null" || 
    token === "undefined" || 
    token.trim() === "";

  // Token yoksa veya süresi dolmuşsa oturumu temizleyip login'e atar
  if (isInvalidToken || isTokenExpired(token)) {
    if (token) logout(); // Süresi dolmuş token'ı temizle
    return <Navigate to="/login" replace />;
  }

  return <Outlet />;
}

export default ProtectedRoute;
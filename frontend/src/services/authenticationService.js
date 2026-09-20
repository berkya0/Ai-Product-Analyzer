const API_BASE_URL = "http://localhost:8080/api/auth";

/**
 * Kullanıcı Girişi (POST /api/auth/login)
 */
export async function login(credentials) {
  const response = await fetch(`${API_BASE_URL}/login`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(credentials),
  });

  const data = await response.json();

  if (!response.ok) {
    throw new Error(data.message || "Kullanıcı adı veya şifre hatalı.");
  }

  if (data.accessToken) {
    localStorage.setItem("accessToken", data.accessToken);
  }
  if (data.refreshToken) {
    localStorage.setItem("refreshToken", data.refreshToken);
  }

  return data;
}

/**
 * Kullanıcı Kaydı (POST /api/auth/register)
 */
export async function register(userData) {
  const response = await fetch(`${API_BASE_URL}/register`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(userData),
  });

  const data = await response.json();

  if (!response.ok) {
    throw new Error(data.message || "Kayıt işlemi başarısız oldu.");
  }

  if (data.accessToken) {
    localStorage.setItem("accessToken", data.accessToken);
  }
  if (data.refreshToken) {
    localStorage.setItem("refreshToken", data.refreshToken);
  }

  return data;
}

/**
 * Access Token Yenileme (POST /api/auth/refresh)
 */
export async function refreshToken() {
  const storedRefreshToken = localStorage.getItem("refreshToken");

  if (!storedRefreshToken) {
    throw new Error("Yenileme jetonu bulunamadı.");
  }

  const response = await fetch(`${API_BASE_URL}/refresh`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ refreshToken: storedRefreshToken }),
  });

  const data = await response.json();

  if (!response.ok) {
    logout();
    throw new Error(data.message || "Oturum süreniz doldu, tekrar giriş yapın.");
  }

  if (data.accessToken) {
    localStorage.setItem("accessToken", data.accessToken);
  }

  return data;
}

/**
 * Oturumu Kapatma (POST /api/auth/logout)
 */
export async function logout() {
  const storedRefreshToken = localStorage.getItem("refreshToken");

  try {
    if (storedRefreshToken) {
      await fetch(`${API_BASE_URL}/logout`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ refreshToken: storedRefreshToken }),
      });
    }
  } catch (error) {
    console.error("Logout API hatası:", error);
  } finally {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
  }
}

/**
 * Mevcut Access Token'ı Getir
 */
export function getAccessToken() {
  return localStorage.getItem("accessToken");
}
import { getAccessToken, refreshToken, logout } from "./authenticationService";

const BASE_URL = "http://localhost:8080/api";

export async function fetchWithAuth(endpoint, options = {}, responseType = "json") {
  let token = getAccessToken();

  const headers = {
    "Content-Type": "application/json",
    ...(options.headers || {}),
  };

  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }

  let response = await fetch(`${BASE_URL}${endpoint}`, {
    ...options,
    headers,
  });

  if (response.status === 401) {
    try {
      const refreshData = await refreshToken();
      headers["Authorization"] = `Bearer ${refreshData.accessToken}`;

      response = await fetch(`${BASE_URL}${endpoint}`, {
        ...options,
        headers,
      });
    } catch (error) {
      logout();
      window.location.href = "/login";
      throw new Error("Oturum süreniz doldu, lütfen tekrar giriş yapın.");
    }
  }

  if (!response.ok) {
    const errorText = await response.text();
    let errorMessage = "İşlem gerçekleştirilemedi.";
    try {
      const errorJson = JSON.parse(errorText);
      errorMessage = errorJson.message || errorMessage;
    } catch {
      if (errorText) errorMessage = errorText;
    }
    throw new Error(errorMessage);
  }

  const responseText = await response.text();
  if (!responseText) return null;

  if (responseType === "text") {
    return responseText;
  }

  try {
    return JSON.parse(responseText);
  } catch (e) {
    return responseText;
  }
}
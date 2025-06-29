import { getToken, clearToken, clearUser } from "./authService";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "";

export async function apiFetch(path, options = {}) {
  const token = getToken();

  const isFormData = options.body instanceof FormData;

  options.headers = {
    ...(options.headers || {}),
    ...(token ? { Authorization: "Bearer " + token } : {}),
    // ✅ Nếu body không phải FormData thì mới gán Content-Type là JSON
    ...(!isFormData ? { "Content-Type": "application/json" } : {}),
  };

  const url =
    path.startsWith("http://") || path.startsWith("https://")
      ? path
      : API_BASE_URL + path;

  const response = await fetch(url, options);

  if (response.status === 401) {
    clearToken();
    clearUser();
    alert("Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại!");
    window.location.reload();
    throw new Error("Token expired");
  }

  if (!response.ok) {
    let errObj = {};
    try {
      errObj = await response.json();
    } catch (e) {
      try {
        const text = await response.text();
        errObj = { error: text || `HTTP ${response.status}` };
      } catch {
        errObj = { error: `HTTP ${response.status}` };
      }
    }
    throw errObj.error || "Unknown error";
  }

  return response;
}

import { getToken, clearToken, clearUser } from "./authService";

// Lấy biến env từ Vite (.env file)
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "";

export async function apiFetch(path, options = {}) {
  const token = getToken();
  options.headers = {
    ...(options.headers || {}),
    ...(token ? { Authorization: "Bearer " + token } : {}),
  };

  const url =
    path.startsWith("http://") || path.startsWith("https://")
      ? path
      : API_BASE_URL + path;

  const response = await fetch(url, options);

  // Xử lý hết hạn token
  if (response.status === 401) {
    clearToken();
    clearUser();
    alert("Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại!");
    window.location.reload();
    throw new Error("Token expired");
  }

  // ✅ Bổ sung xử lý lỗi chung ở đây:
  if (!response.ok) {
    let errObj = {};
    try {
      errObj = await response.json();
    } catch (e) {
      // Nếu không phải JSON thì lấy text
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

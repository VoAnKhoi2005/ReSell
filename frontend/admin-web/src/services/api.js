import { getToken, clearToken, clearUser } from "./authService";

// Lấy biến env từ Vite (.env file)
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "";

// Wrapper fetch: auto gắn token, tự ghép base URL, handle 401
export async function apiFetch(path, options = {}) {
  const token = getToken();
  console.log("API_BASE_URL", API_BASE_URL);
  options.headers = {
    ...(options.headers || {}),
    ...(token ? { Authorization: "Bearer " + token } : {}),
  };

  // Nếu path đã là absolute (http/https) thì dùng luôn, còn lại thì ghép với API_BASE_URL
  const url =
    path.startsWith("http://") || path.startsWith("https://")
      ? path
      : API_BASE_URL + path;

  const response = await fetch(url, options);

  // Nếu bị 401/hết hạn token
  if (response.status === 401) {
    clearToken();
    clearUser();
    alert("Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại!");
    window.location.reload();
    throw new Error("Token expired");
  }

  return response;
}

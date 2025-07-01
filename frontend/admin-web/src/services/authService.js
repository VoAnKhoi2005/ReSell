const TOKEN_KEY = "access_token";
const REFRESH_KEY = "refresh_token";
const USER_KEY = "admin_user";

export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token);
}
export function getToken() {
  return localStorage.getItem(TOKEN_KEY);
}
export function clearToken() {
  localStorage.removeItem(TOKEN_KEY);
}

export function setRefreshToken(refreshToken) {
  localStorage.setItem(REFRESH_KEY, refreshToken);
}
export function getRefreshToken() {
  return localStorage.getItem(REFRESH_KEY);
}
export function clearRefreshToken() {
  localStorage.removeItem(REFRESH_KEY);
}

export function setUser(user) {
  localStorage.setItem(USER_KEY, JSON.stringify(user));
}
export function getUser() {
  try {
    return JSON.parse(localStorage.getItem(USER_KEY));
  } catch {
    return null;
  }
}
export function clearUser() {
  localStorage.removeItem(USER_KEY);
}

export function isLoggedIn() {
  return !!getToken();
}

// 🔥 Hàm decode JWT để lấy thời gian hết hạn
function decodeToken(token) {
  try {
    const payload = token.split('.')[1];
    const decoded = atob(payload);
    return JSON.parse(decoded);
  } catch {
    return null;
  }
}

// ⏳ Kiểm tra token có hết hạn chưa
function isTokenExpired(token) {
  const decoded = decodeToken(token);
  if (!decoded || !decoded.exp) return true;
  const now = Math.floor(Date.now() / 1000); // Giây
  return decoded.exp < now;
}

// ⚙️ Hàm chính: trả về access token còn hạn, hoặc tự refresh nếu hết hạn
export async function getValidAccessToken() {
  const token = getToken();
  if (token && !isTokenExpired(token)) {
    return token;
  }

  // Token hết hạn → thử refresh
  const refreshToken = getRefreshToken();
  if (!refreshToken) {
    logout(); // Không còn refresh token → logout
    return null;
  }

  try {
    const res = await fetch("/api/admin/refresh", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ refresh_token: refreshToken }),
    });

    if (!res.ok) throw new Error("Refresh failed");

    const data = await res.json();
    setToken(data.access_token); // Lưu lại token mới
    return data.access_token;
  } catch (err) {
    console.error("Refresh token error:", err);
    logout(); // Refresh thất bại → logout
    return null;
  }
}

export function logout() {
  clearToken();
  clearRefreshToken();
  clearUser();
  window.location.reload();
}

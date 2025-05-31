const TOKEN_KEY = "access_token";
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

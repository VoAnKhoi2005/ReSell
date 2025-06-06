import { apiFetch } from "./api";

const BASE_PATH = "/api/posts";

// Lấy danh sách bài đăng (phân trang + filter)
export async function fetchPosts(query = {}) {
  const queryString = new URLSearchParams(
    Object.entries(query).filter(([_, v]) => v !== undefined)
  ).toString();

  const res = await apiFetch(`/api/posts?${queryString}`);
  return res.json();
}

export async function approvePost(id) {
  return apiFetch(`/api/admin/posts/${id}/approve`, { method: "PUT" });
}

export async function rejectPost(id) {
  return apiFetch(`/api/admin/posts/${id}/reject`, { method: "PUT" });
}

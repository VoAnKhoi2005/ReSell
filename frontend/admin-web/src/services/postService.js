import { apiFetch } from "./api";

// Lấy danh sách bài đăng (phân trang + filter)
export async function fetchPosts(query = {}) {
  const queryString = new URLSearchParams(
    Object.entries(query).filter(([_, v]) => v !== undefined)
  ).toString();

  const res = await apiFetch(`/api/admin/posts?${queryString}`);
  return res.json();
}

export async function fetchPostById(id) {
  const res = await apiFetch(`/api/posts/${id}`);
  return res.json();
}

export async function approvePost(id) {
  return apiFetch(`/api/admin/posts/${id}/approve`, { method: "PUT" });
}

export async function rejectPost(id) {
  return apiFetch(`/api/admin/posts/${id}/reject`, { method: "PUT" });
}

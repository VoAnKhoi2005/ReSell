import { apiFetch } from "./api";

export async function fetchAllCategories() {
  const res = await apiFetch("/api/categories");
  return await res.json();
}

// Truyền vào FormData thay vì string
export async function createCategory(formData) {
  const res = await apiFetch("/api/admin/categories", {
    method: "POST",
    body: formData, // Không cần headers
  });
  return await res.json();
}

// Truyền vào FormData thay vì string
export async function updateCategory(id, formData) {
  const res = await apiFetch(`/api/admin/categories/${id}`, {
    method: "PUT",
    body: formData, // Không cần headers
  });
  return await res.json();
}

export async function deleteCategory(id) {
  const res = await apiFetch(`/api/admin/categories/${id}`, {
    method: "DELETE",
  });
  return await res.json();
}

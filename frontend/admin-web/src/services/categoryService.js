import { apiFetch } from "./api";

export async function fetchAllCategories() {
  const res = await apiFetch("/api/categories");
  return await res.json();
}

export async function createCategory(name, parentId = null) {
  const res = await apiFetch("/api/admin/categories", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ name, parent_category_id: parentId }),
  });
  return await res.json(); // { id: ..., message: "Category created" }
}

export async function updateCategory(id, name) {
  const res = await apiFetch(`/api/admin/categories/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ name }),
  });
  return await res.json();
}

export async function deleteCategory(id) {
  const res = await apiFetch(`/api/admin/categories/${id}`, {
    method: "DELETE",
  });
  return await res.json();
}

import { apiFetch } from "./api";

// Lấy danh sách phương thức thanh toán
export async function fetchPayments() {
  const res = await apiFetch("/api/payment-methods");
  return res.json();
}

// Tạo mới phương thức
export async function createPayment(name) {
  const res = await apiFetch("/api/admin/payment-methods", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ name }),
  });
  return res.json();
}

// Cập nhật tên phương thức
export async function updatePayment(id, name) {
  const res = await apiFetch(`/api/admin/payment-methods/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ name }),
  });
  return res.json();
}

// Xoá phương thức thanh toán
export async function deletePayment(id) {
  const res = await apiFetch(`/api/admin/payment-methods/${id}`, {
    method: "DELETE",
  });
  return res.json();
}

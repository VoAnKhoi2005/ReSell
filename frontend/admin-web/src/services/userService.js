import { apiFetch } from "./api";

// Lấy danh sách user theo phân trang (batch)
// pageSize: số lượng mỗi trang, page: số trang (bắt đầu từ 1)
export async function fetchUsers(pageSize = 100, page = 1) {
  const res = await apiFetch(`/api/admin/user/batch/${pageSize}/${page}`);
  if (!res.ok) throw await res.text();
  // Kết quả có dạng: { total_batch_count, users: [...] }
  return await res.json();
}

// Ban user (có thể truyền mảng user_id, nhưng FE chỉ dùng 1 user mỗi lần)
export async function banUser(userId) {
  const res = await apiFetch("/api/admin/user/ban", {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      ban_user_id: userId,
      length: 1, // length phải là 1 như backend yêu cầu
    }),
  });
  if (!res.ok) throw await res.text();
  return await res.json(); // { success: true }
}

// Unban user
export async function unbanUser(userId) {
  const res = await apiFetch(`/api/admin/user/unban/${userId}`, {
    method: "PUT",
  });
  if (!res.ok) throw await res.text();
  return await res.json(); // { success: true }
}

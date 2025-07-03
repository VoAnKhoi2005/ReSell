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
export async function banUser(userId, reason, length) {
  const res = await apiFetch("/api/admin/user/ban", {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      ban_user_id: userId,
      ban_reason: reason,
      length: length,
    }),
  });
  if (!res.ok) throw await res.text();
  return await res.json();
}

// Unban user
export async function unbanUser(userId) {
  const res = await apiFetch(`/api/admin/user/unban/${userId}`, {
    method: "PUT",
  });
  if (!res.ok) throw await res.text();
  return await res.json(); // { success: true }
}

export async function fetchUserStat(userId) {
  const res = await apiFetch(`/api/user/stat/${userId}`);
  if (!res.ok) throw await res.text();
  return await res.json();
}

export async function updateReputation(userId, newRepu) {
  const res = await apiFetch(
    `/api/admin/user/reputation/${userId}/${newRepu}`,
    {
      method: "PUT",
    }
  );
  if (!res.ok) throw await res.text();
  return await res.json();
}

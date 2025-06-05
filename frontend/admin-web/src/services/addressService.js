import { apiFetch } from "./api";

// Lấy tất cả tỉnh
export async function fetchProvinces() {
  const res = await apiFetch("/api/address/provinces/all");
  const data = await res.json();
  // Nếu API trả về object, lấy ra mảng:
  return Array.isArray(data) ? data : data.provinces;
}

// Lấy danh sách quận/huyện theo tỉnh
export async function fetchDistricts(provinceId) {
  const res = await apiFetch(`/api/address/districts/${provinceId}`);
  const data = await res.json();
  // Nếu API trả về object, lấy ra mảng:
  return Array.isArray(data) ? data : data.districts;
}

// Lấy danh sách phường/xã theo quận/huyện
export async function fetchWards(districtId) {
  const res = await apiFetch(`/api/address/wards/${districtId}`);
  const data = await res.json();
  // Nếu API trả về object, lấy ra mảng:
  return Array.isArray(data) ? data : data.wards;
}

export async function createProvince(name) {
  const res = await apiFetch("/api/admin/address/provinces", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify([name]),
  });
  if (!res.ok) throw await res.text();
  return await res.json();
}

export async function updateProvince(id, name) {
  const res = await apiFetch(`/api/admin/address/province/${id}/${name}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
  });
  if (!res.ok) throw await res.text();
  return await res.json();
}

export async function deleteProvince(id) {
  const res = await apiFetch(`/api/admin/address/province/${id}`, {
    method: "DELETE",
  });
  if (!res.ok) throw await res.text();
  return res;
}

// Lấy danh sách quận/huyện theo tỉnh (đã có ở trên)

// Tạo quận/huyện mới (POST mảng object)
export async function createDistrict(name, provinceId) {
  const res = await apiFetch("/api/admin/address/districts", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify([{ name, province_id: provinceId }]),
  });
  if (!res.ok) throw await res.text();
  return await res.json();
}

// Sửa tên quận/huyện (PUT, y chang province)
export async function updateDistrict(id, name) {
  const res = await apiFetch(`/api/admin/address/district/${id}/${name}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
  });
  if (!res.ok) throw await res.text();
  return await res.json();
}

// Xóa quận/huyện
export async function deleteDistrict(id) {
  const res = await apiFetch(`/api/admin/address/district/${id}`, {
    method: "DELETE",
  });
  if (!res.ok) throw await res.text();
  return res;
}

// Lấy danh sách phường/xã theo quận/huyện (đã có ở trên)

// Tạo phường/xã mới (POST mảng object)
export async function createWard(name, districtId) {
  const res = await apiFetch("/api/admin/address/wards", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify([{ name, district_id: districtId }]),
  });
  if (!res.ok) throw await res.text();
  return await res.json();
}

// Sửa tên phường/xã (PUT, y chang district)
export async function updateWard(id, name) {
  const res = await apiFetch(`/api/admin/address/ward/${id}/${name}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
  });
  if (!res.ok) throw await res.text();
  return await res.json();
}

// Xóa phường/xã
export async function deleteWard(id) {
  const res = await apiFetch(`/api/admin/address/ward/${id}`, {
    method: "DELETE",
  });
  if (!res.ok) throw await res.text();
  return res;
}

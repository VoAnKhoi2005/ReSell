import { apiFetch } from "./api";

// Lấy tất cả tỉnh
export async function fetchProvinces() {
  const res = await apiFetch("/api/provinces");
  return await res.json();
}

// Lấy danh sách quận/huyện theo tỉnh
export async function fetchDistricts(provinceId) {
  const res = await apiFetch(`/api/districts/${provinceId}`);
  return await res.json();
}

// Lấy danh sách phường/xã theo quận/huyện
export async function fetchWards(districtId) {
  const res = await apiFetch(`/api/wards/${districtId}`);
  return await res.json();
}

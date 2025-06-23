// src/services/reportService.js

import { apiFetch } from "./api";

// Báo cáo người dùng
export async function fetchReportUsers(limit = 10, page = 1) {
  const res = await apiFetch(
    `/api/admin/report/users?limit=${limit}&page=${page}`
  );
  return await res.json(); // { data: [...], limit, page, total }
}

// Báo cáo bài đăng
export async function fetchReportPosts(limit = 10, page = 1) {
  const res = await apiFetch(
    `/api/admin/report/posts?limit=${limit}&page=${page}`
  );
  return await res.json(); // { data: [...], limit, page, total }
}

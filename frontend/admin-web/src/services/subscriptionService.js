import { apiFetch } from "./api";

export async function fetchPlans() {
  const res = await apiFetch("/api/subscriptions");
  return res.json();
}

export async function createPlan(data) {
  const res = await apiFetch("/api/admin/subscriptions", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
  return res.json();
}

export async function updatePlan(id, data) {
  const res = await apiFetch(`/api/admin/subscriptions/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
  return res.json();
}

export async function deletePlan(id) {
  const res = await apiFetch(`/api/admin/subscriptions/${id}`, {
    method: "DELETE",
  });
  return res.json();
}

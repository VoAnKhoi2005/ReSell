<script>
  import { onMount } from "svelte";
  import {
    fetchPlans,
    createPlan,
    updatePlan,
    deletePlan,
  } from "../services/subscriptionService";

  let plans = [];
  let name = "";
  let description = "";
  let duration = 30;
  let stripePriceID = "";
  let editingId = null;

  async function loadPlans() {
    plans = await fetchPlans();
  }

  async function handleSubmit() {
    if (!name.trim() || !stripePriceID.trim()) return;

    const data = {
      name,
      description,
      duration: duration,
      stripe_price_id: stripePriceID,
    };

    if (editingId) {
      await updatePlan(editingId, data);
    } else {
      await createPlan(data);
    }

    name = "";
    description = "";
    duration = 30;
    stripePriceID = "";
    editingId = null;

    await loadPlans();
  }

  function handleEdit(p) {
    name = p.name;
    description = p.description;
    duration = p.duration;
    stripePriceID = p.stripe_price_id;
    editingId = p.id;
  }

  async function handleDelete(id) {
    if (confirm("Xoá gói đăng ký này?")) {
      await deletePlan(id);
      await loadPlans();
    }
  }

  onMount(loadPlans);
</script>

<div class="w-100">
  <h3>Quản lý gói đăng ký</h3>

  <div class="my-3">
    <div class="mb-2"><b>Thông tin gói:</b></div>
    <input class="form-control mb-2" placeholder="Tên gói" bind:value={name} />
    <textarea class="form-control mb-2" placeholder="Mô tả" bind:value={description} />
    <input type="number" class="form-control mb-2" placeholder="Số ngày hiệu lực" bind:value={duration} />
    <input class="form-control mb-2" placeholder="Stripe Price ID" bind:value={stripePriceID} />
    <div class="d-flex gap-2">
      <button class="btn btn-primary" on:click={handleSubmit}>
        {editingId ? "Cập nhật" : "Thêm"}
      </button>
      {#if editingId}
        <button class="btn btn-secondary" on:click={() => {
          name = ""; description = ""; duration = 30; stripePriceID = ""; editingId = null;
        }}>Hủy</button>
      {/if}
    </div>
  </div>

  <table class="table table-hover">
    <thead>
      <tr>
        <th>Tên</th>
        <th>Mô tả</th>
        <th>Thời hạn (ngày)</th>
        <th>Stripe Price ID</th>
        <th style="width:100px">Hành động</th>
      </tr>
    </thead>
    <tbody>
      {#each plans as p (p.id)}
        <tr>
          <td>{p.name}</td>
          <td>{p.description}</td>
          <td>{p.duration}</td>
          <td>{p.stripe_price_id}</td>
          <td>
            <button class="btn btn-sm btn-outline-primary me-1" on:click={() => handleEdit(p)}>Sửa</button>
            <button class="btn btn-sm btn-outline-danger" on:click={() => handleDelete(p.id)}>Xoá</button>
          </td>
        </tr>
      {/each}
      {#if plans.length === 0}
        <tr>
          <td colspan="5" class="text-center text-muted">Chưa có gói nào</td>
        </tr>
      {/if}
    </tbody>
  </table>
</div>

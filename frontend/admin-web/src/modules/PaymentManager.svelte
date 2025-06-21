<script>
    import { onMount } from "svelte";
    import {
      fetchPayments,
      createPayment,
      updatePayment,
      deletePayment,
    } from "../services/paymentService";
  
    let payments = [];
    let name = "";
    let editingId = null;
  
    async function loadPayments() {
      payments = await fetchPayments();
    }
  
    async function handleSubmit() {
      if (name.trim() === "") return;
  
      if (editingId) {
        await updatePayment(editingId, name);
      } else {
        await createPayment(name);
      }
  
      name = "";
      editingId = null;
      await loadPayments();
    }
  
    function handleEdit(payment) {
      name = payment.name;
      editingId = payment.id;
    }
  
    async function handleDelete(id) {
      if (confirm("Xoá phương thức thanh toán này?")) {
        await deletePayment(id);
        await loadPayments();
      }
    }
  
    onMount(loadPayments);
  </script>
  
  <div class="w-100">
    <h3>Quản lý phương thức thanh toán</h3>
  
    <div class="d-flex gap-2 my-3">
      <input
        class="form-control"
        placeholder="Tên phương thức thanh toán"
        bind:value={name}
      />
      <button class="btn btn-primary" on:click={handleSubmit}>
        {editingId ? "Cập nhật" : "Thêm"}
      </button>
      {#if editingId}
        <button class="btn btn-secondary" on:click={() => { name = ""; editingId = null }}>
          Hủy
        </button>
      {/if}
    </div>
  
    <table class="table table-hover">
      <thead>
        <tr>
          <th>Tên</th>
          <th style="width: 100px;">Hành động</th>
        </tr>
      </thead>
      <tbody>
        {#each payments as p (p.id)}
          <tr>
            <td>{p.name}</td>
            <td>
              <button class="btn btn-sm btn-outline-primary me-1" on:click={() => handleEdit(p)}>Sửa</button>
              <button class="btn btn-sm btn-outline-danger" on:click={() => handleDelete(p.id)}>Xoá</button>
            </td>
          </tr>
        {/each}
        {#if payments.length === 0}
          <tr>
            <td colspan="2" class="text-center text-muted">Chưa có phương thức nào</td>
          </tr>
        {/if}
      </tbody>
    </table>
  </div>
  
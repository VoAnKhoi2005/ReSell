<script>
  import { onMount } from "svelte";
  import { fetchUserStat, updateReputation } from "../services/userService.js";
  import { createEventDispatcher } from "svelte";
  const dispatch = createEventDispatcher();


  export let user;
  export let onClose; // Expect onClose(shouldRefresh = true|false)

  let reputationInput = user.reputation;
  let stat = null;
  let loading = true;
  let error = null;
  let updated = false;

  onMount(async () => {
    try {
      stat = await fetchUserStat(user.id);
    } catch (e) {
      error = "Lỗi khi tải thống kê.";
    } finally {
      loading = false;
    }
  });

 
  async function changeReputation(newValue) {
  try {
    await updateReputation(user.id, newValue);
    reputationInput = newValue;
    user.reputation = newValue;
    updated = true; // ✅ báo hiệu đã cập nhật
    dispatch("updated"); // tùy, nếu cha xài thì xài event này
    alert("Cập nhật điểm uy tín thành công");
  } catch (err) {
    alert("Không thể cập nhật điểm uy tín");
  }
}


  function handleClose() {
    onClose(updated); // truyền true nếu đã cập nhật, false nếu không
  }
</script>

<div class="modal fade show" style="display:block; background:rgba(0,0,0,0.25);" tabindex="-1">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Thông tin người dùng</h5>
        <button type="button" class="btn-close" on:click={handleClose}></button>
      </div>
      <div class="modal-body">
        <div><b>Username:</b> {user.username}</div>

        <div class="mt-3">
          <b>Điểm uy tín:</b>
          <div class="input-group mt-1">
            <input
              type="number"
              class="form-control form-control-sm"
              bind:value={reputationInput}
              min="0"
            />
            <button
              class="btn btn-sm btn-outline-success"
              on:click={() => changeReputation(reputationInput)}
            >
              Cập nhật
            </button>
          </div>
        </div>

        <hr />

        {#if loading}
          <div class="text-muted">Đang tải thống kê...</div>
        {:else if error}
          <div class="text-danger">{error}</div>
        {:else if stat}
          <div><b>Tổng số bài đăng:</b> {stat.post_number}</div>
          <div><b>Số đơn đã mua:</b> {stat.bought_number}</div>
          <div><b>Số đơn đã bán:</b> {stat.selled_number}</div>
          <div><b>Tổng doanh thu:</b> {stat.revenue} đ</div>
          <div><b>Lượt bị báo cáo:</b> {stat.report_number}</div>
        {/if}
      </div>
      <div class="modal-footer">
        <button class="btn btn-secondary btn-sm" on:click={handleClose}>
          Đóng
        </button>
      </div>
    </div>
  </div>
</div>
<div class="modal-backdrop fade show"></div>

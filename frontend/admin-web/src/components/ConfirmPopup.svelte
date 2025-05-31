<script>
  import { confirmStore } from "../stores/confirmStore.js";
  let show = false;
  let message = "";

  // Auto subscribe store
  $: {
    const unsubscribe = confirmStore.subscribe(($state) => {
      show = $state.show;
      message = $state.message;
    });
  }
</script>

{#if show}
  <div class="popup-backdrop">
    <div class="popup-card">
      <h5>Xác nhận</h5>
      <div class="mb-3">{message}</div>
      <div class="text-end">
        <button class="btn btn-secondary me-2" on:click={confirmStore.cancel}
          >Huỷ</button
        >
        <button class="btn btn-danger" on:click={confirmStore.ok}>OK</button>
      </div>
    </div>
  </div>
{/if}

<style>
  .popup-backdrop {
    position: fixed;
    left: 0;
    top: 0;
    width: 100vw;
    height: 100vh;
    background: rgba(0, 0, 0, 0.2);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 9999;
  }
  .popup-card {
    background: #fff;
    padding: 24px 20px 18px;
    border-radius: 14px;
    box-shadow: 0 2px 16px rgba(0, 0, 0, 0.12);
    min-width: 300px;
  }
</style>

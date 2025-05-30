<script>
  import Login from "./pages/Login.svelte";
  import AppLayout from "./layouts/AppLayout.svelte";

  let isLoggedIn = true; // Đặt true để test luôn giao diện admin

  function handleLoginSuccess() {
    isLoggedIn = true;
  }
</script>

{#if !isLoggedIn}
  <Login on:loginSuccess={handleLoginSuccess} />
{:else}
  <div class="admin-root w-100 h-100" style="min-height:100vh;">
    <AppLayout />
  </div>
{/if}

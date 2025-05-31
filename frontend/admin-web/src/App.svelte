<script>
  import {
    isLoggedIn,
    clearToken,
    clearUser,
    getUser,
  } from "./services/authService.js";
  import Login from "./pages/Login.svelte";
  import AppLayout from "./layouts/AppLayout.svelte";

  let loggedIn = isLoggedIn();

  // Nếu cần: khi token hết hạn và reload, sẽ tự vào login luôn do isLoggedIn = false
</script>

{#if loggedIn}
  <AppLayout />
{:else}
  <Login />
{/if}

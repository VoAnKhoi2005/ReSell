<script>
  import { getUser, clearToken, clearUser } from "../services/authService.js";
  import { clickOutside } from "../utils/clickOutside.js";

  // Lấy tên admin từ localStorage
  let adminName = getUser()?.username || "Admin";
  let showDropdown = false;

  function logout() {
    clearToken();
    clearUser();
    window.location.reload();
  }
</script>

<nav
  class="navbar navbar-light bg-white border-bottom px-4"
  style="height:56px;"
>
  <div class="flex-grow-1"></div>
  <div class="dropdown ms-auto" style="position:relative;">
    <button
      class="btn btn-light border shadow-sm"
      type="button"
      on:click={() => (showDropdown = !showDropdown)}
    >
      {adminName}
    </button>
    {#if showDropdown}
      <ul
        class="dropdown-menu show"
        style="position:absolute;right:0;top:56px;z-index:999;"
        use:clickOutside={() => (showDropdown = false)}
      >
        <li>
          <a class="dropdown-item text-danger" href="#" on:click={logout}
            >Đăng xuất</a
          >
        </li>
      </ul>
    {/if}
  </div>
</nav>

<script>
  let adminName = "Admin";
  let showDropdown = false;

  function logout() {
    alert("Logout chưa code, chỉ là demo!");
  }
</script>

<nav class="navbar navbar-light bg-white border-bottom px-4" style="height:56px;">
  <div class="flex-grow-1"></div>
  <div class="dropdown ms-auto">
    <button class="btn btn-light border shadow-sm" type="button" on:click={() => showDropdown = !showDropdown}>
      {adminName}
    </button>
    {#if showDropdown}
      <ul class="dropdown-menu show" style="position:absolute;right:0;top:56px;">
        <li><a class="dropdown-item" on:click={logout}>Đăng xuất</a></li>
      </ul>
    {/if}
  </div>
</nav>

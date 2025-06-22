<script>
  import { createEventDispatcher } from "svelte";
  import { clickOutside } from "../utils/clickOutside.js";

  export let users = [];
  export let page = 1;
  export let limit = 10;
  export let total = 0;
  export let search = "";
  export let filter = "all";

  const dispatch = createEventDispatcher();
  let totalPages = 0;
$: totalPages = Math.ceil(total / limit);



  let showMenu = {};

  function toggleMenu(id) {
    showMenu[id] = !showMenu[id];
    showMenu = { ...showMenu };
  }

  function handleBan(user) {
    dispatch("toggleBan", { id: user.id });
    showMenu[user.id] = false;
    showMenu = { ...showMenu };
  }

  function goToPage(p) {
    if (p >= 1 && p <= totalPages && p !== page) {
      dispatch("changePage", { page: p });
    }
  }

  $: filteredUsers = users.filter((u) => {
    const matchesSearch =
      search.trim() === "" ||
      u.username?.toLowerCase().includes(search.trim().toLowerCase());
    const matchesFilter =
      filter === "all" ||
      (filter === "active" && u.status === "active") ||
      (filter === "banned" && u.status === "banned");
    return matchesSearch && matchesFilter;
  });
</script>

<table class="table table-hover align-middle">
  <thead>
    <tr>
      <th>Tên đăng nhập</th>
      <th>Email</th>
      <th>SĐT</th>
      <th>Họ tên</th>
      <th>Điểm uy tín</th>
      <th>Trạng thái</th>
      <th></th>
    </tr>
  </thead>
  <tbody>
    {#each filteredUsers as user (user.id)}
      <tr>
        <td>{user.username}</td>
        <td>{user.email}</td>
        <td>{user.phone}</td>
        <td>{user.full_name}</td>
        <td>{user.reputation}</td>
        <td>
          {#if user.status === "active"}
            <span class="badge bg-success">Hoạt động</span>
          {:else}
            <span class="badge bg-danger">Bị cấm</span>
          {/if}
        </td>
        <td style="width:48px; position:relative;">
          <div class="dropdown">
            <button
              class="btn btn-sm btn-light"
              style="width:32px;"
              on:click={() => toggleMenu(user.id)}
            >
              &#8942;
            </button>
            {#if showMenu[user.id]}
              <ul
                class="dropdown-menu show"
                use:clickOutside={() => {
                  showMenu[user.id] = false;
                  showMenu = { ...showMenu };
                }}
                style="position:absolute; right:0; z-index:999;"
              >
                {#if user.status === "active"}
                  <li>
                    <a
                      class="dropdown-item text-danger"
                      href="#"
                      on:click={() => handleBan(user)}>Ban</a>
                  </li>
                {:else}
                  <li>
                    <a
                      class="dropdown-item text-success"
                      href="#"
                      on:click={() => handleBan(user)}>Unban</a>
                  </li>
                {/if}
              </ul>
            {/if}
          </div>
        </td>
      </tr>
    {/each}

    {#if filteredUsers.length === 0}
      <tr>
        <td colspan="8" class="text-center text-muted">Không có người dùng nào</td>
      </tr>
    {/if}
  </tbody>
</table>

{#if totalPages > 1}
  <nav class="d-flex justify-content-center">
    <ul class="pagination pagination-sm">
      <li class="page-item {page === 1 ? 'disabled' : ''}">
        <button class="page-link" on:click={() => goToPage(page - 1)}>Trước</button>
      </li>
      {#each Array(totalPages) as _, i}
        <li class="page-item {page === i + 1 ? 'active' : ''}">
          <button class="page-link" on:click={() => goToPage(i + 1)}>{i + 1}</button>
        </li>
      {/each}
      <li class="page-item {page === totalPages ? 'disabled' : ''}">
        <button class="page-link" on:click={() => goToPage(page + 1)}>Sau</button>
      </li>
    </ul>
  </nav>
{/if}

<style>
  .dropdown-menu.show {
    display: block;
    min-width: 100px;
    font-size: 14px;
  }
</style>

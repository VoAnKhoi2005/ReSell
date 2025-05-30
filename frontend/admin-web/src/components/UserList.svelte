<script>
  import { createEventDispatcher } from "svelte";
  import { clickOutside } from "../utils/clickOutside.js";
  export let users = [];
  const dispatch = createEventDispatcher();

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
</script>

<table class="table table-hover align-middle">
  <thead>
    <tr>
      <th>Tên đăng nhập</th>
      <th>Email</th>
      <th>SĐT</th>
      <th>Họ tên</th>
      <th>CCCD</th>
      <th>Điểm uy tín</th>
      <th>Trạng thái</th>
      <th></th>
    </tr>
  </thead>
  <tbody>
    {#each users as user (user.id)}
      <tr>
        <td>{user.username}</td>
        <td>{user.email}</td>
        <td>{user.phone}</td>
        <td>{user.fullname}</td>
        <td>{user.cccd}</td>
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
                      on:click={() => handleBan(user)}>Ban</a
                    >
                  </li>
                {:else}
                  <li>
                    <a
                      class="dropdown-item text-success"
                      href="#"
                      on:click={() => handleBan(user)}>Unban</a
                    >
                  </li>
                {/if}
              </ul>
            {/if}
          </div>
        </td>
      </tr>
    {/each}
  </tbody>
</table>

<style>
  .dropdown-menu.show {
    display: block;
    min-width: 100px;
    font-size: 14px;
  }
</style>

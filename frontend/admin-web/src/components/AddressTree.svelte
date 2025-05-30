<script>
  import { clickOutside } from "../utils/clickOutside.js";
  import { createEventDispatcher } from "svelte";
  export let type = "province"; // "province" | "district" | "ward"
  export let items = [];
  export let selectedId = null;

  const dispatch = createEventDispatcher();

  let showMenu = {};
  let editingName = {};
  let editedName = {};

  function handleSelect(id) {
    dispatch("select", { id });
  }

  function toggleMenu(id) {
    showMenu[id] = !showMenu[id];
    showMenu = { ...showMenu };
  }
  function renameItem(item) {
    editingName[item.id] = true;
    editedName[item.id] = item.name;
    toggleMenu(item.id);
  }
  function confirmRename(item) {
    const name = (editedName[item.id] || "").trim();
    if (name && name !== item.name) {
      dispatch("rename", { id: item.id, name });
    }
    editingName[item.id] = false;
  }
  function cancelRename(item) {
    editingName[item.id] = false;
  }
  function deleteItem(item) {
    toggleMenu(item.id);
    dispatch("delete", { id: item.id });
  }
</script>

<ul class="list-group list-group-flush">
  {#each items as item (item.id)}
    <li
      class="list-group-item d-flex align-items-center position-relative"
      class:active={item.id === selectedId}
      style="cursor:pointer;"
      on:click={() => handleSelect(item.id)}
    >
      {#if editingName[item.id]}
        <input
          class="form-control form-control-sm flex-grow-1 me-2"
          bind:value={editedName[item.id]}
          on:keydown={(e) => {
            if (e.key === "Enter") confirmRename(item);
            if (e.key === "Escape") cancelRename(item);
          }}
          autofocus
          style="width:120px;"
        />
        <button
          class="btn btn-success btn-sm me-1"
          on:click={() => confirmRename(item)}>OK</button
        >
        <button
          class="btn btn-secondary btn-sm"
          on:click={() => cancelRename(item)}>Huỷ</button
        >
      {:else}
        <span class="flex-grow-1">{item.name}</span>
      {/if}
      <div class="dropdown ms-auto">
        <button
          class="btn btn-sm btn-light"
          style="width:32px;"
          on:click|stopPropagation={() => toggleMenu(item.id)}
        >
          &#8942;
        </button>
        {#if showMenu[item.id]}
          <ul
            class="dropdown-menu show"
            use:clickOutside={() => {
              showMenu[item.id] = false;
              showMenu = { ...showMenu };
            }}
            style="position: absolute; right: 0; z-index: 999;"
          >
            <li>
              <a
                class="dropdown-item"
                href="#"
                on:click|stopPropagation={() => renameItem(item)}>Đổi tên</a
              >
            </li>
            <li>
              <a
                class="dropdown-item text-danger"
                href="#"
                on:click|stopPropagation={() => deleteItem(item)}>Xóa</a
              >
            </li>
          </ul>
        {/if}
      </div>
    </li>
  {/each}
</ul>

<style>
  .list-group-item.active {
    background: #e9f3fd;
    color: #0d6efd;
    font-weight: 500;
  }
  .dropdown-menu.show {
    display: block;
    min-width: 120px;
    font-size: 14px;
  }
</style>

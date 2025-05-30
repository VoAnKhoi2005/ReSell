<script>
  import { clickOutside } from "../utils/clickOutside.js";
  import { createEventDispatcher } from "svelte";
  import CategoryTree from "./CategoryTree.svelte";

  export let categories = [];
  export let parentId = null;
  export let level = 0;

  const dispatch = createEventDispatcher();

  let expanded = {};
  let showMenu = {};

  // Thêm con
  let addingChild = {};
  let newChildName = {};

  // Đổi tên
  let editingName = {};
  let editedName = {};

  function toggleExpand(id) {
    expanded[id] = !expanded[id];
    expanded = { ...expanded };
  }
  function toggleMenu(id) {
    showMenu[id] = !showMenu[id];
    showMenu = { ...showMenu };
  }

  function renameCategory(category) {
    editingName[category.id] = true;
    editedName[category.id] = category.name;
    toggleMenu(category.id);
  }
  function confirmRename(category) {
    const name = (editedName[category.id] || "").trim();
    if (name && name !== category.name) {
      dispatch("renameCategory", { id: category.id, name });
    }
    editingName[category.id] = false;
  }
  function cancelRename(category) {
    editingName[category.id] = false;
  }

  function addChildCategory(category) {
    addingChild[category.id] = true;
    newChildName[category.id] = "";
    toggleMenu(category.id);
    if (!expanded[category.id]) toggleExpand(category.id);
  }
  function confirmAddChild(category) {
    const name = (newChildName[category.id] || "").trim();
    if (name === "") return;
    dispatch("addChildCategory", { parentId: category.id, name });
    addingChild[category.id] = false;
    newChildName[category.id] = "";
  }
  function cancelAddChild(category) {
    addingChild[category.id] = false;
    newChildName[category.id] = "";
    addingChild = { ...addingChild };
    newChildName = { ...newChildName };
  }

  function deleteCategory(category) {
    toggleMenu(category.id);
    const hasChild = categories.some(
      (c) => c.parent_category_id === category.id
    );
    let confirmMsg = "Bạn có chắc chắn muốn xóa danh mục này?";
    if (hasChild)
      confirmMsg +=
        "\nDanh mục này có danh mục con. Xóa sẽ xóa toàn bộ các danh mục con.";
    if (window.confirm(confirmMsg)) {
      dispatch("deleteCategory", { id: category.id });
    }
  }
</script>

{#each categories.filter((c) => c.parent_category_id === parentId) as category (category.id)}
  <div
    class="d-flex align-items-center border-bottom position-relative"
    style="padding-left: {level * 24}px; height:48px;"
  >
    {#if categories.some((c) => c.parent_category_id === category.id)}
      <span
        style="width:24px;cursor:pointer;"
        on:click={() => toggleExpand(category.id)}
      >
        {#if expanded[category.id]}▼{:else}▶{/if}
      </span>
    {:else}
      <span style="width:24px;display:inline-block;"></span>
    {/if}

    <!-- Nếu đang đổi tên thì hiện input -->
    {#if editingName[category.id]}
      <input
        class="form-control form-control-sm flex-grow-1 me-2"
        bind:value={editedName[category.id]}
        on:keydown={(e) => {
          if (e.key === "Enter") confirmRename(category);
          if (e.key === "Escape") cancelRename(category);
        }}
        autofocus
        style="width:180px;"
      />
      <button
        class="btn btn-success btn-sm me-1"
        on:click={() => confirmRename(category)}>OK</button
      >
      <button
        class="btn btn-secondary btn-sm"
        on:click={() => cancelRename(category)}>Huỷ</button
      >
    {:else}
      <span class="flex-grow-1">{category.name}</span>
    {/if}

    <!-- 3 chấm menu -->
    <div class="dropdown" style="margin-right:8px;">
      <button
        class="btn btn-sm btn-light"
        style="width:32px;"
        on:click={() => toggleMenu(category.id)}
      >
        &#8942;
      </button>
      {#if showMenu[category.id]}
        <ul
          class="dropdown-menu show"
          use:clickOutside={() => {
            showMenu[category.id] = false;
            showMenu = { ...showMenu };
          }}
          style="position: absolute; right: 0; z-index: 999;"
        >
          <li>
            <a
              class="dropdown-item"
              href="#"
              on:click={() => renameCategory(category)}>Đổi tên</a
            >
          </li>
          <li>
            <a
              class="dropdown-item"
              href="#"
              on:click={() => addChildCategory(category)}>Thêm danh mục con</a
            >
          </li>
          <li>
            <a
              class="dropdown-item text-danger"
              href="#"
              on:click={() => deleteCategory(category)}>Xóa</a
            >
          </li>
        </ul>
      {/if}
    </div>
  </div>

  <!-- Thêm con inline -->
  {#if addingChild[category.id]}
    <div
      class="d-flex align-items-center border-bottom"
      style="padding-left: {(level + 1) *
        24}px; height:48px; background: #f6fafd;"
    >
      <input
        class="form-control form-control-sm me-2"
        style="width:180px"
        placeholder="Tên danh mục con"
        bind:value={newChildName[category.id]}
        on:keydown={(e) => {
          if (e.key === "Enter") confirmAddChild(category);
        }}
        autofocus
      />
      <button
        class="btn btn-success btn-sm me-1"
        on:click={() => confirmAddChild(category)}>OK</button
      >
      <button
        class="btn btn-secondary btn-sm"
        on:click={() => cancelAddChild(category)}>Huỷ</button
      >
    </div>
  {/if}

  {#if expanded[category.id]}
    <CategoryTree
      {categories}
      parentId={category.id}
      level={level + 1}
      on:deleteCategory
      on:addChildCategory
      on:renameCategory
    />
  {/if}
{/each}

<style>
  .dropdown-menu.show {
    display: block;
    min-width: 160px;
    font-size: 14px;
  }
</style>

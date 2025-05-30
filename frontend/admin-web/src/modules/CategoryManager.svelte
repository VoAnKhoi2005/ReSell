<script>
  import CategoryTree from "../components/CategoryTree.svelte";

  // Mảng lưu toàn bộ danh mục
  let categories = [
    { id: "1", name: "Đồ điện tử", parent_category_id: null },
    { id: "2", name: "Điện thoại", parent_category_id: "1" },
    { id: "3", name: "Laptop", parent_category_id: "1" },
    { id: "4", name: "Xe cộ", parent_category_id: null },
    { id: "5", name: "Xe máy", parent_category_id: "4" },
  ];

  // Thêm danh mục gốc
  let addingCategory = false;
  let newCategoryName = "";

  function handleAddCategory() {
    if (newCategoryName.trim() === "") return;
    const newId = Date.now().toString() + Math.random();
    categories = [
      ...categories,
      { id: newId, name: newCategoryName, parent_category_id: null },
    ];
    newCategoryName = "";
    addingCategory = false;
  }

  // Handler: Xóa danh mục
  function handleDeleteCategory(e) {
    const id = e.detail.id;
    function removeWithChildren(id) {
      categories
        .filter((c) => c.parent_category_id === id)
        .forEach((c) => removeWithChildren(c.id));
      const idx = categories.findIndex((c) => c.id === id);
      if (idx > -1) categories.splice(idx, 1);
    }
    removeWithChildren(id);
    categories = [...categories];
  }

  // Handler: Thêm danh mục con
  function handleAddChildCategory(e) {
    const { parentId, name } = e.detail;
    const newId = Date.now().toString() + Math.random();
    categories = [
      ...categories,
      { id: newId, name, parent_category_id: parentId },
    ];
  }

  // Handler: Đổi tên danh mục
  function handleRenameCategory(e) {
    const { id, name } = e.detail;
    const idx = categories.findIndex((c) => c.id === id);
    if (idx > -1) {
      categories[idx].name = name;
      categories = [...categories];
    }
  }
</script>

<div class="w-100">
  <div class="d-flex justify-content-between align-items-center mb-3">
    <h3>Quản lý danh mục</h3>
    {#if !addingCategory}
      <button class="btn btn-primary" on:click={() => (addingCategory = true)}
        >+ Tạo danh mục</button
      >
    {:else}
      <div class="d-flex align-items-center">
        <input
          class="form-control form-control-sm"
          placeholder="Tên danh mục mới"
          bind:value={newCategoryName}
          style="width:180px; margin-right:8px;"
          on:keydown={(e) => {
            if (e.key === "Enter") handleAddCategory();
          }}
          autofocus
        />
        <button class="btn btn-success btn-sm me-1" on:click={handleAddCategory}
          >OK</button
        >
        <button
          class="btn btn-secondary btn-sm"
          on:click={() => {
            addingCategory = false;
            newCategoryName = "";
          }}>Huỷ</button
        >
      </div>
    {/if}
  </div>

  <div class="card">
    <div class="card-body p-0">
      <CategoryTree
        {categories}
        parentId={null}
        on:deleteCategory={handleDeleteCategory}
        on:addChildCategory={handleAddChildCategory}
        on:renameCategory={handleRenameCategory}
      />
    </div>
  </div>
</div>

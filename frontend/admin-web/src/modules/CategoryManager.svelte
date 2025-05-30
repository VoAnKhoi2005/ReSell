<script>
  import CategoryTree from "../components/CategoryTree.svelte";

  let categories = [
    { id: "1", name: "Đồ điện tử", parent_category_id: null },
    { id: "2", name: "Điện thoại", parent_category_id: "1" },
    { id: "3", name: "Laptop", parent_category_id: "1" },
    { id: "4", name: "Xe cộ", parent_category_id: null },
    { id: "5", name: "Xe máy", parent_category_id: "4" },
  ];

  let addingCategory = false;
  let newCategoryName = "";

  function handleAddCategory() {
    if (newCategoryName.trim() === "") return;
    // Tạo id mới (random đơn giản, thực tế dùng uuid)
    const newId = Date.now().toString();
    categories = [
      ...categories,
      {
        id: newId,
        name: newCategoryName,
        parent_category_id: null,
      },
    ];
    newCategoryName = "";
    addingCategory = false;
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
      <CategoryTree {categories} parentId={null} level={0} />
    </div>
  </div>
</div>

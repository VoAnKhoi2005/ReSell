<script>
  import { onMount } from "svelte";
  import CategoryTree from "../components/CategoryTree.svelte";
  import {
    fetchAllCategories,
    createCategory,
    updateCategory,
    deleteCategory,
  } from "../services/categoryService";

  let categories = [];
  let addingCategory = false;
  let newCategoryName = "";

  async function loadCategories() {
    try {
      categories = await fetchAllCategories();
      console.log(categories);
    } catch (err) {
      console.error("Lỗi khi load danh mục:", err);
      alert("Không thể tải danh mục!");
    }
  }

  onMount(loadCategories);

  // Xóa danh mục + reload lại danh sách từ server
  async function handleDeleteCategory(e) {
    const id = e.detail.id;
    try {
      const res = await deleteCategory(id); // deleteCategory trả về response hoặc throw lỗi

      // Nếu API trả về 200 hoặc 204 thì tiếp tục load lại categories
      await loadCategories();
    } catch (err) {
      // Nếu là response lỗi từ server
      let msg = "Lỗi khi xóa danh mục!";
      if (typeof err === "string" && err.includes("foreign key"))
        msg = "Không thể xóa vì đang có bài đăng thuộc danh mục này.";
      alert(msg);
      console.error(err);
    }
  }

  // Thêm danh mục con
  async function handleAddChildCategory(e) {
    const { parentId, name } = e.detail;
    try {
      await createCategory(name, parentId);
      await loadCategories();
    } catch (err) {
      alert("Lỗi khi tạo danh mục con!");
      console.error(err);
    }
  }

  // Đổi tên danh mục
  async function handleRenameCategory(e) {
    const { id, name } = e.detail;
    try {
      await updateCategory(id, name);
      await loadCategories();
    } catch (err) {
      alert("Lỗi khi đổi tên danh mục!");
      console.error(err);
    }
  }

  // Thêm danh mục gốc
  async function handleAddCategory() {
    if (newCategoryName.trim() === "") return;
    try {
      await createCategory(newCategoryName, null);
      newCategoryName = "";
      addingCategory = false;
      await loadCategories();
    } catch (err) {
      alert("Lỗi khi tạo danh mục mới!");
      console.error(err);
    }
  }
</script>

<div class="w-100">
  <div class="d-flex justify-content-between align-items-center mb-3">
    <h3>Quản lý danh mục</h3>
    {#if !addingCategory}
      <button class="btn btn-primary" on:click={() => (addingCategory = true)}>
        + Tạo danh mục
      </button>
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
        <button
          class="btn btn-success btn-sm me-1"
          on:click={handleAddCategory}
        >
          OK
        </button>
        <button
          class="btn btn-secondary btn-sm"
          on:click={() => {
            addingCategory = false;
            newCategoryName = "";
          }}
        >
          Huỷ
        </button>
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

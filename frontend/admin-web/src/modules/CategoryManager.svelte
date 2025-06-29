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
  let newCategoryImage = null;

  async function loadCategories() {
    try {
      categories = await fetchAllCategories();
    } catch (err) {
      console.error("Lỗi khi load danh mục:", err);
      alert("Không thể tải danh mục!");
    }
  }

  onMount(loadCategories);

  async function handleDeleteCategory(e) {
    const id = e.detail.id;
    try {
      await deleteCategory(id);
      await loadCategories();
    } catch (err) {
      let msg = "Lỗi khi xóa danh mục!";
      if (typeof err === "string" && err.includes("foreign key"))
        msg = "Không thể xóa vì đang có bài đăng thuộc danh mục này.";
      alert(msg);
      console.error(err);
    }
  }

  async function handleAddChildCategory(e) {
    try {
      await createCategory(e.detail.form);
      await loadCategories();
    } catch (err) {
      alert("Lỗi khi tạo danh mục con!");
      console.error(err);
    }
  }

  async function handleRenameCategory(e) {
  const { id, form } = e.detail; // ✅ nhận FormData
  try {
    await updateCategory(id, form); // ✅ truyền đúng kiểu
    await loadCategories();
  } catch (err) {
    alert("Lỗi khi đổi tên danh mục!");
    console.error(err);
  }
}


  async function handleAddCategory() {
    if (newCategoryName.trim() === "") return;

    const form = new FormData();
    form.append("name", newCategoryName);
    if (newCategoryImage) {
      form.append("image", newCategoryImage);
    }

    try {
      await createCategory(form);
      newCategoryName = "";
      newCategoryImage = null;
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
          class="form-control form-control-sm me-2"
          placeholder="Tên danh mục mới"
          bind:value={newCategoryName}
          style="width:180px"
          on:keydown={(e) => {
            if (e.key === "Enter") handleAddCategory();
          }}
          autofocus
        />
        <input
          type="file"
          accept="image/*"
          class="form-control form-control-sm me-2"
          on:change={(e) => {
          const input = /** @type {HTMLInputElement} */ (e.target);
            if (input.files && input.files.length > 0) {
              newCategoryImage = input.files[0];
            }
          }}
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
            newCategoryImage = null;
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

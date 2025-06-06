<script>
  import { createEventDispatcher } from "svelte";
  export let showPosts = [];
  export let page = 1;
  export let total = 0;
  export let limit = 10;

  const dispatch = createEventDispatcher();

  const totalPages = Math.ceil(total / limit);

  function view(post) {
    dispatch("viewDetail", { post });
  }

  function goToPage(p) {
    if (p >= 1 && p <= totalPages && p !== page) {
      dispatch("changePage", { page: p });
    }
  }
</script>

<table class="table table-hover align-middle">
  <thead>
    <tr>
      <th>Tiêu đề</th>
      <th>Danh mục</th>
      <th>Người đăng</th>
      <th>Trạng thái</th>
      <th></th>
    </tr>
  </thead>
  <tbody>
    {#each showPosts as post (post.id)}
      <tr>
        <td>
          <a href="" on:click|preventDefault={() => view(post)}>
            {post.title}
          </a>
        </td>
        <td>{post.category?.name || "-"}</td>
        <td>{post.user?.username || "-"}</td>
        <td>
          {#if post.status === "pending"}
            <span class="badge bg-warning text-dark">Chờ duyệt</span>
          {:else if post.status === "approved"}
            <span class="badge bg-success">Đã duyệt</span>
          {:else if post.status === "rejected"}
            <span class="badge bg-danger">Bị từ chối</span>
          {:else}
            <span class="badge bg-secondary">Không xác định</span>
          {/if}
        </td>
        <td>
          <button class="btn btn-link btn-sm" on:click={() => view(post)}>
            Chi tiết
          </button>
        </td>
      </tr>
    {/each}

    {#if showPosts.length === 0}
      <tr>
        <td colspan="5" class="text-center text-muted">Không có bài đăng nào</td
        >
      </tr>
    {/if}
  </tbody>
</table>

<!-- Phân trang -->
{#if totalPages > 1}
  <nav class="d-flex justify-content-center">
    <ul class="pagination pagination-sm">
      <li class="page-item {page === 1 ? 'disabled' : ''}">
        <button class="page-link" on:click={() => goToPage(page - 1)}>
          Trước
        </button>
      </li>
      {#each Array(totalPages) as _, i}
        <li class="page-item {page === i + 1 ? 'active' : ''}">
          <button class="page-link" on:click={() => goToPage(i + 1)}>
            {i + 1}
          </button>
        </li>
      {/each}
      <li class="page-item {page === totalPages ? 'disabled' : ''}">
        <button class="page-link" on:click={() => goToPage(page + 1)}>
          Sau
        </button>
      </li>
    </ul>
  </nav>
{/if}

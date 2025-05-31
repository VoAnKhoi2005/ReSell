<script>
  import { createEventDispatcher } from "svelte";
  export let showPosts = [];
  export let tab = "pending"; // pending | all
  const dispatch = createEventDispatcher();

  function view(post) {
    dispatch("viewDetail", { post });
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
          <a href="#" on:click|preventDefault={() => view(post)}>{post.title}</a
          >
        </td>
        <td>{post.category}</td>
        <td>{post.user}</td>
        <td>
          {#if post.status === "pending"}
            <span class="badge bg-warning text-dark">Chờ duyệt</span>
          {:else if post.status === "approved"}
            <span class="badge bg-success">Đã duyệt</span>
          {:else}
            <span class="badge bg-danger">Bị từ chối</span>
          {/if}
        </td>
        <td>
          <button class="btn btn-link btn-sm" on:click={() => view(post)}
            >Chi tiết</button
          >
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

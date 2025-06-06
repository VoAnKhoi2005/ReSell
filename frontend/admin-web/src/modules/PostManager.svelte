<script>
  import { onMount } from "svelte";
  import PostList from "../components/PostList.svelte";
  import {
    fetchPosts,
    approvePost,
    rejectPost,
  } from "../services/postService.js";

  let posts = [];
  let search = "";
  let filterStatus = "all"; // all | pending | approved | rejected
  let selectedPost = null;
  let loading = true;

  let page = 1;
  let limit = 10;
  let total = 0;

  // Gọi khi load ban đầu hoặc khi đổi page
  async function loadPosts() {
    loading = true;
    try {
      const params = { page, limit };
      const res = await fetchPosts(params);
      posts = res.data || [];
      total = res.total || 0;
    } catch (err) {
      console.error("Lỗi khi load bài đăng:", err);
    } finally {
      loading = false;
    }
  }

  onMount(loadPosts);

  // Gọi lại loadPosts nếu đổi page
  $: if (page) loadPosts();

  // Danh sách hiển thị sau khi lọc theo trạng thái + search
  $: showPosts = posts.filter((p) => {
    const matchStatus = filterStatus === "all" || p.status === filterStatus;
    const matchSearch =
      search.trim() === "" ||
      p.title.toLowerCase().includes(search.trim().toLowerCase());
    return matchStatus && matchSearch;
  });

  function handleViewDetail(e) {
    selectedPost = e.detail.post;
  }

  function handleCloseDetail() {
    selectedPost = null;
  }

  async function handleApprove(e) {
    const { id } = e.detail;
    try {
      await approvePost(id);
      const idx = posts.findIndex((p) => p.id === id);
      if (idx > -1) {
        posts[idx].status = "approved";
        posts = [...posts];
        selectedPost = null;
      }
    } catch (err) {
      alert("Lỗi khi duyệt bài");
      console.error(err);
    }
  }

  async function handleReject(e) {
    const { id } = e.detail;
    try {
      await rejectPost(id);
      const idx = posts.findIndex((p) => p.id === id);
      if (idx > -1) {
        posts[idx].status = "rejected";
        posts = [...posts];
        selectedPost = null;
      }
    } catch (err) {
      alert("Lỗi khi từ chối bài");
      console.error(err);
    }
  }

  function handlePageChange(e) {
    page = e.detail.page;
  }
</script>

<div class="w-100">
  <div class="d-flex justify-content-between align-items-center mb-3">
    <h3>Quản lý bài đăng</h3>
    <div class="d-flex align-items-center gap-2">
      <input
        class="form-control form-control-sm"
        placeholder="Tìm tiêu đề"
        bind:value={search}
        style="width:200px;"
      />
      <select
        class="form-select form-select-sm"
        bind:value={filterStatus}
        style="width:120px;"
      >
        <option value="all">Tất cả</option>
        <option value="pending">Chờ duyệt</option>
        <option value="approved">Đã duyệt</option>
        <option value="rejected">Bị từ chối</option>
      </select>
    </div>
  </div>

  {#if loading}
    <div class="text-center text-muted my-3">Đang tải bài đăng...</div>
  {:else}
    <PostList
      {showPosts}
      {page}
      {limit}
      {total}
      on:viewDetail={handleViewDetail}
      on:changePage={handlePageChange}
    />
  {/if}

  {#if selectedPost}
    <div
      class="modal fade show"
      style="display:block; background:rgba(0,0,0,0.25);"
      tabindex="-1"
    >
      <div class="modal-dialog modal-lg modal-dialog-scrollable">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{selectedPost.title}</h5>
            <button type="button" class="btn-close" on:click={handleCloseDetail}
            ></button>
          </div>
          <div class="modal-body">
            {#if selectedPost.post_images?.length}
              <div class="mb-3 d-flex flex-wrap gap-2">
                {#each selectedPost.post_images.sort((a, b) => a.image_order - b.image_order) as img}
                  <img
                    src={img.image_url}
                    alt="Ảnh"
                    style="width: 250px; height: 160px; object-fit: cover; border-radius: 8px;"
                  />
                {/each}
              </div>
            {/if}
            <div><b>Mô tả:</b> {selectedPost.description}</div>
            <div><b>Danh mục:</b> {selectedPost.category?.name}</div>
            <div><b>Người đăng:</b> {selectedPost.user?.username}</div>
            <div>
              <b>Trạng thái:</b>
              {#if selectedPost.status === "pending"}
                <span class="badge bg-warning text-dark">Chờ duyệt</span>
              {:else if selectedPost.status === "approved"}
                <span class="badge bg-success">Đã duyệt</span>
              {:else if selectedPost.status === "rejected"}
                <span class="badge bg-danger">Bị từ chối</span>
              {:else}
                <span class="badge bg-secondary">Không xác định</span>
              {/if}
            </div>
          </div>
          <div class="modal-footer">
            {#if selectedPost.status === "pending"}
              <button
                class="btn btn-success"
                on:click={() =>
                  handleApprove({ detail: { id: selectedPost.id } })}
                >Duyệt</button
              >
              <button
                class="btn btn-danger"
                on:click={() =>
                  handleReject({ detail: { id: selectedPost.id } })}
                >Từ chối</button
              >
            {:else if selectedPost.status === "approved"}
              <button
                class="btn btn-danger"
                on:click={() =>
                  handleReject({ detail: { id: selectedPost.id } })}
                >Từ chối</button
              >
            {:else if selectedPost.status === "rejected"}
              <button
                class="btn btn-success"
                on:click={() =>
                  handleApprove({ detail: { id: selectedPost.id } })}
                >Duyệt</button
              >
            {/if}
            <button class="btn btn-secondary" on:click={handleCloseDetail}
              >Đóng</button
            >
          </div>
        </div>
      </div>
    </div>
    <div class="modal-backdrop fade show"></div>
  {/if}
</div>

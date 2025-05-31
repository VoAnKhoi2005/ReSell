<script>
  import PostList from "../components/PostList.svelte";

  // Mock data
  let posts = [
    {
      id: "1",
      title: "Bán iPhone 13 Pro Max",
      description: "Hàng xách tay, còn mới 99%.",
      category: "Điện thoại",
      user: "khoict",
      status: "pending",
    },
    {
      id: "2",
      title: "Xe Wave Alpha",
      description: "Wave alpha 2022 biển Sài Gòn.",
      category: "Xe máy",
      user: "admin1",
      status: "approved",
    },
    {
      id: "3",
      title: "Laptop Dell XPS",
      description: "Dell XPS 13, i7, RAM 16GB.",
      category: "Laptop",
      user: "user2",
      status: "rejected",
    },
  ];

  let search = "";
  let filterStatus = "all"; // all | pending | approved | rejected

  $: showPosts = posts.filter((p) => {
    const matchesSearch =
      search.trim() === "" ||
      p.title.toLowerCase().includes(search.trim().toLowerCase());
    const matchesFilter = filterStatus === "all" || p.status === filterStatus;
    return matchesSearch && matchesFilter;
  });

  let selectedPost = null;

  function handleViewDetail(e) {
    selectedPost = e.detail.post;
  }
  function handleCloseDetail() {
    selectedPost = null;
  }

  function handleApprove(e) {
    const { id } = e.detail;
    const idx = posts.findIndex((p) => p.id === id);
    if (idx > -1) {
      posts[idx].status = "approved";
      posts = [...posts];
      selectedPost = null;
    }
  }
  function handleReject(e) {
    const { id } = e.detail;
    const idx = posts.findIndex((p) => p.id === id);
    if (idx > -1) {
      posts[idx].status = "rejected";
      posts = [...posts];
      selectedPost = null;
    }
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

  <PostList {showPosts} on:viewDetail={handleViewDetail} />

  {#if selectedPost}
    <div
      class="modal fade show"
      style="display:block; background:rgba(0,0,0,0.25);"
      tabindex="-1"
    >
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{selectedPost.title}</h5>
            <button type="button" class="btn-close" on:click={handleCloseDetail}
            ></button>
          </div>
          <div class="modal-body">
            <div><b>Mô tả:</b> {selectedPost.description}</div>
            <div><b>Danh mục:</b> {selectedPost.category}</div>
            <div><b>Người đăng:</b> {selectedPost.user}</div>
            <div>
              <b>Trạng thái:</b>
              {#if selectedPost.status === "pending"}
                <span class="badge bg-warning text-dark">Chờ duyệt</span>
              {:else if selectedPost.status === "approved"}
                <span class="badge bg-success">Đã duyệt</span>
              {:else}
                <span class="badge bg-danger">Bị từ chối</span>
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

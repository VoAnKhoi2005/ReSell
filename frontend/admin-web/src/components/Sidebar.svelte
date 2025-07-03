<script>
  export let activeModule;
</script>

<div
  class="d-flex flex-column bg-white border-end p-0"
  style="width:220px; min-height:100vh;"
>
  <div class="nav flex-column py-3">
    <a
      class="nav-link px-4 py-2"
      class:active={activeModule === "category"}
      on:click={() => (activeModule = "category")}>Danh mục</a
    >
    <a
      class="nav-link px-4 py-2"
      class:active={activeModule === "address"}
      on:click={() => (activeModule = "address")}>Địa chỉ</a
    >
    <a
      class="nav-link px-4 py-2"
      class:active={activeModule === "user"}
      on:click={() => (activeModule = "user")}>Người dùng</a
    >
    <a
      class="nav-link px-4 py-2"
      class:active={activeModule === "post"}
      on:click={() => (activeModule = "post")}>Bài đăng</a
    >
    <!-- <a
      class="nav-link px-4 py-2"
      class:active={activeModule === "payment"}
      on:click={() => (activeModule = "payment")}>Phương thức thanh toán</a
    >
    <a
      class="nav-link px-4 py-2"
      class:active={activeModule === "subscription"}
      on:click={() => (activeModule = "subscription")}>Gói thành viên</a
    > -->
    <a
      class="nav-link px-4 py-2"
      class:active={activeModule === "report"}
      on:click={() => (activeModule = "report")}>Báo cáo</a
    >
  </div>
</div>

<style>
  .nav-link.active {
    background: #f0f4fa;
    font-weight: bold;
    color: #0062e6;
    border-radius: 6px;
  }
  .nav-link {
    color: #444;
    cursor: pointer;
    transition: 0.2s;
  }
  .nav-link:hover {
    background: #f0f4fa;
    color: #0056b3;
  }
</style>

<script>
  import Sidebar from "../components/Sidebar.svelte";
  import Topbar from "../components/Topbar.svelte";
  import CategoryManager from "../modules/CategoryManager.svelte";
  import AddressManager from "../modules/AddressManager.svelte";
  import UserManager from "../modules/UserManager.svelte";
  import PostManager from "../modules/PostManager.svelte";
  import PaymentManager from "../modules/PaymentManager.svelte";
  import SubscriptionManager from "../modules/SubscriptionManager.svelte";

  let activeModule = "category";
</script>

<div class="d-flex flex-column w-100 h-100" style="min-height:100vh;">
  <Topbar />
  <div class="d-flex flex-grow-1 w-100 h-100" style="min-height:0;">
    <Sidebar bind:activeModule />
    <main class="main-content p-4 overflow-auto">
      {#if activeModule === "category"}
        <CategoryManager />
      {:else if activeModule === "address"}
        <AddressManager />
      {:else if activeModule === "user"}
        <UserManager />
      {:else if activeModule === "post"}
        <PostManager />
      {:else if activeModule === "payment"}
        <PaymentManager />
      {:else if activeModule === "subscription"}
        <SubscriptionManager/>
      {:else}
        <div class="alert alert-warning">Vui lòng chọn một module để quản lý.</div>
      {/if}
    </main>
  </div>
</div>

<style>
  .sidebar {
    flex: 0 0 220px;
    min-height: 100vh;
    background: #fff;
    border-right: 1px solid #eee;
  }
  .main-content {
    flex: 1 1 0;
    min-width: 0;
    width: 100%;
    background: #f7f7fb;
    /* KHÔNG set max-width/margin auto! */
    height: 100vh;
    box-sizing: border-box;
  }
</style>

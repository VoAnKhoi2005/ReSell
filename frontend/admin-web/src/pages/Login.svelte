<script>
  import { createEventDispatcher } from "svelte";
  const dispatch = createEventDispatcher();

  let username = "";
  let password = "";

  function handleLogin(e) {
    e.preventDefault();
    // Tạm thời auto login thành công
    dispatch("loginSuccess");
  }
</script>

<div
  class="d-flex align-items-center justify-content-center"
  style="height:100vh;"
>
  <form
    class="border p-4 bg-white rounded shadow"
    style="min-width:320px;"
    on:submit={handleLogin}
  >
    <h4 class="mb-3 text-center">Admin Login</h4>
    <div class="mb-3">
      <label class="form-label">Tên đăng nhập</label>
      <input class="form-control" bind:value={username} required />
    </div>
    <div class="mb-3">
      <label class="form-label">Mật khẩu</label>
      <input
        type="password"
        class="form-control"
        bind:value={password}
        required
      />
    </div>
    <button type="submit" class="btn btn-primary w-100">Đăng nhập</button>
  </form>
</div>

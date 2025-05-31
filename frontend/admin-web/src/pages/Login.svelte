<script>
  import { setToken, setUser } from "../services/authService.js";
  import { apiFetch } from "../services/api.js";
  let username = "";
  let password = "";
  let error = "";
  let loading = false;

  async function login() {
    error = "";
    loading = true;
    try {
      const res = await apiFetch("/api/admin/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password }),
      });
      if (!res.ok) throw new Error("Sai tài khoản hoặc mật khẩu!");
      const data = await res.json();
      console.log("Dữ liệu trả về khi login:", data);
      setToken(data.token.access_token);
      setUser(data.user);
      window.location.reload();
    } catch (e) {
      error = e.message || "Có lỗi khi đăng nhập!";
    }
    loading = false;
  }
</script>

<div
  class="d-flex flex-column align-items-center justify-content-center"
  style="min-height: 60vh;"
>
  <div class="card" style="width:350px;">
    <div class="card-body">
      <h4 class="mb-3">Đăng nhập Admin</h4>
      {#if error}
        <div class="alert alert-danger py-2">{error}</div>
      {/if}
      <form on:submit|preventDefault={login}>
        <div class="mb-3">
          <input
            class="form-control"
            placeholder="Tên đăng nhập"
            bind:value={username}
            required
            autofocus
          />
        </div>
        <div class="mb-3">
          <input
            class="form-control"
            placeholder="Mật khẩu"
            type="password"
            bind:value={password}
            required
          />
        </div>
        <button class="btn btn-primary w-100" disabled={loading}>
          {#if loading}Đang đăng nhập...{:else}Đăng nhập{/if}
        </button>
      </form>
    </div>
  </div>
</div>

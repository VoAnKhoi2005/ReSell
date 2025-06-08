<script>
  import { onMount } from "svelte";
  import UserList from "../components/UserList.svelte";
  import { fetchUsers, banUser, unbanUser } from "../services/userService";

  let users = [];
  let search = "";
  let filter = "all"; // all | active | banned

  // Load users từ server
  async function loadUsers() {
    try {
      // Lấy 100 user đầu tiên (tùy backend, có thể sửa page/pageSize)
      const data = await fetchUsers(100, 1);
      users = (data.users || []).map((u) => ({
        id: u.id,
        username: u.username,
        email: u.email,
        phone: u.phone,
        fullname: u.fullname || "",
        cccd: u.citizen_id || "",
        reputation: u.reputation || 0,
        status: u.status === "banned" ? "banned" : "active", // fallback nếu backend trả về rỗng
      }));
    } catch (err) {
      alert("Không thể tải danh sách người dùng!");
      console.error(err);
    }
  }

  onMount(loadUsers);

  // Filter user hiển thị
  $: filteredUsers = users.filter((u) => {
    const matchesSearch =
      search.trim() === "" ||
      u.username?.toLowerCase().includes(search.trim().toLowerCase());
    const matchesFilter =
      filter === "all" ||
      (filter === "active" && u.status === "active") ||
      (filter === "banned" && u.status === "banned");
    return matchesSearch && matchesFilter;
  });

  // Ban/Unban user
  async function handleToggleBan(e) {
    const { id } = e.detail;
    const user = users.find((u) => u.id === id);
    if (!user) return;
    try {
      if (user.status === "active") {
        await banUser(id);
        user.status = "banned";
      } else {
        await unbanUser(id);
        user.status = "active";
      }
      users = [...users];
    } catch (err) {
      alert("Lỗi khi cập nhật trạng thái người dùng!");
      console.error(err);
    }
  }
</script>

<div class="w-100">
  <div class="d-flex justify-content-between align-items-center mb-3">
    <h3>Quản lý người dùng</h3>
    <div class="d-flex align-items-center gap-2">
      <input
        class="form-control form-control-sm"
        placeholder="Tìm theo tên đăng nhập"
        bind:value={search}
        style="width:200px;"
      />
      <select
        class="form-select form-select-sm"
        bind:value={filter}
        style="width:120px;"
      >
        <option value="all">Tất cả</option>
        <option value="active">Hoạt động</option>
        <option value="banned">Bị cấm</option>
      </select>
    </div>
  </div>

  <UserList users={filteredUsers} on:toggleBan={handleToggleBan} />
</div>

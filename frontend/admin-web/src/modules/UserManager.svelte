<script>
  import UserList from "../components/UserList.svelte";

  // Mock data mẫu
  let users = [
    {
      id: "1",
      username: "admin1",
      email: "admin1@example.com",
      phone: "0909000001",
      fullname: "Nguyễn Văn A",
      cccd: "123456789",
      reputation: 90,
      status: "active",
    },
    {
      id: "2",
      username: "user2",
      email: "user2@example.com",
      phone: "0909000002",
      fullname: "Trần Thị B",
      cccd: "223456789",
      reputation: 65,
      status: "banned",
    },
    {
      id: "3",
      username: "khoict",
      email: "khoict@gmail.com",
      phone: "0912333444",
      fullname: "Võ An Khôi",
      cccd: "1111222233",
      reputation: 99,
      status: "active",
    },
  ];

  let search = "";
  let filter = "all"; // all | active | banned

  // Tính toán user hiển thị (filter + search)
  $: filteredUsers = users.filter((u) => {
    const matchesSearch =
      search.trim() === "" ||
      u.username.toLowerCase().includes(search.trim().toLowerCase());
    const matchesFilter =
      filter === "all" ||
      (filter === "active" && u.status === "active") ||
      (filter === "banned" && u.status === "banned");
    return matchesSearch && matchesFilter;
  });

  // Ban/Unban
  function handleToggleBan(e) {
    const { id } = e.detail;
    const idx = users.findIndex((u) => u.id === id);
    if (idx > -1) {
      users[idx].status = users[idx].status === "active" ? "banned" : "active";
      users = [...users];
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

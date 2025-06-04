<script>
  import { onMount } from "svelte";
  import AddressTree from "../components/AddressTree.svelte";
  import {
    fetchProvinces,
    fetchDistricts,
    fetchWards,
    createProvince,
    createDistrict,
    createWard,
    updateProvince,
    updateDistrict,
    updateWard,
    deleteProvince,
    deleteDistrict,
    deleteWard,
  } from "../services/addressService";

  let provinces = [];
  let districts = [];
  let wards = [];
  let selectedProvince = null;
  let selectedDistrict = null;

  // Thêm mới
  let addingProvince = false;
  let newProvinceName = "";
  let addingDistrict = false;
  let newDistrictName = "";
  let addingWard = false;
  let newWardName = "";

  // Load tỉnh
  async function loadProvinces() {
    try {
      provinces = await fetchProvinces();
      console.log(provinces);
    } catch (err) {
      alert("Không thể tải danh sách tỉnh!");
      console.error(err);
    }
  }

  // Load huyện theo tỉnh
  async function loadDistricts() {
    try {
      districts = selectedProvince
        ? await fetchDistricts(selectedProvince)
        : [];
    } catch (err) {
      alert("Không thể tải danh sách huyện!");
      console.error(err);
    }
  }

  // Load xã theo huyện
  async function loadWards() {
    try {
      wards = selectedDistrict ? await fetchWards(selectedDistrict) : [];
    } catch (err) {
      alert("Không thể tải danh sách xã!");
      console.error(err);
    }
  }

  // Khi vào trang
  onMount(loadProvinces);

  // Chọn tỉnh → load huyện
  async function selectProvince(id) {
    selectedProvince = id;
    selectedDistrict = null;
    wards = [];
    await loadDistricts();
  }
  // Chọn huyện → load xã
  async function selectDistrict(id) {
    selectedDistrict = id;
    await loadWards();
  }

  // Thêm tỉnh
  async function handleAddProvince() {
    if (newProvinceName.trim() === "") return;
    try {
      await createProvince(newProvinceName);
      newProvinceName = "";
      addingProvince = false;
      await loadProvinces();
    } catch (err) {
      alert("Lỗi khi tạo tỉnh!");
      console.error(err);
    }
  }

  // Thêm huyện
  async function handleAddDistrict() {
    if (newDistrictName.trim() === "" || !selectedProvince) return;
    try {
      await createDistrict(newDistrictName, selectedProvince);
      newDistrictName = "";
      addingDistrict = false;
      await loadDistricts();
    } catch (err) {
      alert("Lỗi khi tạo huyện!");
      console.error(err);
    }
  }

  // Thêm xã
  async function handleAddWard() {
    if (newWardName.trim() === "" || !selectedDistrict) return;
    try {
      await createWard(newWardName, selectedDistrict);
      newWardName = "";
      addingWard = false;
      await loadWards();
    } catch (err) {
      alert("Lỗi khi tạo xã!");
      console.error(err);
    }
  }

  // Đổi tên tỉnh
  async function handleRenameProvince(e) {
    const { id, name } = e.detail;
    try {
      await updateProvince(id, name);
      await loadProvinces();
    } catch (err) {
      alert("Lỗi khi đổi tên tỉnh!");
      console.error(err);
    }
  }
  // Xóa tỉnh
  async function handleDeleteProvince(e) {
    const { id } = e.detail;
    if (!window.confirm("Xóa tỉnh sẽ xóa toàn bộ huyện/xã liên quan. Đồng ý?"))
      return;
    try {
      await deleteProvince(id);
      if (selectedProvince === id) selectedProvince = null;
      await loadProvinces();
      districts = [];
      wards = [];
    } catch (err) {
      alert("Lỗi khi xóa tỉnh!");
      console.error(err);
    }
  }

  // Đổi tên huyện
  async function handleRenameDistrict(e) {
    const { id, name } = e.detail;
    try {
      await updateDistrict(id, name);
      await loadDistricts();
    } catch (err) {
      alert("Lỗi khi đổi tên huyện!");
      console.error(err);
    }
  }
  // Xóa huyện
  async function handleDeleteDistrict(e) {
    const { id } = e.detail;
    if (!window.confirm("Xóa huyện sẽ xóa toàn bộ xã liên quan. Đồng ý?"))
      return;
    try {
      await deleteDistrict(id);
      if (selectedDistrict === id) selectedDistrict = null;
      await loadDistricts();
      wards = [];
    } catch (err) {
      alert("Lỗi khi xóa huyện!");
      console.error(err);
    }
  }

  // Đổi tên xã
  async function handleRenameWard(e) {
    const { id, name } = e.detail;
    try {
      await updateWard(id, name);
      await loadWards();
    } catch (err) {
      alert("Lỗi khi đổi tên xã!");
      console.error(err);
    }
  }
  // Xóa xã
  async function handleDeleteWard(e) {
    const { id } = e.detail;
    if (!window.confirm("Bạn có chắc chắn muốn xóa xã này?")) return;
    try {
      await deleteWard(id);
      await loadWards();
    } catch (err) {
      alert("Lỗi khi xóa xã!");
      console.error(err);
    }
  }
</script>

<!--
  ... Giữ nguyên phần giao diện bên dưới, chỉ thay đổi các biến FE như trên ...
-->

<div class="w-100">
  <div class="d-flex justify-content-between align-items-center mb-3">
    <h3>Quản lý địa chỉ</h3>
  </div>

  <div class="row">
    <div class="col-4">
      <div class="card">
        <div
          class="card-header fw-bold d-flex align-items-center justify-content-between"
        >
          <span>Tỉnh / Thành phố</span>
          {#if !addingProvince}
            <button
              class="btn btn-sm btn-primary"
              on:click={() => (addingProvince = true)}
            >
              + Thêm tỉnh
            </button>
          {:else}
            <div class="d-flex align-items-center">
              <input
                class="form-control form-control-sm"
                placeholder="Tên tỉnh"
                bind:value={newProvinceName}
                style="width:120px; margin-right:8px;"
                on:keydown={(e) => {
                  if (e.key === "Enter") handleAddProvince();
                }}
                autofocus
              />
              <button
                class="btn btn-success btn-sm me-1"
                on:click={handleAddProvince}>OK</button
              >
              <button
                class="btn btn-secondary btn-sm"
                on:click={() => {
                  addingProvince = false;
                  newProvinceName = "";
                }}>Huỷ</button
              >
            </div>
          {/if}
        </div>
        <div class="card-body p-0">
          <AddressTree
            type="province"
            items={provinces}
            selectedId={selectedProvince}
            on:select={(e) => selectProvince(e.detail.id)}
            on:rename={handleRenameProvince}
            on:delete={handleDeleteProvince}
          />
        </div>
      </div>
    </div>

    {#if selectedProvince}
      <div class="col-4">
        <div class="card">
          <div
            class="card-header fw-bold d-flex align-items-center justify-content-between"
          >
            <span>Quận / Huyện</span>
            {#if !addingDistrict}
              <button
                class="btn btn-sm btn-primary"
                on:click={() => (addingDistrict = true)}
              >
                + Thêm huyện
              </button>
            {:else}
              <div class="d-flex align-items-center">
                <input
                  class="form-control form-control-sm"
                  placeholder="Tên huyện"
                  bind:value={newDistrictName}
                  style="width:120px; margin-right:8px;"
                  on:keydown={(e) => {
                    if (e.key === "Enter") handleAddDistrict();
                  }}
                  autofocus
                />
                <button
                  class="btn btn-success btn-sm me-1"
                  on:click={handleAddDistrict}>OK</button
                >
                <button
                  class="btn btn-secondary btn-sm"
                  on:click={() => {
                    addingDistrict = false;
                    newDistrictName = "";
                  }}>Huỷ</button
                >
              </div>
            {/if}
          </div>
          <div class="card-body p-0">
            <AddressTree
              type="district"
              items={districts}
              selectedId={selectedDistrict}
              on:select={(e) => selectDistrict(e.detail.id)}
              on:rename={handleRenameDistrict}
              on:delete={handleDeleteDistrict}
            />
          </div>
        </div>
      </div>
    {/if}
    {#if selectedDistrict}
      <div class="col-4">
        <div class="card">
          <div
            class="card-header fw-bold d-flex align-items-center justify-content-between"
          >
            <span>Phường / Xã</span>
            {#if !addingWard}
              <button
                class="btn btn-sm btn-primary"
                on:click={() => (addingWard = true)}
              >
                + Thêm xã
              </button>
            {:else}
              <div class="d-flex align-items-center">
                <input
                  class="form-control form-control-sm"
                  placeholder="Tên xã"
                  bind:value={newWardName}
                  style="width:120px; margin-right:8px;"
                  on:keydown={(e) => {
                    if (e.key === "Enter") handleAddWard();
                  }}
                  autofocus
                />
                <button
                  class="btn btn-success btn-sm me-1"
                  on:click={handleAddWard}>OK</button
                >
                <button
                  class="btn btn-secondary btn-sm"
                  on:click={() => {
                    addingWard = false;
                    newWardName = "";
                  }}>Huỷ</button
                >
              </div>
            {/if}
          </div>
          <div class="card-body p-0">
            <AddressTree
              type="ward"
              items={wards}
              on:rename={handleRenameWard}
              on:delete={handleDeleteWard}
            />
          </div>
        </div>
      </div>
    {/if}
  </div>
</div>

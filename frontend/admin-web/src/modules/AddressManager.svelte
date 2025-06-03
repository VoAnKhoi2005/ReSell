<script>
  import AddressTree from "../components/AddressTree.svelte";
  import {
    fetchProvinces,
    fetchDistricts,
    fetchWards,
  } from "../services/addressService.js";
  import { onMount } from "svelte";

  let provinces = [];
  let districts = [];
  let wards = [];

  let selectedProvince = null;
  let selectedDistrict = null;

  // Fetch tỉnh khi load trang
  onMount(async () => {
    provinces = await fetchProvinces();
  });

  // Khi chọn tỉnh, fetch huyện
  async function selectProvince(id) {
    selectedProvince = id;
    selectedDistrict = null;
    wards = [];
    districts = id ? await fetchDistricts(id) : [];
  }

  // Khi chọn huyện, fetch xã
  async function selectDistrict(id) {
    selectedDistrict = id;
    wards = id ? await fetchWards(id) : [];
  }

  // Các hàm CRUD để lại trống, sẽ tích hợp sau nếu có API
  function handleRenameProvince(e) {}
  function handleDeleteProvince(e) {}
  function handleRenameDistrict(e) {}
  function handleDeleteDistrict(e) {}
  function handleRenameWard(e) {}
  function handleDeleteWard(e) {}
</script>

<div class="w-100">
  <div class="d-flex justify-content-between align-items-center mb-3">
    <h3>Quản lý địa chỉ</h3>
  </div>

  <div class="row">
    <!-- Tỉnh/Thành phố -->
    <div class="col-4">
      <div class="card">
        <div class="card-header fw-bold">Tỉnh / Thành phố</div>
        <div class="card-body p-0">
          <AddressTree
            type="province"
            {items}=provinces
            {selectedId}=selectedProvince
            on:select={(e) => selectProvince(e.detail.id)}
            on:rename={handleRenameProvince}
            on:delete={handleDeleteProvince}
          />
        </div>
      </div>
    </div>

    {#if selectedProvince}
      <!-- Quận/Huyện -->
      <div class="col-4">
        <div class="card">
          <div class="card-header fw-bold">Quận / Huyện</div>
          <div class="card-body p-0">
            <AddressTree
              type="district"
              {items}=districts
              {selectedId}=selectedDistrict
              on:select={(e) => selectDistrict(e.detail.id)}
              on:rename={handleRenameDistrict}
              on:delete={handleDeleteDistrict}
            />
          </div>
        </div>
      </div>
    {/if}

    {#if selectedDistrict}
      <!-- Phường/Xã -->
      <div class="col-4">
        <div class="card">
          <div class="card-header fw-bold">Phường / Xã</div>
          <div class="card-body p-0">
            <AddressTree
              type="ward"
              {items}=wards
              on:rename={handleRenameWard}
              on:delete={handleDeleteWard}
            />
          </div>
        </div>
      </div>
    {/if}
  </div>
</div>

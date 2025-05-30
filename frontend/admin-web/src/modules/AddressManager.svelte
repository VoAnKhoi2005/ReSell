<script>
  import AddressTree from "../components/AddressTree.svelte";

  let provinces = [
    { id: "1", name: "TP. Hồ Chí Minh" },
    { id: "2", name: "Hà Nội" },
  ];
  let districts = [
    { id: "11", name: "Quận 1", province_id: "1" },
    { id: "12", name: "Quận 3", province_id: "1" },
    { id: "21", name: "Ba Đình", province_id: "2" },
  ];
  let wards = [
    { id: "111", name: "Phường Bến Nghé", district_id: "11" },
    { id: "121", name: "Phường Võ Thị Sáu", district_id: "12" },
    { id: "211", name: "Phường Phúc Xá", district_id: "21" },
  ];

  let selectedProvince = null;
  let selectedDistrict = null;

  // Thêm tỉnh
  let addingProvince = false;
  let newProvinceName = "";

  // Thêm huyện
  let addingDistrict = false;
  let newDistrictName = "";

  // Thêm xã
  let addingWard = false;
  let newWardName = "";

  function handleAddProvince() {
    if (newProvinceName.trim() === "") return;
    const newId = Date.now().toString();
    provinces = [...provinces, { id: newId, name: newProvinceName }];
    newProvinceName = "";
    addingProvince = false;
  }

  function handleAddDistrict() {
    if (newDistrictName.trim() === "" || !selectedProvince) return;
    const newId = Date.now().toString();
    districts = [
      ...districts,
      { id: newId, name: newDistrictName, province_id: selectedProvince },
    ];
    newDistrictName = "";
    addingDistrict = false;
  }

  function handleAddWard() {
    if (newWardName.trim() === "" || !selectedDistrict) return;
    const newId = Date.now().toString();
    wards = [
      ...wards,
      { id: newId, name: newWardName, district_id: selectedDistrict },
    ];
    newWardName = "";
    addingWard = false;
  }

  function selectProvince(id) {
    selectedProvince = id;
    selectedDistrict = null;
  }
  function selectDistrict(id) {
    selectedDistrict = id;
  }

  // Xử lý sự kiện menu (đổi tên/xóa) nhận từ AddressTree
  function handleRenameProvince(e) {
    const { id, name } = e.detail;
    const idx = provinces.findIndex((p) => p.id === id);
    if (idx > -1) {
      provinces[idx].name = name;
      provinces = [...provinces];
    }
  }
  function handleDeleteProvince(e) {
    const { id } = e.detail;
    if (window.confirm("Xóa tỉnh sẽ xóa tất cả huyện/xã liên quan. Đồng ý?")) {
      // Xóa huyện, xã thuộc tỉnh
      const removeDistrictIds = districts
        .filter((d) => d.province_id === id)
        .map((d) => d.id);
      districts = districts.filter((d) => d.province_id !== id);
      wards = wards.filter((w) => !removeDistrictIds.includes(w.district_id));
      provinces = provinces.filter((p) => p.id !== id);
      if (selectedProvince === id) selectedProvince = null;
    }
  }

  function handleRenameDistrict(e) {
    const { id, name } = e.detail;
    const idx = districts.findIndex((d) => d.id === id);
    if (idx > -1) {
      districts[idx].name = name;
      districts = [...districts];
    }
  }
  function handleDeleteDistrict(e) {
    const { id } = e.detail;
    if (window.confirm("Xóa huyện sẽ xóa tất cả xã liên quan. Đồng ý?")) {
      wards = wards.filter((w) => w.district_id !== id);
      districts = districts.filter((d) => d.id !== id);
      if (selectedDistrict === id) selectedDistrict = null;
    }
  }

  function handleRenameWard(e) {
    const { id, name } = e.detail;
    const idx = wards.findIndex((w) => w.id === id);
    if (idx > -1) {
      wards[idx].name = name;
      wards = [...wards];
    }
  }
  function handleDeleteWard(e) {
    const { id } = e.detail;
    if (window.confirm("Bạn có chắc chắn muốn xóa xã này?")) {
      wards = wards.filter((w) => w.id !== id);
    }
  }
</script>

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
              on:click={() => (addingProvince = true)}>+ Thêm tỉnh</button
            >
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
                on:click={() => (addingDistrict = true)}>+ Thêm huyện</button
              >
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
              items={districts.filter(
                (d) => d.province_id === selectedProvince
              )}
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
                on:click={() => (addingWard = true)}>+ Thêm xã</button
              >
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
              items={wards.filter((w) => w.district_id === selectedDistrict)}
              on:rename={handleRenameWard}
              on:delete={handleDeleteWard}
            />
          </div>
        </div>
      </div>
    {/if}
  </div>
</div>

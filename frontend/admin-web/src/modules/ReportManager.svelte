<!-- src/pages/ReportManager.svelte -->
<script>
  import { onMount } from "svelte";
  import {
    fetchReportUsers,
    fetchReportPosts,
  } from "../services/reportService";
  import ReportList from "../components/ReportList.svelte";
  import ReportDetailModal from "../components/ReportDetailModal.svelte";

  let activeTab = "users"; // "users" | "posts"
  let reports = [];
  let page = 1;
  let limit = 10;
  let total = 0;

  let selectedReport = null;
  let showModal = false;

  async function loadReports() {
    try {
      const fetchFn =
        activeTab === "users" ? fetchReportUsers : fetchReportPosts;
      const res = await fetchFn(limit, page);
      reports = res.data || [];
      total = res.total || 0;
    } catch (err) {
      console.error("Lỗi khi tải báo cáo:", err);
      alert("Không thể tải danh sách báo cáo!");
    }
  }

  function handleView(report) {
    selectedReport = report;
    showModal = true;
  }

  function handlePageChange(p) {
    page = p;
  }

  $: loadReports(), [activeTab, page];

  onMount(loadReports);
</script>

<div>
  <h3>Danh sách báo cáo</h3>
  <div class="mb-3">
    <button
      class="btn btn-sm me-2 {activeTab === 'users'
        ? 'btn-primary'
        : 'btn-outline-primary'}"
      on:click={() => {
        activeTab = "users";
        page = 1;
      }}
    >
      Báo cáo người dùng
    </button>
    <button
      class="btn btn-sm {activeTab === 'posts'
        ? 'btn-primary'
        : 'btn-outline-primary'}"
      on:click={() => {
        activeTab = "posts";
        page = 1;
      }}
    >
      Báo cáo bài đăng
    </button>
  </div>

  <ReportList
    {reports}
    {page}
    {limit}
    {total}
    on:view={(e) => handleView(e.detail)}
    on:changePage={(e) => handlePageChange(e.detail.page)}
  />

  {#if showModal && selectedReport}
    <ReportDetailModal
      report={selectedReport}
      onClose={() => {
        showModal = false;
        selectedReport = null;
      }}
      on:close={() => {
        showModal = false;
        selectedReport = null;
      }}
    />
  {/if}
</div>

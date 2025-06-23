<!-- src/components/ReportDetailModal.svelte -->
<script>
  export let report;
  export let onClose;

  function formatDate(datetime) {
    const date = new Date(datetime);
    return date.toLocaleString("vi-VN");
  }
</script>

<div
  class="modal fade show"
  style="display:block; background:rgba(0,0,0,0.25);"
  tabindex="-1"
>
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Chi tiết báo cáo</h5>
        <button type="button" class="btn-close" on:click={onClose}></button>
      </div>
      <div class="modal-body">
        <div>
          <b>Đối tượng bị báo cáo:</b>
          {report.reported.username || report.reported.title}
        </div>
        <div><b>Người báo cáo:</b> {report.reporter.username}</div>
        <div><b>Thời gian:</b> {formatDate(report.created_at)}</div>
        <hr />
        <div><b>Nội dung báo cáo:</b></div>
        <div class="mt-1">{report.description}</div>
      </div>
      <div class="modal-footer">
        <button class="btn btn-sm btn-secondary" on:click={onClose}>Đóng</button
        >
      </div>
    </div>
  </div>
</div>
<div class="modal-backdrop fade show"></div>

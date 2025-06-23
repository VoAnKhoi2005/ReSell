<!-- src/components/ReportList.svelte -->
<script>
  import { createEventDispatcher } from "svelte";

  export let reports = [];
  export let page = 1;
  export let limit = 10;
  export let total = 0;

  const dispatch = createEventDispatcher();
  $: totalPages = Math.ceil(total / limit);

  function goToPage(p) {
    if (p >= 1 && p <= totalPages && p !== page) {
      dispatch("changePage", { page: p });
    }
  }

  function formatDate(datetime) {
    const date = new Date(datetime);
    return date.toLocaleString("vi-VN");
  }
</script>

<table class="table table-hover align-middle">
  <thead>
    <tr>
      <th>Đối tượng bị báo cáo</th>
      <th>Người báo cáo</th>
      <th>Thời gian</th>
    </tr>
  </thead>
  <tbody>
    {#each reports as report (report.id)}
      <tr on:click={() => dispatch("view", report)} style="cursor:pointer">
        <td>{report.reported?.username || report.reported?.title || ""}</td>
        <td>{report.reporter.username}</td>
        <td>{formatDate(report.created_at)}</td>
      </tr>
    {/each}

    {#if reports.length === 0}
      <tr>
        <td colspan="3" class="text-muted text-center">Không có báo cáo nào</td>
      </tr>
    {/if}
  </tbody>
</table>

{#if totalPages > 1}
  <nav class="d-flex justify-content-center">
    <ul class="pagination pagination-sm">
      <li class="page-item {page === 1 ? 'disabled' : ''}">
        <button class="page-link" on:click={() => goToPage(page - 1)}
          >Trước</button
        >
      </li>
      {#each Array(totalPages) as _, i}
        <li class="page-item {page === i + 1 ? 'active' : ''}">
          <button class="page-link" on:click={() => goToPage(i + 1)}
            >{i + 1}</button
          >
        </li>
      {/each}
      <li class="page-item {page === totalPages ? 'disabled' : ''}">
        <button class="page-link" on:click={() => goToPage(page + 1)}
          >Sau</button
        >
      </li>
    </ul>
  </nav>
{/if}

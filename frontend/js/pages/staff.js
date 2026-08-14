import { icon } from '../icons.js';
// staff.js —— 员工排班
import { Api } from '../api.js';

const SHIFTS = ['早班 09:00-14:00', '午班 14:00-18:00', '晚班 18:00-22:00'];
const DAYS = ['周一', '周二', '周三', '周四', '周五', '周六', '周日'];

export async function renderStaff(root) {
  root.innerHTML = `
    <div class="page">
      <div class="page-title">员工排班</div>
      <div class="page-desc">本周班次安排</div>
      <div class="table-wrap" style="margin-top:16px"><table class="table">
        <thead><tr><th>员工</th>${DAYS.map((d) => `<th>${d}</th>`).join('')}</tr></thead>
        <tbody id="rows"><tr><td colspan="8"><div class="loading">加载中…</div></td></tr></tbody>
      </table></div>
    </div>`;
  const rows = root.querySelector('#rows');
  const emps = await Api.listEmployees().catch(() => []);
  const list = emps.length ? emps : await Api.listStaff().catch(() => []);
  if (!list.length) { rows.innerHTML = `<tr><td colspan="8"><div class="empty"><div class="em-ic">${icon('calendar')}</div><div class="em-txt">暂无排班数据</div></div></td></tr>`; return; }
  rows.innerHTML = list.map((e) => `<tr>
    <td class="cell-strong">${e.name || ('员工#' + e.id)}</td>
    ${DAYS.map((_, i) => `<td><span class="badge ${['badge-ok', 'badge-info', 'badge-warn'][i % 3]}">${SHIFTS[i % 3].slice(0, 2)}</span></td>`).join('')}
  </tr>`).join('');
}

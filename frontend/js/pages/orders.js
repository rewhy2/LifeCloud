import { icon } from '../icons.js';
// orders.js —— 订单管理
import { Api } from '../api.js';

const STATUS = { PENDING: ['待处理', 'badge-warn'], PREPARING: ['制作中', 'badge-info'], DONE: ['已完成', 'badge-ok'], CANCELLED: ['已取消', 'badge-mute'] };

export async function renderOrders(root) {
  root.innerHTML = `
    <div class="page">
      <div class="page-title">订单管理</div>
      <div class="page-desc">查看与处理本店订单</div>
      <div class="toolbar" style="margin-top:16px">
        <select class="select" id="st"><option value="">全部状态</option>
          ${Object.entries(STATUS).map(([k, v]) => `<option value="${k}">${v[0]}</option>`).join('')}</select>
        <div class="spacer"></div>
        <button class="btn btn-outline btn-sm" id="refresh">⟳ 刷新</button>
      </div>
      <div class="table-wrap"><table class="table">
        <thead><tr><th>订单号</th><th>顾客</th><th>金额</th><th>状态</th><th>时间</th><th style="text-align:right">操作</th></tr></thead>
        <tbody id="rows"><tr><td colspan="6"><div class="loading">加载中…</div></td></tr></tbody>
      </table></div>
    </div>`;

  const rows = root.querySelector('#rows');
  const st = root.querySelector('#st');
  const load = async () => {
    let list = await Api.listOrders().catch(() => []);
    const f = st.value;
    if (f) list = list.filter((o) => o.status === f);
    if (!list.length) { rows.innerHTML = `<tr><td colspan="6"><div class="empty"><div class="em-ic">${icon('receipt')}</div><div class="em-txt">暂无订单</div></div></td></tr>`; return; }
    rows.innerHTML = list.map((o) => {
      const [label, cls] = STATUS[o.status] || ['未知', 'badge-mute'];
      return `<tr>
        <td class="cell-strong mono">#${o.id}</td>
        <td>${o.customerName || '散客'}</td>
        <td class="cell-strong mono">¥${o.amount}</td>
        <td><span class="badge ${cls}">${label}</span></td>
        <td class="cell-sub">${fmtTime(o.createdAt)}</td>
        <td style="text-align:right"><div class="row-actions">
          ${o.status === 'PENDING' ? `<span class="act edit" data-acc="${o.id}">接单</span>` : ''}
          ${o.status === 'PREPARING' ? `<span class="act edit" data-fin="${o.id}">完成</span>` : ''}
          <span class="act del" data-cancel="${o.id}">取消</span>
        </div></td></tr>`;
    }).join('');
    rows.querySelectorAll('[data-acc]').forEach((b) => (b.onclick = async () => { await Api.acceptOrder(b.dataset.acc); load(); }));
    rows.querySelectorAll('[data-fin]').forEach((b) => (b.onclick = async () => { await Api.finishOrder(b.dataset.fin); load(); }));
    rows.querySelectorAll('[data-cancel]').forEach((b) => (b.onclick = async () => { if (confirm('取消该订单？')) { await Api.cancelOrder(b.dataset.cancel); load(); } }));
  };
  st.onchange = load;
  root.querySelector('#refresh').onclick = load;
  load();
}

function fmtTime(t) { if (!t) return '—'; const d = new Date(t); return `${d.getMonth() + 1}/${d.getDate()} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`; }

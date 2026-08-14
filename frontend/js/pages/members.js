import { icon } from '../icons.js';
// members.js —— 会员管理
import { Api } from '../api.js';

export async function renderMembers(root) {
  root.innerHTML = `
    <div class="page">
      <div class="page-title">会员管理</div>
      <div class="page-desc">门店会员与积分概览</div>
      <div class="toolbar" style="margin-top:16px"><input class="input" id="kw" placeholder="搜索姓名 / 手机…" style="min-width:220px"><div class="spacer"></div>
        <button class="btn btn-outline btn-sm" id="refresh">⟳ 刷新</button></div>
      <div class="table-wrap"><table class="table">
        <thead><tr><th>会员</th><th>手机</th><th>积分</th><th>等级</th><th>注册</th></tr></thead>
        <tbody id="rows"><tr><td colspan="5"><div class="loading">加载中…</div></td></tr></tbody>
      </table></div>
    </div>`;
  const rows = root.querySelector('#rows');
  const load = async () => {
    const kw = root.querySelector('#kw').value.trim();
    let list = await Api.listMembers().catch(() => []);
    if (kw) list = list.filter((m) => (m.name || '').includes(kw) || (m.phone || '').includes(kw));
    if (!list.length) { rows.innerHTML = `<tr><td colspan="5"><div class="empty"><div class="em-ic">${icon('users')}</div><div class="em-txt">暂无会员</div></div></td></tr>`; return; }
    rows.innerHTML = list.map((m) => `<tr>
      <td><div style="display:flex;align-items:center;gap:10px"><div class="thumb sm">${(m.name || '?').slice(0,1)}</div><div class="cell-strong">${m.name}</div></div></td>
      <td class="mono">${m.phone || '—'}</td>
      <td class="cell-strong mono">${m.points ?? 0}</td>
      <td><span class="badge badge-info">${m.level || '普通'}</span></td>
      <td class="cell-sub">${m.createdAt || '—'}</td></tr>`).join('');
  };
  root.querySelector('#kw').oninput = debounce(load, 300);
  root.querySelector('#refresh').onclick = load;
  load();
}
function debounce(fn, d) { let t; return (...a) => { clearTimeout(t); t = setTimeout(() => fn(...a), d); }; }

import { icon } from '../icons.js';
// inventory.js —— 库存管理
import { Api } from '../api.js';

export async function renderInventory(root) {
  root.innerHTML = `
    <div class="page">
      <div class="page-title">库存管理</div>
      <div class="page-desc">实时食材与物料库存</div>
      <div class="toolbar" style="margin-top:16px"><input class="input" id="kw" placeholder="搜索…" style="min-width:200px"><div class="spacer"></div>
        <button class="btn btn-outline btn-sm" id="refresh">⟳ 刷新</button></div>
      <div class="table-wrap"><table class="table">
        <thead><tr><th>物料</th><th>分类</th><th>当前库存</th><th>单位</th><th>状态</th></tr></thead>
        <tbody id="rows"><tr><td colspan="5"><div class="loading">加载中…</div></td></tr></tbody>
      </table></div>
    </div>`;
  const rows = root.querySelector('#rows');
  const load = async () => {
    const kw = root.querySelector('#kw').value.trim();
    let list = await Api.listInventory().catch(() => []);
    if (kw) list = list.filter((i) => (i.name || '').includes(kw));
    if (!list.length) { rows.innerHTML = `<tr><td colspan="5"><div class="empty"><div class="em-ic">${icon('box')}</div><div class="em-txt">暂无库存记录</div></div></td></tr>`; return; }
    rows.innerHTML = list.map((i) => {
      const low = (i.quantity ?? 0) <= (i.threshold ?? 0);
      return `<tr>
        <td class="cell-strong">${i.name}</td>
        <td>${i.category || '—'}</td>
        <td class="mono cell-strong">${i.quantity ?? 0}</td>
        <td>${i.unit || ''}</td>
        <td>${low ? '<span class="badge badge-warn">偏低</span>' : '<span class="badge badge-ok">充足</span>'}</td></tr>`;
    }).join('');
  };
  root.querySelector('#kw').oninput = debounce(load, 300);
  root.querySelector('#refresh').onclick = load;
  load();
}
function debounce(fn, d) { let t; return (...a) => { clearTimeout(t); t = setTimeout(() => fn(...a), d); }; }

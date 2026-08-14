import { icon } from '../icons.js';
// purchases.js —— 采购管理
import { Api } from '../api.js';
import { openModal } from '../components/modal.js';

export async function renderPurchases(root) {
  root.innerHTML = `
    <div class="page">
      <div class="page-title">采购管理</div>
      <div class="page-desc">发起采购单、跟踪入库状态</div>
      <div class="toolbar" style="margin-top:16px">
        <select class="select" id="st"><option value="">全部状态</option><option value="0">待入库</option><option value="1">已入库</option></select>
        <div class="spacer"></div>
        <button class="btn btn-primary btn-sm" id="add">＋ 新建采购单</button>
      </div>
      <div class="table-wrap"><table class="table">
        <thead><tr><th>单号</th><th>供应商</th><th>金额</th><th>状态</th><th>时间</th><th style="text-align:right">操作</th></tr></thead>
        <tbody id="rows"><tr><td colspan="6"><div class="loading">加载中…</div></td></tr></tbody>
      </table></div>
    </div>`;
  const rows = root.querySelector('#rows');
  const st = root.querySelector('#st');
  const load = async () => {
    const list = await Api.listPurchases(st.value).catch(() => []);
    if (!list.length) { rows.innerHTML = `<tr><td colspan="6"><div class="empty"><div class="em-ic">${icon('receipt')}</div><div class="em-txt">暂无采购单</div></div></td></tr>`; return; }
    rows.innerHTML = list.map((p) => `<tr>
      <td class="cell-strong mono">#${p.id}</td>
      <td>${p.supplierName || '—'}</td>
      <td class="cell-strong mono">¥${p.totalAmount ?? 0}</td>
      <td>${p.status === 1 ? '<span class="badge badge-ok">已入库</span>' : '<span class="badge badge-warn">待入库</span>'}</td>
      <td class="cell-sub">${p.createdAt || '—'}</td>
      <td style="text-align:right"><div class="row-actions">
        <span class="act edit" data-view="${p.id}">查看</span>
        ${p.status !== 1 ? `<span class="act edit" data-in="${p.id}">入库</span>` : ''}
      </div></td></tr>`).join('');
    rows.querySelectorAll('[data-view]').forEach((b) => (b.onclick = () => view(b.dataset.view)));
    rows.querySelectorAll('[data-in]').forEach((b) => (b.onclick = async () => { if (confirm('确认入库并增加库存？')) { await Api.stockInPurchase(b.dataset.in); load(); } }));
  };
  st.onchange = load;
  root.querySelector('#add').onclick = create;
  load();

  async function create() {
    const suppliers = await Api.listSuppliers().catch(() => []);
    let idx = 0;
    const lineHtml = () => `<div class="form-row line" style="margin-bottom:8px">
      <div class="field"><input class="input" data-name placeholder="物料名称"></div>
      <div class="field" style="display:flex;gap:8px"><input class="input" data-qty type="number" placeholder="数量" style="flex:1"><input class="input" data-price type="number" placeholder="单价" style="flex:1"></div>
    </div>`;
    openModal({
      title: '新建采购单', wide: true,
      body: `<div class="field"><label>供应商</label><select class="select" id="f_sup">${suppliers.map((s) => `<option value="${s.id}">${s.name}</option>`).join('') || '<option value="">（无供应商）</option>'}</select></div>
        <div id="lines">${lineHtml()}</div>
        <button class="btn btn-ghost btn-sm" id="addLine" style="margin-top:4px">＋ 添加一行</button>`,
      confirmText: '提交采购',
      onConfirm: () => {
        const lines = [...document.querySelectorAll('#lines .line')].map((l) => ({ name: l.querySelector('[data-name]').value.trim(), qty: Number(l.querySelector('[data-qty]').value || 0), price: Number(l.querySelector('[data-price]').value || 0) })).filter((x) => x.name);
        const supplierId = document.getElementById('f_sup').value;
        if (!lines.length) { alert('请至少填写一行物料'); return false; }
        return Api.createPurchase({ supplierId: supplierId ? Number(supplierId) : null, items: lines }).then(load);
      },
    });
    setTimeout(() => { const b = document.getElementById('addLine'); if (b) b.onclick = () => document.getElementById('lines').insertAdjacentHTML('beforeend', lineHtml()); }, 0);
  }

  async function view(id) {
    const p = await Api.getPurchase(id).catch(() => null);
    if (!p) return;
    openModal({
      title: `采购单 #${p.id}`,
      body: `<div class="field"><label>供应商</label><div class="cell-strong">${p.supplierName || '—'}</div></div>
        <div class="field"><label>明细</label>
          <div class="table-wrap"><table class="table"><thead><tr><th>物料</th><th>数量</th><th>单价</th><th>小计</th></tr></thead>
          <tbody>${(p.items || []).map((i) => `<tr><td>${i.name}</td><td>${i.qty}</td><td>¥${i.price}</td><td>¥${(i.qty * i.price).toFixed(2)}</td></tr>`).join('') || '<tr><td colspan="4" class="muted">无明细</td></tr>'}</tbody></table></div></div>
        <div class="field" style="text-align:right;font-weight:700;margin-top:8px">合计：¥${p.totalAmount ?? 0}</div>`,
    });
  }
}

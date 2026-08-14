import { icon } from '../icons.js';
// coupons.js —— 优惠券管理
import { Api } from '../api.js';
import { openModal } from '../components/modal.js';

export async function renderCoupons(root) {
  root.innerHTML = `
    <div class="page">
      <div class="page-title">优惠券管理</div>
      <div class="page-desc">发放与配置营销优惠券</div>
      <div class="toolbar" style="margin-top:16px"><div class="spacer"></div>
        <button class="btn btn-primary btn-sm" id="add">＋ 新建优惠券</button></div>
      <div class="table-wrap"><table class="table">
        <thead><tr><th>名称</th><th>面额</th><th>门槛</th><th>余量</th><th>状态</th><th style="text-align:right">操作</th></tr></thead>
        <tbody id="rows"><tr><td colspan="6"><div class="loading">加载中…</div></td></tr></tbody>
      </table></div>
    </div>`;
  const rows = root.querySelector('#rows');
  const load = async () => {
    const list = await Api.listCoupons().catch(() => []);
    if (!list.length) { rows.innerHTML = `<tr><td colspan="6"><div class="empty"><div class="em-ic">${icon('tag')}</div><div class="em-txt">暂无优惠券</div></div></td></tr>`; return; }
    rows.innerHTML = list.map((c) => `<tr>
      <td class="cell-strong">${c.name}</td>
      <td class="mono">¥${c.amount ?? 0}</td>
      <td class="mono">${c.threshold ? '满¥' + c.threshold : '无门槛'}</td>
      <td class="mono">${c.stock ?? '∞'}</td>
      <td>${c.status === 1 ? '<span class="badge badge-ok">发放中</span>' : '<span class="badge badge-mute">已下线</span>'}</td>
      <td style="text-align:right"><div class="row-actions"><span class="act del" data-del="${c.id}">删除</span></div></td></tr>`).join('');
    rows.querySelectorAll('[data-del]').forEach((b) => (b.onclick = async () => { if (confirm('删除该优惠券？')) { await Api.deleteCoupon(b.dataset.del); load(); } }));
  };
  root.querySelector('#add').onclick = () => openModal({
    title: '新建优惠券',
    body: `<div class="field"><label>名称</label><input class="input" id="f_name" placeholder="如：新客立减"></div>
      <div class="form-row"><div class="field"><label>面额（元）</label><input class="input" id="f_amount" type="number" placeholder="10"></div>
      <div class="field"><label>使用门槛（元）</label><input class="input" id="f_th" type="number" placeholder="0 = 无门槛"></div></div>
      <div class="field"><label>发放数量</label><input class="input" id="f_stock" type="number" placeholder="留空为不限量"></div>`,
    onConfirm: () => {
      const payload = { name: document.getElementById('f_name').value.trim(), amount: Number(document.getElementById('f_amount').value || 0), threshold: Number(document.getElementById('f_th').value || 0), stock: document.getElementById('f_stock').value ? Number(document.getElementById('f_stock').value) : null, status: 1 };
      if (!payload.name) { alert('请填写名称'); return false; }
      return Api.saveCoupon(payload).then(load);
    },
  });
  load();
}

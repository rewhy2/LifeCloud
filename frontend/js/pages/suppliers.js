import { icon } from '../icons.js';
// suppliers.js —— 供应商管理
import { Api } from '../api.js';
import { openModal } from '../components/modal.js';

export async function renderSuppliers(root) {
  root.innerHTML = `
    <div class="page">
      <div class="page-title">供应商管理</div>
      <div class="page-desc">维护食材与物料的供应渠道</div>
      <div class="toolbar" style="margin-top:16px"><div class="spacer"></div>
        <button class="btn btn-primary btn-sm" id="add">＋ 新增供应商</button></div>
      <div class="table-wrap"><table class="table">
        <thead><tr><th>名称</th><th>联系人</th><th>电话</th><th>状态</th><th style="text-align:right">操作</th></tr></thead>
        <tbody id="rows"><tr><td colspan="5"><div class="loading">加载中…</div></td></tr></tbody>
      </table></div>
    </div>`;
  const rows = root.querySelector('#rows');
  const load = async () => {
    const list = await Api.listSuppliers().catch(() => []);
    if (!list.length) { rows.innerHTML = `<tr><td colspan="5"><div class="empty"><div class="em-ic">${icon('supplier')}</div><div class="em-txt">暂无供应商</div></div></td></tr>`; return; }
    rows.innerHTML = list.map((s) => `<tr>
      <td class="cell-strong">${s.name}</td>
      <td>${s.contact || '—'}</td>
      <td class="mono">${s.phone || '—'}</td>
      <td>${s.status === 0 ? '<span class="badge badge-ok">合作中</span>' : '<span class="badge badge-mute">停用</span>'}</td>
      <td style="text-align:right"><div class="row-actions">
        <span class="act edit" data-edit="${s.id}">编辑</span>
        <span class="act del" data-del="${s.id}">删除</span></div></td></tr>`).join('');
    rows.querySelectorAll('[data-edit]').forEach((b) => (b.onclick = () => edit(b.dataset.edit)));
    rows.querySelectorAll('[data-del]').forEach((b) => (b.onclick = async () => { if (confirm('删除该供应商？')) { await Api.deleteSupplier(b.dataset.del); load(); } }));
  };
  root.querySelector('#add').onclick = () => edit(null);
  load();

  async function edit(id) {
    const s = id ? await Api.listSuppliers().then((l) => l.find((x) => String(x.id) === String(id))) : null;
    openModal({
      title: id ? '编辑供应商' : '新增供应商',
      body: `<div class="field"><label>名称</label><input class="input" id="f_name" value="${s ? s.name : ''}"></div>
        <div class="form-row">
          <div class="field"><label>联系人</label><input class="input" id="f_contact" value="${s ? (s.contact || '') : ''}"></div>
          <div class="field"><label>电话</label><input class="input" id="f_phone" value="${s ? (s.phone || '') : ''}"></div>
        </div>
        <div class="field"><label>状态</label>
          <div class="switch-row"><div><div class="label-txt">保持合作</div></div>
            <label class="switch"><input type="checkbox" id="f_status" ${!s || s.status === 0 ? 'checked' : ''}><span class="track"></span></label></div></div>`,
      onConfirm: () => {
        const payload = { name: document.getElementById('f_name').value.trim(), contact: document.getElementById('f_contact').value.trim(), phone: document.getElementById('f_phone').value.trim(), status: document.getElementById('f_status').checked ? 0 : 1 };
        if (!payload.name) { alert('请填写名称'); return false; }
        return (id ? Api.saveSupplier(id, payload) : Api.saveSupplier(null, payload)).then(load);
      },
    });
  }
}

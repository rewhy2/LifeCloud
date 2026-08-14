import { icon } from '../icons.js';
// employees.js —— 员工花名册
import { Api } from '../api.js';
import { openModal } from '../components/modal.js';

export async function renderEmployees(root) {
  root.innerHTML = `
    <div class="page">
      <div class="page-title">员工花名册</div>
      <div class="page-desc">门店员工信息管理</div>
      <div class="toolbar" style="margin-top:16px"><div class="spacer"></div>
        <button class="btn btn-primary btn-sm" id="add">＋ 新增员工</button></div>
      <div class="table-wrap"><table class="table">
        <thead><tr><th>姓名</th><th>岗位</th><th>电话</th><th>入职</th><th style="text-align:right">操作</th></tr></thead>
        <tbody id="rows"><tr><td colspan="5"><div class="loading">加载中…</div></td></tr></tbody>
      </table></div>
    </div>`;
  const rows = root.querySelector('#rows');
  const load = async () => {
    const list = await Api.listEmployees().catch(() => []);
    if (!list.length) { rows.innerHTML = `<tr><td colspan="5"><div class="empty"><div class="em-ic">${icon('user')}</div><div class="em-txt">暂无员工</div></div></td></tr>`; return; }
    rows.innerHTML = list.map((e) => `<tr>
      <td class="cell-strong">${e.name}</td>
      <td>${e.position || '—'}</td>
      <td class="mono">${e.phone || '—'}</td>
      <td class="cell-sub">${e.hireDate || '—'}</td>
      <td style="text-align:right"><div class="row-actions">
        <span class="act edit" data-edit="${e.id}">编辑</span>
        <span class="act del" data-del="${e.id}">删除</span></div></td></tr>`).join('');
    rows.querySelectorAll('[data-edit]').forEach((b) => (b.onclick = () => edit(b.dataset.edit)));
    rows.querySelectorAll('[data-del]').forEach((b) => (b.onclick = async () => { if (confirm('删除该员工？')) { await Api.deleteEmployee(b.dataset.del); load(); } }));
  };
  root.querySelector('#add').onclick = () => edit(null);
  load();

  async function edit(id) {
    const e = id ? await Api.listEmployees().then((l) => l.find((x) => String(x.id) === String(id))) : null;
    openModal({
      title: id ? '编辑员工' : '新增员工',
      body: `<div class="form-row">
          <div class="field"><label>姓名</label><input class="input" id="f_name" value="${e ? e.name : ''}"></div>
          <div class="field"><label>岗位</label><input class="input" id="f_position" value="${e ? (e.position || '') : ''}" placeholder="如：服务员"></div></div>
        <div class="form-row">
          <div class="field"><label>电话</label><input class="input" id="f_phone" value="${e ? (e.phone || '') : ''}"></div>
          <div class="field"><label>入职日期</label><input class="input" id="f_hire" value="${e ? (e.hireDate || '') : ''}" placeholder="2026-01-01"></div></div>`,
      onConfirm: () => {
        const payload = { name: document.getElementById('f_name').value.trim(), position: document.getElementById('f_position').value.trim(), phone: document.getElementById('f_phone').value.trim(), hireDate: document.getElementById('f_hire').value.trim() };
        if (!payload.name) { alert('请填写姓名'); return false; }
        return (id ? Api.saveEmployee(id, payload) : Api.saveEmployee(null, payload)).then(load);
      },
    });
  }
}

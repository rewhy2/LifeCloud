import { icon } from '../icons.js';
// categories.js —— 菜品分类管理
import { Api } from '../api.js';
import { openModal, closeModal } from '../components/modal.js';

export async function renderCategories(root) {
  root.innerHTML = `
    <div class="page">
      <div class="page-title">菜品分类管理</div>
      <div class="page-desc">管理菜品的一级与二级分类</div>
      <div class="toolbar" style="margin-top:16px"><div class="spacer"></div>
        <button class="btn btn-primary btn-sm" id="add">＋ 新增分类</button></div>
      <div class="table-wrap"><table class="table">
        <thead><tr><th>分类名称</th><th>排序</th><th style="text-align:right">操作</th></tr></thead>
        <tbody id="rows"><tr><td colspan="3"><div class="loading">加载中…</div></td></tr></tbody>
      </table></div>
    </div>`;
  const rows = root.querySelector('#rows');
  const load = async () => {
    const list = await Api.listCategories().catch(() => []);
    if (!list.length) { rows.innerHTML = `<tr><td colspan="3"><div class="empty"><div class="em-ic">${icon('menu')}</div><div class="em-txt">暂无分类</div></div></td></tr>`; return; }
    rows.innerHTML = list.map((c) => `<tr>
      <td class="cell-strong">${c.name}</td>
      <td class="mono">${c.sort ?? 0}</td>
      <td style="text-align:right"><div class="row-actions">
        <span class="act edit" data-edit="${c.id}">编辑</span>
        <span class="act del" data-del="${c.id}">删除</span></div></td></tr>`).join('');
    rows.querySelectorAll('[data-edit]').forEach((b) => (b.onclick = () => edit(b.dataset.edit)));
    rows.querySelectorAll('[data-del]').forEach((b) => (b.onclick = async () => { if (confirm('删除该分类？')) { await Api.deleteCategory(b.dataset.del); load(); } }));
  };
  root.querySelector('#add').onclick = () => edit(null);
  load();

  async function edit(id) {
    const c = id ? await Api.listCategories().then((l) => l.find((x) => String(x.id) === String(id))) : null;
    openModal({
      title: id ? '编辑分类' : '新增分类',
      body: `<div class="field"><label>分类名称</label><input class="input" id="f_name" value="${c ? c.name : ''}" placeholder="如：招牌主食"></div>
             <div class="field"><label>排序（越小越靠前）</label><input class="input" id="f_sort" type="number" value="${c ? (c.sort ?? 0) : 0}"></div>`,
      onConfirm: () => {
        const payload = { name: document.getElementById('f_name').value.trim(), sort: Number(document.getElementById('f_sort').value || 0) };
        if (!payload.name) { alert('请填写分类名称'); return false; }
        return (id ? Api.saveCategory(id, payload) : Api.saveCategory(null, payload)).then(() => load());
      },
    });
  }
}

import { icon } from '../icons.js';
// products.js —— 菜品库管理
import { Api } from '../api.js';
import { openModal, closeModal } from '../components/modal.js';

export async function renderProducts(root) {
  root.innerHTML = `
    <div class="page">
      <div class="page-title">菜品库管理</div>
      <div class="page-desc">管理菜品信息、价格与上下架状态</div>

      <div class="toolbar" style="margin-top:16px">
        <input class="input" id="kw" placeholder="搜索菜品名称…" style="min-width:220px">
        <select class="select" id="catFilter"><option value="">全部分类</option></select>
        <div class="spacer"></div>
        <button class="btn btn-outline btn-sm" id="refresh">⟳ 刷新</button>
        <button class="btn btn-primary btn-sm" id="add">＋ 新增菜品</button>
      </div>

      <div class="table-wrap">
        <table class="table">
          <thead><tr>
            <th>菜品</th><th>分类</th><th>价格</th><th>状态</th><th>描述</th><th style="text-align:right">操作</th>
          </tr></thead>
          <tbody id="rows"><tr><td colspan="6"><div class="loading">加载中…</div></td></tr></tbody>
        </table>
      </div>
    </div>`;

  const catSel = root.querySelector('#catFilter');
  const cats = await Api.listCategories().catch(() => []);
  catSel.innerHTML = '<option value="">全部分类</option>' + cats.map((c) => `<option value="${c.id}">${c.name}</option>`).join('');

  const rows = root.querySelector('#rows');
  const load = async () => {
    const kw = root.querySelector('#kw').value.trim();
    const cid = catSel.value;
    let list = await Api.listProducts().catch(() => []);
    if (cid) list = list.filter((p) => String(p.categoryId) === String(cid));
    if (kw) list = list.filter((p) => (p.name || '').includes(kw));
    if (!list.length) {
      rows.innerHTML = `<tr><td colspan="6"><div class="empty"><div class="em-ic">${icon('bowl')}</div><div class="em-txt">暂无菜品，点击右上角新增</div></div></td></tr>`;
      return;
    }
    const catName = (id) => (cats.find((c) => String(c.id) === String(id)) || {}).name || '—';
    rows.innerHTML = list.map((p) => `
      <tr>
        <td><div class="cell-strong">${p.name}</div></td>
        <td>${catName(p.categoryId)}</td>
        <td class="cell-strong mono">¥${p.price}</td>
        <td>${p.status === 1 ? '<span class="badge badge-ok">在售</span>' : '<span class="badge badge-mute">下架</span>'}</td>
        <td class="ellipsis" style="max-width:220px">${p.description || '<span class="muted">—</span>'}</td>
        <td style="text-align:right"><div class="row-actions">
          <span class="act edit" data-edit="${p.id}">编辑</span>
          <span class="act del" data-del="${p.id}">删除</span>
        </div></td>
      </tr>`).join('');
    rows.querySelectorAll('[data-edit]').forEach((b) => b.onclick = () => editProduct(b.dataset.edit));
    rows.querySelectorAll('[data-del]').forEach((b) => b.onclick = () => delProduct(b.dataset.del));
  };

  root.querySelector('#kw').oninput = debounce(load, 300);
  catSel.onchange = load;
  root.querySelector('#refresh').onclick = load;
  root.querySelector('#add').onclick = () => editProduct(null);
  load();

  async function editProduct(id) {
    const p = id ? await Api.getProduct(id).catch(() => null) : null;
    openModal({
      title: id ? '编辑菜品' : '新增菜品',
      body: `
        <div class="field"><label>菜品名称</label><input class="input" id="f_name" value="${p ? p.name : ''}" placeholder="如：招牌牛肉面"></div>
        <div class="form-row">
          <div class="field"><label>分类</label><select class="select" id="f_cat">${cats.map((c) => `<option value="${c.id}" ${p && String(p.categoryId) === String(c.id) ? 'selected' : ''}>${c.name}</option>`).join('')}</select></div>
          <div class="field"><label>价格（元）</label><input class="input" id="f_price" type="number" value="${p ? p.price : ''}" placeholder="0.00"></div>
        </div>
        <div class="field"><label>状态</label>
          <div class="switch-row"><div><div class="label-txt">上架销售</div><div class="label-sub">关闭后顾客端不可见</div></div>
            <label class="switch"><input type="checkbox" id="f_status" ${!p || p.status === 1 ? 'checked' : ''}><span class="track"></span></label></div>
        </div>
        <div class="field"><label>描述</label><textarea class="textarea" id="f_desc" placeholder="一句话介绍这道菜…">${p ? (p.description || '') : ''}</textarea></div>`,
      confirmText: id ? '保存' : '创建',
      onConfirm: async () => {
        const payload = {
          name: document.getElementById('f_name').value.trim(),
          categoryId: Number(document.getElementById('f_cat').value),
          price: Number(document.getElementById('f_price').value),
          status: document.getElementById('f_status').checked ? 1 : 0,
          description: document.getElementById('f_desc').value.trim(),
        };
        if (!payload.name) { alert('请填写菜品名称'); return false; }
        if (id) await Api.updateProduct(id, payload); else await Api.createProduct(payload);
        closeModal(); load();
      },
    });
  }

  async function delProduct(id) {
    if (!confirm('确认删除该菜品？')) return;
    await Api.deleteProduct(id).catch((e) => alert(e.message));
    load();
  }
}

function debounce(fn, d) { let t; return (...a) => { clearTimeout(t); t = setTimeout(() => fn(...a), d); }; }

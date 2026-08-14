import { icon } from '../icons.js';
// users.js —— 系统用户管理（平台管理员）
import { Api } from '../api.js';
import { openModal } from '../components/modal.js';

export async function renderUsers(root) {
  root.innerHTML = `
    <div class="page">
      <div class="page-title">系统用户管理</div>
      <div class="page-desc">管理平台账号与角色权限</div>
      <div class="toolbar" style="margin-top:16px"><div class="spacer"></div>
        <button class="btn btn-primary btn-sm" id="add">＋ 新建账号</button></div>
      <div class="table-wrap"><table class="table">
        <thead><tr><th>账号</th><th>姓名</th><th>角色</th><th>状态</th><th style="text-align:right">操作</th></tr></thead>
        <tbody id="rows"><tr><td colspan="5"><div class="loading">加载中…</div></td></tr></tbody>
      </table></div>
    </div>`;
  const rows = root.querySelector('#rows');
  const load = async () => {
    const list = await Api.listUsers().catch(() => []);
    if (!list.length) { rows.innerHTML = `<tr><td colspan="5"><div class="empty"><div class="em-ic">${icon('shield')}</div><div class="em-txt">暂无系统用户</div></div></td></tr>`; return; }
    rows.innerHTML = list.map((u) => `<tr>
      <td class="cell-strong">${u.username}</td>
      <td>${u.realName || '—'}</td>
      <td><span class="badge badge-info">${u.role || '—'}</span></td>
      <td>${u.enabled ? '<span class="badge badge-ok">启用</span>' : '<span class="badge badge-mute">停用</span>'}</td>
      <td style="text-align:right"><div class="row-actions"><span class="act del" data-del="${u.id}">删除</span></div></td></tr>`).join('');
    rows.querySelectorAll('[data-del]').forEach((b) => (b.onclick = async () => { if (confirm('删除该账号？')) { await Api.deleteUser(b.dataset.del); load(); } }));
  };
  root.querySelector('#add').onclick = () => openModal({
    title: '新建系统账号',
    body: `<div class="field"><label>账号</label><input class="input" id="f_u" placeholder="登录账号"></div>
      <div class="form-row"><div class="field"><label>密码</label><input class="input" id="f_p" type="password" placeholder="初始密码"></div>
      <div class="field"><label>角色</label><select class="select" id="f_r"><option value="ADMIN">管理员</option><option value="MERCHANT">商家</option></select></div></div>
      <div class="field"><label>姓名</label><input class="input" id="f_n" placeholder="真实姓名"></div>`,
    onConfirm: () => {
      const payload = { username: document.getElementById('f_u').value.trim(), password: document.getElementById('f_p').value, role: document.getElementById('f_r').value, realName: document.getElementById('f_n').value.trim() };
      if (!payload.username || !payload.password) { alert('请填写账号和密码'); return false; }
      return Api.createUser(payload).then(load);
    },
  });
  load();
}

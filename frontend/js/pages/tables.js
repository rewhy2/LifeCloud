import { icon } from '../icons.js';
// tables.js —— 桌台管理
import { Api } from '../api.js';

const ST = { FREE: ['空闲', 'badge-ok'], OCCUPIED: ['占用', 'badge-warn'], RESERVED: ['已预订', 'badge-info'], CLEANING: ['清洁中', 'badge-mute'] };

export async function renderTables(root) {
  root.innerHTML = `
    <div class="page">
      <div class="page-title">桌台管理</div>
      <div class="page-desc">门店桌位状态总览</div>
      <div class="grid cols-4" style="margin-top:18px" id="grid"><div class="skeleton card" style="height:120px"></div></div>
    </div>`;
  const grid = root.querySelector('#grid');
  const load = async () => {
    const list = await Api.listTables().catch(() => []);
    if (!list.length) { grid.innerHTML = `<div class="card empty" style="grid-column:1/-1"><div class="em-ic">${icon('table')}</div><div class="em-txt">暂无桌台</div></div>`; return; }
    grid.innerHTML = list.map((t) => {
      const [label, cls] = ST[t.status] || ['未知', 'badge-mute'];
      return `<div class="card hover" style="text-align:center">
        <div style="font-size:22px">${icon('table')}</div>
        <div class="cell-strong" style="font-size:16px;margin-top:6px">${t.name || '桌' + t.id}</div>
        <div class="muted" style="font-size:12px">${t.seats ? t.seats + ' 座' : ''}</div>
        <div style="margin-top:10px"><span class="badge ${cls}">${label}</span></div>
      </div>`;
    }).join('');
  };
  load();
}

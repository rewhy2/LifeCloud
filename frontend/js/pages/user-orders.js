import { icon } from '../icons.js';
// user-orders.js —— 顾客订单
import { Api } from '../api.js';

const STATUS = { PENDING: ['待处理', 'badge-warn'], PREPARING: ['制作中', 'badge-info'], DONE: ['已完成', 'badge-ok'], CANCELLED: ['已取消', 'badge-mute'] };

export async function renderUserOrders(root) {
  root.innerHTML = `
    <div class="page">
      <div class="page-title">我的订单</div>
      <div class="page-desc">查看你的点餐与配送记录</div>
      <div class="grid cols-2" style="margin-top:18px" id="list"><div class="skeleton card" style="height:140px"></div></div>
    </div>`;
  const list = root.querySelector('#list');
  const orders = await Api.myOrders().catch(() => []);
  if (!orders.length) { list.innerHTML = `<div class="card empty" style="grid-column:1/-1"><div class="em-ic">${icon('receipt')}</div><div class="em-txt">还没有订单，去点餐吧～</div></div>`; return; }
  list.innerHTML = orders.map((o) => {
    const [label, cls] = STATUS[o.status] || ['未知', 'badge-mute'];
    const items = (o.items || []).map((i) => `${i.name} ×${i.qty}`).join('、') || '—';
    return `<div class="card hover">
      <div style="display:flex;justify-content:space-between;align-items:center">
        <div class="cell-strong mono">#${o.id}</div>
        <span class="badge ${cls}">${label}</span>
      </div>
      <div class="muted" style="font-size:12.5px;margin:8px 0">${items}</div>
      <div style="display:flex;justify-content:space-between;align-items:center">
        <div class="cell-sub">${o.createdAt || ''}</div>
        <div class="dish-price" style="font-size:16px">¥${o.amount}</div>
      </div>
    </div>`;
  }).join('');
}

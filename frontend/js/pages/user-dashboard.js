import { icon } from '../icons.js';
// user-dashboard.js —— 顾客端首页
import { Api } from '../api.js';

export async function renderUserDashboard(root) {
  root.innerHTML = `
    <div class="page">
      <div class="user-hero">
        <div>
          <h2>欢迎光临 ${icon('bowl')}</h2>
          <p>新鲜现做 · 30 分钟必达 · 新客立减优惠进行中</p>
        </div>
        <div style="font-size:54px">${icon('bowl')}</div>
      </div>

      <div class="grid cols-3" style="margin-bottom:20px">
        <div class="card hover" style="cursor:pointer" data-go="#/menu"><div class="ic-badge" style="background:#ff7a3c22;color:#ff5a1f">${icon('bowl')}</div><div class="label">浏览菜单</div><div class="muted" style="font-size:12px;margin-top:4px">挑你喜欢的</div></div>
        <div class="card hover" style="cursor:pointer" data-go="#/orders"><div class="ic-badge" style="background:#3b82f622;color:#3b82f6">${icon('receipt')}</div><div class="label">我的订单</div><div class="muted" style="font-size:12px;margin-top:4px">查看配送进度</div></div>
        <div class="card hover" style="cursor:pointer" data-go="#/chat"><div class="ic-badge" style="background:#1bbf8322;color:#1bbf83">${icon('bot')}</div><div class="label">AI 助手</div><div class="muted" style="font-size:12px;margin-top:4px">有问必答</div></div>
      </div>

      <div class="card">
        <div class="card-head"><div class="ttl"><span class="ic">${icon('flame')}</span>本店招牌</div><div class="more" data-go="#/menu">查看全部 →</div></div>
        <div class="dish-grid" id="hot"><div class="skeleton" style="height:240px;border-radius:18px"></div></div>
      </div>
    </div>`;

  root.querySelectorAll('[data-go]').forEach((el) => (el.onclick = () => (location.hash = el.dataset.go)));
  const hot = root.querySelector('#hot');
  const list = await Api.listProducts().then((r) => r.filter((p) => p.status === 1).slice(0, 4)).catch(() => []);
  if (!list.length) { hot.innerHTML = '<div class="muted">暂无菜品</div>'; return; }
  hot.innerHTML = list.map((p) => `
    <div class="dish-card">
      <div class="dish-thumb">${icon('bowl')}</div>
      <div class="dish-body">
        <div class="dish-name">${p.name}</div>
        <div class="dish-desc">${p.description || ''}</div>
        <div class="dish-foot"><div class="dish-price">¥${p.price}<span class="unit">元</span></div></div>
      </div>
    </div>`).join('');
}

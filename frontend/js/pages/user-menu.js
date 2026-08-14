import { icon } from '../icons.js';
// user-menu.js —— 顾客端点餐 + 购物车
import { Api } from '../api.js';

let cart = {};

export async function renderUserMenu(root) {
  root.innerHTML = `
    <div class="page">
      <div class="page-title">菜单</div>
      <div class="page-desc">挑选心仪菜品，加入购物车结算</div>
      <div class="cat-bar" id="catBar"></div>
      <div class="dish-grid" id="grid"><div class="skeleton" style="height:240px;border-radius:18px"></div></div>
    </div>`;

  const cats = await Api.publicCategories().catch(() => []);
  const catBar = root.querySelector('#catBar');
  catBar.innerHTML = `<div class="cat-chip active" data-c="0">全部</div>` + cats.map((c) => `<div class="cat-chip" data-c="${c.id}">${c.name}</div>`).join('');
  const grid = root.querySelector('#grid');

  const load = async (cid) => {
    let list = await Api.publicProducts().catch(() => []);
    if (cid) list = list.filter((p) => String(p.categoryId) === String(cid));
    grid.innerHTML = list.map((p) => `
      <div class="dish-card">
        <div class="dish-thumb">${icon('bowl')}</div>
        <div class="dish-body">
          <div class="dish-name">${p.name}</div>
          <div class="dish-desc">${p.description || ''}</div>
          <div class="dish-foot">
            <div class="dish-price">¥${p.price}<span class="unit">元</span></div>
            <div class="dish-add" data-add="${p.id}" data-name="${p.name}" data-price="${p.price}">＋</div>
          </div>
        </div>
      </div>`).join('');
    grid.querySelectorAll('[data-add]').forEach((b) => (b.onclick = () => add(b.dataset)));
  };
  catBar.querySelectorAll('.cat-chip').forEach((c) => (c.onclick = () => {
    catBar.querySelectorAll('.cat-chip').forEach((x) => x.classList.remove('active'));
    c.classList.add('active');
    load(c.dataset.c === '0' ? null : c.dataset.c);
  }));
  load(null);

  function add(d) {
    const id = d.add;
    cart[id] = cart[id] || { name: d.name, price: Number(d.price), qty: 0 };
    cart[id].qty++;
    syncFab();
  }
  function syncFab() {
    const fab = document.getElementById('cartFab');
    const count = Object.values(cart).reduce((a, b) => a + b.qty, 0);
    document.getElementById('cartCount').textContent = count;
    fab.style.display = count ? 'inline-flex' : 'none';
  }
  const fab = document.getElementById('cartFab');
  fab.onclick = openCart;
  syncFab();

  function openCart() {
    const items = Object.entries(cart);
    const total = items.reduce((a, [, v]) => a + v.price * v.qty, 0);
    const mask = document.createElement('div');
    mask.className = 'modal-mask';
    mask.innerHTML = `
      <div class="cart-drawer" role="dialog">
        <div class="cart-head"><div class="ttl">${icon('cart')} 购物车</div><div class="x" data-close>${icon('close', { size: 16 })}</div></div>
        <div class="cart-list">
          ${items.length ? items.map(([id, v]) => `
            <div class="cart-item">
              <div><div class="ci-name">${v.name}</div><div class="ci-price">¥${v.price}</div></div>
              <div class="ci-right">
                <div class="qty"><button data-sub="${id}">−</button><span>${v.qty}</span><button data-inc="${id}">＋</button></div>
              </div>
            </div>`).join('') : '<div class="empty" style="padding:40px"><div class="em-ic">${icon('cart')}</div><div class="em-txt">购物车是空的</div></div>'}
        </div>
        <div class="cart-foot">
          <div class="cart-total"><span class="t">合计</span><span class="v">¥${total.toFixed(2)}<span class="unit">元</span></span></div>
          <button class="btn btn-primary btn-block" id="checkout" ${items.length ? '' : 'disabled'}>提交订单</button>
        </div>
      </div>`;
    document.body.appendChild(mask);
    mask.querySelector('[data-close]').onclick = () => mask.remove();
    mask.onclick = (e) => { if (e.target === mask) mask.remove(); };
    mask.querySelectorAll('[data-inc]').forEach((b) => (b.onclick = () => { cart[b.dataset.inc].qty++; mask.remove(); openCart(); syncFab(); }));
    mask.querySelectorAll('[data-sub]').forEach((b) => (b.onclick = () => { if (--cart[b.dataset.sub].qty <= 0) delete cart[b.dataset.sub]; mask.remove(); openCart(); syncFab(); }));
    const co = mask.querySelector('#checkout');
    if (co) co.onclick = async () => {
      co.disabled = true; co.innerHTML = '<span class="spinner"></span>';
      try {
        await Api.createOrder({ items: items.map(([id, v]) => ({ productId: Number(id), qty: v.qty })) });
        cart = {}; syncFab(); mask.remove();
        location.hash = '#/orders';
      } catch (e) { alert(e.message || '下单失败'); co.disabled = false; co.textContent = '提交订单'; }
    };
  }
}

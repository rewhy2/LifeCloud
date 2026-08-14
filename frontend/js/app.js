// ============================================================
// app.js —— 应用入口：登录 / 工作台框架 / 角色路由
// ============================================================
import { Api } from './api.js';
import { Auth, ROLE_LABEL, currentMenus, Theme } from './store.js';
import { icon } from './icons.js';
import { renderChat } from './pages/chat.js';
import { renderDashboard } from './pages/dashboard.js';
import { renderProducts } from './pages/products.js';
import { renderUsers } from './pages/users.js';
import { renderOrders } from './pages/orders.js';
import { renderTables } from './pages/tables.js';
import { renderMembers } from './pages/members.js';
import { renderCoupons } from './pages/coupons.js';
import { renderInventory } from './pages/inventory.js';
import { renderStaff } from './pages/staff.js';
import { renderBusiness } from './pages/business.js';
import { renderCategories } from './pages/categories.js';
import { renderSuppliers } from './pages/suppliers.js';
import { renderPurchases } from './pages/purchases.js';
import { renderEmployees } from './pages/employees.js';
import { renderFinance } from './pages/finance.js';
import { renderSettings } from './pages/settings.js';

// —— 角色预设：联动默认账号与提示 ——
const ROLE_PRESET = {
  ADMIN:    { hint: '使用平台管理员账号登录',            user: 'admin',         pass: 'admin123' },
  MERCHANT: { hint: '使用商家账号登录',                  user: 'merchant',      pass: 'merchant123' },
  USER:     { hint: '使用顾客账号登录（或先去注册）',     user: 'demo_customer', pass: '123456' },
};

let mode = 'login'; // login | register

// —— 登录 / 注册表单（渲染到 #authBox）——
function renderAuth() {
  const box = document.querySelector('#authBox');
  const isRegister = mode === 'register';
  const preset = ROLE_PRESET[Auth.role || 'ADMIN'];
  box.innerHTML = `
    <h2>${isRegister ? '创建顾客账号' : '欢迎回来'}</h2>
    <p class="sub">${isRegister ? '注册后即可在顾客端点餐、领券、查订单' : '请选择身份并登录到工作台'}</p>
    <div class="field">
      <label>登录身份</label>
      <div class="role-seg" id="roleSeg">
        <div class="role-opt ${Auth.role === 'ADMIN' || !Auth.role ? 'active' : ''}" data-role="ADMIN">${icon('shield', { size: 16 })}管理员</div>
        <div class="role-opt ${Auth.role === 'MERCHANT' ? 'active' : ''}" data-role="MERCHANT">${icon('shop', { size: 16 })}商家</div>
        <div class="role-opt ${Auth.role === 'USER' ? 'active' : ''}" data-role="USER">${icon('user', { size: 16 })}顾客</div>
      </div>
    </div>
    <form id="loginForm" autocomplete="off">
      <div class="field">
        <label>账号</label>
        <input type="text" id="username" value="${isRegister ? '' : preset.user}" placeholder="请输入账号" />
      </div>
      <div class="field">
        <label>密码</label>
        <input type="password" id="password" value="${isRegister ? '' : preset.pass}" placeholder="请输入密码" />
      </div>
      <button class="btn-primary" id="loginBtn" type="submit">
        <span id="loginBtnText">${isRegister ? '注 册' : '登 录'}</span>
      </button>
    </form>
    <div class="hint" id="switchLine">
      ${isRegister ? '已有账号？<a class="link" id="toggleMode">返回登录</a>' : '还没有顾客账号？<a class="link" id="toggleMode">立即注册</a>'}
    </div>`;

  bindAuthEvents();
}

function bindAuthEvents() {
  const roleSeg = document.querySelector('#roleSeg');
  if (roleSeg) {
    roleSeg.querySelectorAll('.role-opt').forEach((el) => {
      el.addEventListener('click', () => {
        roleSeg.querySelectorAll('.role-opt').forEach((x) => x.classList.remove('active'));
        el.classList.add('active');
        const role = el.dataset.role;
        const u = document.querySelector('#username');
        const p = document.querySelector('#password');
        if (role !== 'USER' && mode === 'register') { mode = 'login'; renderAuth(); return; }
        const preset = ROLE_PRESET[role];
        if (u && mode !== 'register') u.value = preset.user;
        if (p && mode !== 'register') p.value = preset.pass;
      });
    });
  }
  const toggle = document.querySelector('#toggleMode');
  if (toggle) toggle.addEventListener('click', () => { mode = mode === 'login' ? 'register' : 'login'; renderAuth(); });

  const form = document.querySelector('#loginForm');
  if (form) form.addEventListener('submit', onAuth);
}

async function onAuth(e) {
  e.preventDefault();
  const roleEl = document.querySelector('#roleSeg .role-opt.active');
  const role = roleEl ? roleEl.dataset.role : 'ADMIN';
  const u = document.querySelector('#username').value.trim();
  const p = document.querySelector('#password').value.trim();
  if (!u || !p) { hintFlash('请填写账号和密码'); return; }
  const btn = document.querySelector('#loginBtn');
  const btnText = document.querySelector('#loginBtnText');
  btn.disabled = true;
  btnText.innerHTML = '<span class="spinner"></span>';
  try {
    if (mode === 'register') {
      await Api.register(u, p, u);
      const data = await Api.login(u, p);
      Auth.set(data.token, data.username || u, data.role);
      location.href = '/user.html';
      return;
    }
    const data = await Api.login(u, p);
    Auth.set(data.token, data.username || u, data.role);
    if (data.role === 'USER') { location.href = '/user.html'; return; }
    enterApp();
  } catch (err) {
    hintFlash(err.message || '操作失败');
  } finally {
    btn.disabled = false;
    btnText.textContent = mode === 'register' ? '注 册' : '登 录';
  }
}

function hintFlash(msg) {
  const line = document.querySelector('#switchLine');
  const old = line.innerHTML;
  line.style.color = 'var(--danger)';
  line.innerHTML = msg;
  setTimeout(() => { line.style.color = ''; renderAuth(); }, 1800);
}

// —— 主题切换（登录页按钮）——
document.querySelectorAll('[data-theme-toggle]').forEach((btn) => {
  btn.addEventListener('click', () => { Theme.toggle(); });
});
Theme.init();

// —— 进入工作台 ——
function enterApp() {
  const loginPage = document.querySelector('.auth-wrap');
  if (loginPage) loginPage.remove();

  const menus = currentMenus();
  const roleName = ROLE_LABEL[Auth.role] || '用户';
  const initial = (Auth.username || 'A').slice(0, 1).toUpperCase();

  const shell = document.createElement('div');
  shell.className = 'admin';
  shell.innerHTML = `
    <aside class="sidebar">
      <div class="brand">
        <span class="logo">${icon('bowl', { size: 20 })}</span><span>智膳餐饮云</span>
      </div>
      <nav id="nav"></nav>
      <div class="nav-foot">
        <div class="usr">
          <span class="av">${initial}</span>
          <div><div class="nm">${Auth.username || ''}</div><div class="rl">${roleName}</div></div>
          <span class="logout" id="logoutBtn" title="退出登录" style="margin-left:auto;cursor:pointer;color:var(--ink-500)">${icon('logout', { size: 18 })}</span>
        </div>
      </div>
    </aside>
    <div class="main">
      <header class="topbar">
        <div class="ttl" id="crumb"></div>
        <div class="spacer"></div>
        <div class="pill" id="bizPill"><span class="dot ok"></span><span id="bizText">营业中</span></div>
        <button class="pill theme-toggle-pill" id="themeBtn" data-theme-toggle type="button">
          <span class="th-ic"></span><span class="theme-label">主题</span>
        </button>
      </header>
      <div class="page" id="content"></div>
    </div>`;
  document.body.appendChild(shell);

  document.querySelector('#logoutBtn').addEventListener('click', () => { Auth.logout(); location.reload(); });
  document.querySelector('#themeBtn').addEventListener('click', () => Theme.toggle());

  renderNav();
  route(menus[0].id);
  syncBizPill();
}

// 营业状态 pill
async function syncBizPill() {
  try {
    const s = await Api.getBusinessStatus();
    const open = s && s.open;
    document.querySelector('#bizText').textContent = open ? '营业中' : '已打烊';
    document.querySelector('#bizPill').querySelector('.dot').className = 'dot ' + (open ? 'ok' : '');
  } catch { /* 忽略 */ }
}

let currentMenuId = null;

// —— 侧边导航（按一级分组）——
function renderNav() {
  const nav = document.querySelector('#nav');
  const menus = currentMenus();
  const groups = {};
  menus.forEach((m) => { const g = m.crumb[0]; (groups[g] = groups[g] || []).push(m); });
  nav.innerHTML = Object.entries(groups).map(([g, items]) => `
    <div class="nav-group">${g}</div>
    ${items.map((m) => `<div class="nav-item" data-menu="${m.id}">${icon(m.icon, { size: 19 })}<span>${m.label}</span></div>`).join('')}
  `).join('');
  nav.querySelectorAll('.nav-item').forEach((el) =>
    el.addEventListener('click', () => route(el.dataset.menu)));
}

// —— 路由分发 ——
function route(id) {
  const menus = currentMenus();
  const menu = menus.find((m) => m.id === id) || menus[0];
  currentMenuId = menu.id;
  document.querySelector('#crumb').textContent = menu.title;
  document.querySelectorAll('.nav-item').forEach((el) => el.classList.toggle('active', el.dataset.menu === id));

  const dispatch = {
    chat: renderChat, dashboard: renderDashboard, products: renderProducts,
    users: renderUsers, orders: renderOrders, tables: renderTables, members: renderMembers,
    coupons: renderCoupons, inventory: renderInventory, staff: renderStaff, business: renderBusiness,
    categories: renderCategories, suppliers: renderSuppliers, purchases: renderPurchases,
    employees: renderEmployees, finance: renderFinance, settings: renderSettings,
  };
  const content = document.querySelector('#content');
  content.scrollTop = 0;
  (dispatch[id] || renderDashboard)(content);
}

// —— 启动：已登录直接进，否则展示登录页 ——
if (Auth.isLogin()) {
  Api.me().then(() => enterApp()).catch(() => { Auth.logout(); renderAuth(); });
} else {
  renderAuth();
}

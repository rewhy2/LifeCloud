// user-app.js —— 顾客端入口：路由 / 顶栏 / 购物车浮标
import { Auth, Theme } from './store.js';
import { renderUserDashboard } from './pages/user-dashboard.js';
import { renderUserMenu } from './pages/user-menu.js';
import { renderUserOrders } from './pages/user-orders.js';
import { renderChat } from './pages/chat.js';

const view = document.getElementById('view');

function activeNav() {
  const hash = location.hash || '#/dashboard';
  const map = { '#/dashboard': 'navDash', '#/menu': 'navMenu', '#/orders': 'navOrders', '#/chat': 'navChat' };
  ['navMenu', 'navOrders', 'navChat', 'navDash'].forEach((id) => {
    const el = document.getElementById(id);
    if (el) el.style.background = id === map[hash] ? 'var(--brand-50)' : '';
    if (el) el.style.color = id === map[hash] ? 'var(--brand-600)' : '';
  });
}

const routes = {
  '#/dashboard': renderUserDashboard,
  '#/menu': renderUserMenu,
  '#/orders': renderUserOrders,
  '#/chat': renderChat,
};

function route() {
  activeNav();
  const r = routes[location.hash] || renderUserDashboard;
  r(view);
}

['navDash', 'navMenu', 'navOrders', 'navChat'].forEach((id) => {
  const el = document.getElementById(id);
  if (el) el.onclick = () => { const h = { navDash: '#/dashboard', navMenu: '#/menu', navOrders: '#/orders', navChat: '#/chat' }[id]; location.hash = h; };
});
document.getElementById('logout').onclick = () => { Auth.logout(); location.href = '/'; };
window.addEventListener('hashchange', route);

// —— 主题切换（顾客端）——
const themeBtn = document.getElementById('themeBtn');
if (themeBtn) themeBtn.addEventListener('click', () => Theme.toggle());
Theme.init();

if (!Auth.isLogin() || Auth.role !== 'USER') { location.href = '/'; }
else route();

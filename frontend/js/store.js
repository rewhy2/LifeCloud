// ============================================================
// store.js —— 全局状态：登录态、主题、角色菜单定义
// ============================================================
import { Api } from './api.js';
import { icon } from './icons.js';

// —— 角色中文名 ——
export const ROLE_LABEL = { ADMIN: '平台管理员', MERCHANT: '商家', USER: '顾客' };

// —— 主题：light / dark / auto（跟随系统），持久化到 localStorage ——
// auto 模式下实时监听系统 prefers-color-scheme 变化
export const Theme = {
  _mq: null,
  // 用户选择的模式：'auto' | 'light' | 'dark'
  getMode() { return localStorage.getItem('theme_mode') || localStorage.getItem('theme-mode') || 'auto'; },
  // 最终生效的主题：'light' | 'dark'
  getResolved() {
    const mode = this.getMode();
    if (mode === 'auto') {
      const prefersDark = window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches;
      return prefersDark ? 'dark' : 'light';
    }
    return mode;
  },
  setMode(mode) {
    localStorage.setItem('theme_mode', mode);
    localStorage.removeItem('theme-mode');
    this.apply();
  },
  // 根据当前模式把真实主题写到 <html data-theme>，并刷新按钮状态
  apply() {
    const resolved = this.getResolved();
    document.documentElement.setAttribute('data-theme', resolved);
    document.body.setAttribute('data-theme', resolved);
    document.querySelectorAll('[data-theme-toggle]').forEach((btn) => this.paintBtn(btn));
  },
  toggle() {
    const order = ['auto', 'light', 'dark'];
    const next = order[(order.indexOf(this.getMode()) + 1) % order.length];
    this.setMode(next);
    return next;
  },
  // 渲染一个切换按钮的图标 / 文案（用线性 SVG，不依赖 emoji）
  paintBtn(btn) {
    const mode = this.getMode();
    const map = {
      auto:  { ic: 'spark',   label: '跟随系统' },
      light: { ic: 'cup',     label: '浅色' },
      dark:  { ic: 'star',    label: '深色' },
    };
    const m = map[mode];
    const icSlot = btn.querySelector('.th-ic') || btn;
    if (btn.querySelector('.th-ic')) btn.querySelector('.th-ic').innerHTML = icon(m.ic, { size: 17 });
    else if (btn.dataset.themeToggle !== undefined || true) {
      // 登录页 / 顾客端按钮：直接填充 SVG
      const labelEl = btn.querySelector('.theme-label');
      btn.innerHTML = icon(m.ic, { size: 17 }) + (labelEl ? `<span class="theme-label">${m.label}</span>` : '');
    }
    btn.title = '主题：' + m.label + '（点击切换）';
    btn.setAttribute('aria-label', '主题切换');
  },
  // 初始化：应用主题 + 注册系统主题变化监听（仅 auto 模式需要）
  init() {
    this.apply();
    if (!this._mq && window.matchMedia) {
      this._mq = window.matchMedia('(prefers-color-scheme: dark)');
      const onChange = () => { if (this.getMode() === 'auto') this.apply(); };
      if (this._mq.addEventListener) this._mq.addEventListener('change', onChange);
      else if (this._mq.addListener) this._mq.addListener(onChange); // 旧版 Safari
    }
  },
};

// —— 登录态（含角色）——
export const Auth = {
  token: localStorage.getItem('token') || '',
  username: localStorage.getItem('username') || '',
  role: localStorage.getItem('role') || '',
  isLogin() { return !!this.token; },
  set(token, username, role) {
    this.token = token; this.username = username; this.role = role || '';
    localStorage.setItem('token', token);
    localStorage.setItem('username', username);
    localStorage.setItem('role', this.role);
  },
  logout() {
    this.token = ''; this.username = ''; this.role = '';
    localStorage.removeItem('token'); localStorage.removeItem('username'); localStorage.removeItem('role');
  },
};

// —— 角色专属菜单 ——
// 公共：AI 对话 / 数据大盘 / 菜品管理
// 管理员附加：系统用户管理、会员管理、优惠券管理、库存采购、员工排班、营业状态
// 商家：在公共基础上附加 订单管理、桌台管理、会员、优惠券、库存采购、员工排班、营业状态
export const MENUS = {
  ADMIN: [
    { id: 'chat', label: 'AI 智能对话', icon: 'chat', title: 'AI 智能对话', crumb: ['智能助手', 'AI 对话'] },
    { id: 'dashboard', label: '全局经营大盘', icon: 'chart', title: '全局经营大盘', crumb: ['数据洞察', '经营大盘'] },
    { id: 'finance', label: '财务对账', icon: 'wallet', title: '财务对账', crumb: ['财务', '对账汇总'] },
    { id: 'users', label: '系统用户管理', icon: 'shield', title: '系统用户管理', crumb: ['平台运营', '系统用户'] },
    { id: 'products', label: '菜品库管理', icon: 'bowl', title: '菜品库管理', crumb: ['商品运营', '菜品库'] },
    { id: 'categories', label: '菜品分类管理', icon: 'menu', title: '菜品分类管理', crumb: ['商品运营', '分类'] },
    { id: 'suppliers', label: '供应商管理', icon: 'supplier', title: '供应商管理', crumb: ['供应链', '供应商'] },
    { id: 'purchases', label: '采购管理', icon: 'receipt', title: '采购管理', crumb: ['供应链', '采购单'] },
    { id: 'members', label: '会员管理', icon: 'users', title: '会员管理', crumb: ['会员运营', '会员'] },
    { id: 'coupons', label: '优惠券管理', icon: 'tag', title: '优惠券管理', crumb: ['营销', '优惠券'] },
    { id: 'inventory', label: '库存管理', icon: 'box', title: '库存管理', crumb: ['供应链', '库存'] },
    { id: 'employees', label: '员工花名册', icon: 'user', title: '员工花名册', crumb: ['人事', '员工'] },
    { id: 'staff', label: '员工排班', icon: 'calendar', title: '员工排班', crumb: ['人事', '排班'] },
    { id: 'business', label: '营业状态', icon: 'shop', title: '营业状态', crumb: ['门店', '营业状态'] },
    { id: 'settings', label: '系统设置', icon: 'gear', title: '系统设置', crumb: ['平台', '设置'] },
  ],
  MERCHANT: [
    { id: 'dashboard', label: '本店数据大盘', icon: 'chart', title: '本店数据大盘', crumb: ['数据洞察', '经营大盘'] },
    { id: 'finance', label: '财务对账', icon: 'wallet', title: '财务对账', crumb: ['财务', '对账汇总'] },
    { id: 'products', label: '菜品管理', icon: 'bowl', title: '菜品管理', crumb: ['商品运营', '菜品管理'] },
    { id: 'categories', label: '菜品分类管理', icon: 'menu', title: '菜品分类管理', crumb: ['商品运营', '分类'] },
    { id: 'orders', label: '订单管理', icon: 'receipt', title: '订单管理', crumb: ['交易', '订单'] },
    { id: 'tables', label: '桌台管理', icon: 'table', title: '桌台管理', crumb: ['门店', '桌台'] },
    { id: 'members', label: '会员管理', icon: 'users', title: '会员管理', crumb: ['会员运营', '会员'] },
    { id: 'coupons', label: '优惠券管理', icon: 'tag', title: '优惠券管理', crumb: ['营销', '优惠券'] },
    { id: 'suppliers', label: '供应商管理', icon: 'supplier', title: '供应商管理', crumb: ['供应链', '供应商'] },
    { id: 'purchases', label: '采购管理', icon: 'receipt', title: '采购管理', crumb: ['供应链', '采购单'] },
    { id: 'inventory', label: '库存管理', icon: 'box', title: '库存管理', crumb: ['供应链', '库存'] },
    { id: 'employees', label: '员工花名册', icon: 'user', title: '员工花名册', crumb: ['人事', '员工'] },
    { id: 'staff', label: '员工排班', icon: 'calendar', title: '员工排班', crumb: ['人事', '排班'] },
    { id: 'business', label: '营业状态', icon: 'shop', title: '营业状态', crumb: ['门店', '营业状态'] },
  ],
  USER: [],
};

export function currentMenus() {
  return MENUS[Auth.role] || MENUS.MERCHANT;
}

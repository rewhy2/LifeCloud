// icons.js —— 极简线性图标（taste 风格：1.6px 描边 / 圆角 / 无填充 / 几何感）
// 替换原先用 emoji 充当图标的所有位置，消除 AI 模板痕迹。
// 用法：
//   icon('bowl')                       -> <svg ...> 默认 20px
//   icon('flame', { size: 18 })        -> 指定尺寸
//   icon('bot', { size: 22, color: 'var(--brand)' })
// 在模板字符串里直接 ${icon('bowl')} 即可。

const P = (d) =>
  `M12 3a9 9 0 1 0 0 18 9 9 0 0 0 0-18Zm0 0c2.5 2.5 3.5 6 0 9-3.5-3-2.5-6.5 0-9Z`;

const PATHS = {
  // 餐饮
  bowl: 'M3 11h18a8 8 0 0 1-8 8h-2a8 8 0 0 1-8-8Zm4 0c.8 1.6 2 2.4 5 2.4S16.2 12.6 17 11M3 11V8.5M21 11V8.5',
  plate: 'M12 3a9 9 0 1 0 0 18 9 9 0 0 0 0-18Zm0 4a5 5 0 1 0 0 10 5 5 0 0 0 0-10Z',
  menu: 'M4 6h16M4 12h16M4 18h10',
  cart: 'M3 4h2l2.2 11.2a1.5 1.5 0 0 0 1.5 1.3h8.1a1.5 1.5 0 0 0 1.5-1.2L21 8H6M9 21a1 1 0 1 0 0-2 1 1 0 0 0 0 2Zm9 0a1 1 0 1 0 0-2 1 1 0 0 0 0 2Z',
  receipt: 'M5 3h14v18l-3-2-3 2-3-2-3 2V3Zm4 6h6M9 12h6M9 15h4',
  flame: 'M12 3c1 3-2 4-2 7a2 2 0 0 0 4 0c0-1 0-2-1-3 2 1 4 3 4 6a5 5 0 1 1-10 0c0-4 3-6 5-10Z',
  // 经营 / 数据
  chart: 'M4 20V10M10 20V4M16 20v-7M22 20H2',
  grid: 'M4 4h7v7H4zM13 4h7v7h-7zM4 13h7v7H4zM13 13h7v7h-7z',
  report: 'M6 3h9l4 4v14H6zM14 3v5h5M9 13h6M9 17h6',
  wallet: 'M3 7h15a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H3zM3 7v10M3 7a2 2 0 0 1 2-2h11M16 13h.01',
  tag: 'M3 12l9-9h6a3 3 0 0 1 3 3v6l-9 9-9-9Zm7 0a1.5 1.5 0 1 0 0-3 1.5 1.5 0 0 0 0 3Z',
  users: 'M9 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8Zm-5 9a7 7 0 0 1 14 0M16 11a4 4 0 0 0 0-8M18 20a6 6 0 0 0-3-5.2',
  user: 'M12 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8Zm-8 9a8 8 0 0 1 16 0',
  box: 'M3 8l9-5 9 5v8l-9 5-9-5zM3 8l9 5 9-5M12 13v8',
  truck: 'M3 6h11v9H3zM14 9h4l3 3v3h-7zM7 18a1.5 1.5 0 1 0 0-3 1.5 1.5 0 0 0 0 3Zm10 0a1.5 1.5 0 1 0 0-3 1.5 1.5 0 0 0 0 3Z',
  supplier: 'M4 21V8l8-4 8 4v13M9 21v-6h6v6',
  clock: 'M12 3a9 9 0 1 0 0 18 9 9 0 0 0 0-18Zm0 4v5l3 2',
  calendar: 'M4 5h16v15H4zM4 9h16M8 3v4M16 3v4',
  table: 'M3 6h18M3 12h18M3 18h18M9 6v12M15 6v12',
  // 系统
  bot: 'M12 3a3 3 0 0 1 3 3v2h2a2 2 0 0 1 2 2v6a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2v-6a2 2 0 0 1 2-2h2V6a3 3 0 0 1 3-3Zm-4 9v2M16 12v2M10.5 7h3',
  gear: 'M12 9a3 3 0 1 0 0 6 3 3 0 0 0 0-6Zm0-6v3M12 18v3M4.5 7.5 7 9M17 15l2.5 1.5M4.5 16.5 7 15M17 9l2.5-1.5',
  logout: 'M15 4h3a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2h-3M10 12H3M6 8l-4 4 4 4',
  send: 'M4 12l16-8-6 16-3-7-7-1Z',
  search: 'M11 4a7 7 0 1 0 0 14 7 7 0 0 0 0-14Zm9 16-4.5-4.5',
  plus: 'M12 5v14M5 12h14',
  close: 'M6 6l12 12M18 6 6 18',
  edit: 'M4 20h4L19 9l-4-4L4 16v4Z',
  trash: 'M4 7h16M9 7V4h6v3M6 7l1 13h10l1-13',
  check: 'M5 12l5 5 9-10',
  warn: 'M12 3 2 20h20L12 3Zm0 7v4M12 16v.5',
  star: 'M12 3l2.7 5.6 6.1.9-4.4 4.3 1 6.1L12 17.8 6.6 20l1-6.1L3.2 9.5l6.1-.9L12 3Z',
  chat: 'M4 5h16v11H9l-4 3v-3H4z',
  cup: 'M5 8h11v5a4 4 0 0 1-4 4H9a4 4 0 0 1-4-4V8Zm11 1h3a2 2 0 0 1 0 5h-3M8 3v2M11 3v2',
  leaf: 'M5 19c0-8 6-13 14-14 0 9-5 14-14 14Zm0 0c2-4 5-6 9-7',
  spark: 'M12 3v6M12 15v6M3 12h6M15 12h6',
  shield: 'M12 3l8 3v5c0 5-3.5 8.5-8 10-4.5-1.5-8-5-8-10V6l8-3Zm-3 8 2 2 4-4',
  bell: 'M6 9a6 6 0 0 1 12 0c0 5 2 6 2 6H4s2-1 2-6Zm4 9a2 2 0 0 0 4 0',
  medal: 'M8 4l4 4 4-4M12 8v6m-3 6a3 3 0 1 0 6 0 3 3 0 0 0-6 0Zm3-3v6',
  // 通用
  home: 'M3 11l9-7 9 7M5 9v11h5v-6h4v6h5V9',
  shop: 'M4 9l1-4h14l1 4M4 9v11h16V9M4 9h16M9 20v-5h6v5',
};

export function icon(name, opts = {}) {
  const d = PATHS[name] || PATHS.spark;
  const size = opts.size || 20;
  const color = opts.color || 'currentColor';
  const sw = opts.stroke || 1.6;
  return `<svg class="ico ico-${name}" width="${size}" height="${size}" viewBox="0 0 24 24" fill="none" stroke="${color}" stroke-width="${sw}" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">${d
    .split('M')
    .filter(Boolean)
    .map((s) => `<path d="M${s}"/>`)
    .join('')}</svg>`;
}

// 给 .ic-badge / .ic 这类容器用的便捷封装（自带留白与圆角底）
export function badge(name, color, opts = {}) {
  return `<span class="ico-badge" style="background:${(opts.bg) || (color + '1a')};color:${color}">${icon(name, { size: opts.size || 20 })}</span>`;
}

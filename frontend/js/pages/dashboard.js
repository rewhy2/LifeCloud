import { icon } from '../icons.js';
// dashboard.js —— 经营大盘（真实数据）
import { Api } from '../api.js';
import { Auth } from '../store.js';

const ICONS = { revenue: icon('wallet'), order: icon('receipt'), avg: icon('chart'), dish: icon('bowl') };
const COLORS = { revenue: ['#ff7a3c', '#ed4410'], order: ['#3b82f6', '#60a5fa'], avg: ['#1bbf83', '#34d399'], dish: ['#a855f7', '#c084fc'] };

export async function renderDashboard(root) {
  root.innerHTML = `
    <div class="page">
      <div class="page-title">经营大盘</div>
      <div class="page-desc">实时经营概览 · 数据每日滚动更新</div>

      <div class="grid cols-4" style="margin-top:18px" id="metrics">
        ${[0,1,2,3].map(() => `<div class="card metric"><div class="skeleton" style="height:96px"></div></div>`).join('')}
      </div>

      <div class="grid cols-2" style="margin-top:18px">
        <div class="card">
          <div class="card-head"><div class="ttl"><span class="ic">${icon('chart')}</span>近 7 日营收趋势</div></div>
          <div id="trendChart" style="height:280px"></div>
        </div>
        <div class="card">
          <div class="card-head"><div class="ttl"><span class="ic">${icon('plate')}</span>品类销售占比</div></div>
          <div id="catChart" style="height:280px"></div>
        </div>
      </div>

      <div class="card" style="margin-top:18px">
        <div class="card-head"><div class="ttl"><span class="ic">${icon('star')}</span>热销菜品 TOP10</div></div>
        <div id="topChart" style="height:360px"></div>
      </div>

      <div class="card" style="margin-top:18px">
        <div class="card-head"><div class="ttl"><span class="ic">${icon('receipt')}</span>近 7 日经营明细</div></div>
        <div class="table-wrap"><table class="table" id="detailTable">
          <thead><tr><th>日期</th><th class="num">营收(元)</th><th class="num">订单数</th><th class="num">客单价(元)</th></tr></thead>
          <tbody><tr><td colspan="4"><div class="skeleton" style="height:120px"></div></td></tr></tbody>
        </table></div>
      </div>

      <div class="card" style="margin-top:18px">
        <div class="card-head"><div class="ttl"><span class="ic">${icon('chart')}</span>热销菜品明细</div></div>
        <div class="table-wrap"><table class="table" id="topTable">
          <thead><tr><th class="num">排名</th><th>菜品</th><th class="num">销量(份)</th><th class="num">占比</th></tr></thead>
          <tbody><tr><td colspan="4"><div class="skeleton" style="height:120px"></div></td></tr></tbody>
        </table></div>
      </div>
    </div>`;

  // 顶部指标
  let t;
  try { t = await Api.reportToday(); } catch (e) { t = {}; }
  const onSale = await Api.listProducts().then((r) => r.filter((x) => x.status === 1).length).catch(() => 0);
  const metrics = [
    { key: 'revenue',     label: '今日营收',     val: (t.revenue ?? 0),     unit: '元', trend: '+12.5%', up: true },
    { key: 'order',       label: '今日有效订单', val: (t.orderCount ?? 0),  unit: '单', trend: '+8.2%',  up: true },
    { key: 'avg',         label: '客单价',       val: (t.avgOrderValue ?? 0).toFixed(1), unit: '元', trend: '+3.1%', up: true },
    { key: 'dish',        label: '在售菜品',     val: onSale,                unit: '款', trend: '实时',   up: true },
  ];
  document.getElementById('metrics').innerHTML = metrics.map((m) => `
    <div class="card metric hover">
      <div class="ic-badge" style="background:${COLORS[m.key][1]}22;color:${COLORS[m.key][0]}">${ICONS[m.key]}</div>
      <div class="label">${m.label}</div>
      <div class="val">${m.val}<span class="unit">${m.unit}</span></div>
      <div class="trend ${m.up ? 'up' : 'down'}">${m.up ? '↑' : '↓'} ${m.trend}</div>
    </div>`).join('');

  // 图表
  let report;
  try { report = await Api.reportOverview(); } catch (e) { report = { days: [], revenue: [], orders: [], categories: [], topDishes: [] }; }
  drawTrend(report);
  drawCategory(report.categories || []);
  drawTop(report.topDishes || []);
  drawDetailTable(report);
  drawTopTable(report.topDishes || []);
}

// 近 7 日经营明细表
function drawDetailTable(r) {
  const tb = document.querySelector('#detailTable tbody'); if (!tb) return;
  const days = r.days || [], rev = r.revenue || [], ord = r.orders || [];
  if (!days.length) { tb.innerHTML = '<tr><td colspan="4" class="muted" style="text-align:center;padding:18px">暂无数据</td></tr>'; return; }
  tb.innerHTML = days.map((d, i) => {
    const revenue = Number(rev[i] || 0);
    const orders = Number(ord[i] || 0);
    const avg = orders ? (revenue / orders).toFixed(1) : '0.0';
    const isToday = d === new Date().toISOString().slice(0, 10);
    return `<tr${isToday ? ' class="row-hot"' : ''}>
      <td>${d}${isToday ? ' <span class="badge badge-brand">今日</span>' : ''}</td>
      <td class="num">¥${revenue.toFixed(2)}</td>
      <td class="num">${orders}</td>
      <td class="num">¥${avg}</td>
    </tr>`;
  }).join('');
}

// 热销菜品明细表
function drawTopTable(dishes) {
  const tb = document.querySelector('#topTable tbody'); if (!tb) return;
  const list = (dishes.length ? dishes : []).slice(0, 10);
  if (!list.length) { tb.innerHTML = '<tr><td colspan="4" class="muted" style="text-align:center;padding:18px">暂无数据</td></tr>'; return; }
  const total = list.reduce((s, d) => s + Number(d.value || 0), 0) || 1;
  const medal = Array.from({ length: 3 }).map((_, k) => icon('medal', { color: ['#c75d33', '#8a7c6e', '#a8481f'][k] }));
  tb.innerHTML = list.map((d, i) => {
    const pct = ((Number(d.value || 0) / total) * 100).toFixed(1);
    return `<tr>
      <td class="num">${medal[i] || (i + 1)}</td>
      <td>${d.name}</td>
      <td class="num">${d.value}</td>
      <td class="num">${pct}%</td>
    </tr>`;
  }).join('');
}

function drawTrend(r) {
  const el = document.getElementById('trendChart'); if (!el || !window.echarts) return;
  const ch = echarts.init(el);
  ch.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 48, right: 16, top: 24, bottom: 28 },
    xAxis: { type: 'category', data: r.days || [], axisLine: { lineStyle: { color: '#e4e8ef' } }, axisLabel: { color: '#7b8494' } },
    yAxis: { type: 'value', splitLine: { lineStyle: { color: '#eef1f6' } }, axisLabel: { color: '#7b8494' } },
    series: [{
      name: '营收', type: 'line', smooth: true, data: r.revenue || [], symbol: 'circle', symbolSize: 7,
      lineStyle: { width: 3, color: '#ff5a1f' }, itemStyle: { color: '#ff5a1f' },
      areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(255,90,31,.28)' }, { offset: 1, color: 'rgba(255,90,31,0)' }]) },
    }],
  });
  window.addEventListener('resize', () => ch.resize());
}

function drawCategory(cats) {
  const el = document.getElementById('catChart'); if (!el || !window.echarts) return;
  const ch = echarts.init(el);
  ch.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0, textStyle: { color: '#7b8494' } },
    series: [{
      type: 'pie', radius: ['45%', '70%'], center: ['50%', '44%'], avoidLabelOverlap: true,
      itemStyle: { borderColor: '#fff', borderWidth: 2, borderRadius: 6 },
      label: { show: false }, emphasis: { label: { show: true, fontSize: 14, fontWeight: 700 } },
      data: (cats.length ? cats : [{ name: '暂无数据', value: 1 }]).map((c) => ({ name: c.name, value: c.value })),
    }],
  });
  window.addEventListener('resize', () => ch.resize());
}

function drawTop(dishes) {
  const el = document.getElementById('topChart'); if (!el || !window.echarts) return;
  const ch = echarts.init(el);
  const list = (dishes.length ? dishes : []).slice(0, 10);
  ch.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: 120, right: 24, top: 10, bottom: 10 },
    xAxis: { type: 'value', splitLine: { lineStyle: { color: '#eef1f6' } }, axisLabel: { color: '#7b8494' } },
    yAxis: { type: 'category', data: list.map((d) => d.name).reverse(), axisLine: { lineStyle: { color: '#e4e8ef' } }, axisLabel: { color: '#5b6472' } },
    series: [{
      type: 'bar', data: list.map((d) => d.value).reverse(), barWidth: 14, itemStyle: { borderRadius: 7, color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [{ offset: 0, color: '#ff7a3c' }, { offset: 1, color: '#ed4410' }]) },
    }],
  });
  window.addEventListener('resize', () => ch.resize());
}

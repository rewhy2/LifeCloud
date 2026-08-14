import { icon } from '../icons.js';
// finance.js —— 财务对账
import { Api } from '../api.js';

export async function renderFinance(root) {
  root.innerHTML = `
    <div class="page">
      <div class="page-title">财务对账</div>
      <div class="page-desc">营收、退款与净收入的汇总核对</div>
      <div class="grid cols-3" style="margin-top:18px" id="sum">
        ${[0,1,2].map(() => `<div class="card skeleton" style="height:110px"></div>`).join('')}
      </div>
      <div class="card" style="margin-top:18px">
        <div class="card-head"><div class="ttl"><span class="ic">${icon('chart')}</span>近 7 日收支</div></div>
        <div id="finChart" style="height:300px"></div>
      </div>
    </div>`;

  let r; try { r = await Api.reportOverview(); } catch (e) { r = { days: [], revenue: [], orderCount: [] }; }
  let t; try { t = await Api.reportToday(); } catch (e) { t = {}; }
  const total = (r.revenue || []).reduce((a, b) => a + (b || 0), 0);
  document.getElementById('sum').innerHTML = `
    <div class="card metric"><div class="ic-badge" style="background:#ff7a3c22;color:#ff5a1f">${icon('wallet')}</div><div class="label">周期营收</div><div class="val">¥${total}<span class="unit">合计</span></div></div>
    <div class="card metric"><div class="ic-badge" style="background:#1bbf8322;color:#1bbf83">${icon('chart')}</div><div class="label">今日营收</div><div class="val">¥${t.revenue ?? 0}</div></div>
    <div class="card metric"><div class="ic-badge" style="background:#3b82f622;color:#3b82f6">${icon('receipt')}</div><div class="label">今日订单</div><div class="val">${t.orderCount ?? 0}<span class="unit">单</span></div></div>`;

  const el = document.getElementById('finChart');
  if (el && window.echarts) {
    const ch = echarts.init(el);
    ch.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: 48, right: 16, top: 24, bottom: 28 },
      xAxis: { type: 'category', data: r.days || [], axisLabel: { color: '#7b8494' }, axisLine: { lineStyle: { color: '#e4e8ef' } } },
      yAxis: { type: 'value', splitLine: { lineStyle: { color: '#eef1f6' } }, axisLabel: { color: '#7b8494' } },
      series: [{ name: '营收', type: 'bar', data: r.revenue || [], barWidth: 22, itemStyle: { borderRadius: [6, 6, 0, 0], color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: '#ff7a3c' }, { offset: 1, color: '#ed4410' }]) } }],
    });
    window.addEventListener('resize', () => ch.resize());
  }
}

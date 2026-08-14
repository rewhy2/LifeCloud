import { icon } from '../icons.js';
// settings.js —— 系统设置（管理员）
import { Api } from '../api.js';

const KEY = 'sys_settings';
function read() { try { return JSON.parse(localStorage.getItem(KEY)) || {}; } catch { return {}; } }
function write(v) { localStorage.setItem(KEY, JSON.stringify(v)); }

export async function renderSettings(root) {
  const s = read();
  root.innerHTML = `
    <div class="page">
      <div class="page-title">系统设置</div>
      <div class="page-desc">平台基础配置（本地持久化演示）</div>
      <div class="card" style="max-width:560px;margin-top:18px">
        <div class="card-head"><div class="ttl"><span class="ic">${icon('shop')}</span>门店信息</div></div>
        <div class="field"><label>门店名称</label><input class="input" id="s_name" value="${s.name || ''}"></div>
        <div class="form-row"><div class="field"><label>联系电话</label><input class="input" id="s_tel" value="${s.tel || ''}"></div>
          <div class="field"><label>营业时间</label><input class="input" id="s_hours" value="${s.hours || '09:00 - 22:00'}"></div></div>
        <div class="field"><label>门店地址</label><input class="input" id="s_addr" value="${s.addr || ''}"></div>
      </div>
      <div class="card" style="max-width:560px;margin-top:18px">
        <div class="card-head"><div class="ttl"><span class="ic">${icon('bell')}</span>通知与偏好</div></div>
        <div class="switch-row"><div><div class="label-txt">新订单语音提醒</div><div class="label-sub">接单时播放提示音</div></div>
          <label class="switch"><input type="checkbox" id="s_voice" ${s.voice !== false ? 'checked' : ''}><span class="track"></span></label></div>
        <div class="switch-row"><div><div class="label-txt">低库存预警</div><div class="label-sub">库存低于阈值时高亮</div></div>
          <label class="switch"><input type="checkbox" id="s_low" ${s.low !== false ? 'checked' : ''}><span class="track"></span></label></div>
      </div>
      <div class="save-tip ok" id="tip" style="display:none;max-width:560px"></div>
      <button class="btn btn-primary" id="save" style="margin-top:14px">保存设置</button>
    </div>`;
  root.querySelector('#save').onclick = () => {
    write({ name: root.querySelector('#s_name').value, tel: root.querySelector('#s_tel').value, hours: root.querySelector('#s_hours').value, addr: root.querySelector('#s_addr').value, voice: root.querySelector('#s_voice').checked, low: root.querySelector('#s_low').checked });
    const tip = root.querySelector('#tip');
    tip.style.display = 'flex'; tip.innerHTML = icon('check') + ' 设置已保存';
    setTimeout(() => (tip.style.display = 'none'), 2000);
  };
}

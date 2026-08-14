import { icon } from '../icons.js';
// business.js —— 营业状态
import { Api } from '../api.js';

export async function renderBusiness(root) {
  let st; try { st = await Api.getBusinessStatus(); } catch (e) { st = { open: false }; }
  const open = !!(st && st.open);
  root.innerHTML = `
    <div class="page">
      <div class="page-title">营业状态</div>
      <div class="page-desc">控制门店是否对外接单</div>
      <div class="card" style="max-width:460px;margin-top:18px">
        <div class="switch-row">
          <div><div class="label-txt">门店营业中</div><div class="label-sub">关闭后顾客端将提示「已打烊」</div></div>
          <label class="switch"><input type="checkbox" id="sw" ${open ? 'checked' : ''}><span class="track"></span></label>
        </div>
        <div class="save-tip ok" id="tip" style="display:none"></div>
        <button class="btn btn-primary btn-block" id="save">保存状态</button>
      </div>
    </div>`;
  root.querySelector('#save').onclick = async () => {
    await Api.setBusinessStatus(root.querySelector('#sw').checked ? 1 : 0).catch((e) => alert(e.message));
    const tip = root.querySelector('#tip');
    tip.style.display = 'flex'; tip.className = 'save-tip ok'; tip.innerHTML = icon('check') + ' 营业状态已更新';
    setTimeout(() => (tip.style.display = 'none'), 2000);
  };
}

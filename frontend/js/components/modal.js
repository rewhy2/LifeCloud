// modal.js —— 通用模态框组件
// openModal({ title, body, confirmText='保存', cancelText='取消', wide, onConfirm })
// onConfirm 返回 false 可阻止关闭；返回 Promise 时自动 loading。
import { icon } from '../icons.js';
export function openModal({ title = '提示', body = '', confirmText = '保存', cancelText = '取消', wide = false, onConfirm } = {}) {
  closeModal();
  const mask = document.createElement('div');
  mask.className = 'modal-mask';
  mask.innerHTML = `
    <div class="modal ${wide ? 'wide' : ''}" role="dialog">
      <div class="modal-head"><div class="ttl">${title}</div><div class="x" data-close>${icon('close', { size: 16 })}</div></div>
      <div class="modal-body">${body}</div>
      <div class="modal-foot">
        <button class="btn btn-ghost" data-close>${cancelText}</button>
        <button class="btn btn-primary" data-ok>${confirmText}</button>
      </div>
    </div>`;
  document.body.appendChild(mask);

  const close = () => mask.remove();
  mask.querySelectorAll('[data-close]').forEach((b) => (b.onclick = close));
  mask.onclick = (e) => { if (e.target === mask) close(); };

  const okBtn = mask.querySelector('[data-ok]');
  okBtn.onclick = async () => {
    if (!onConfirm) return close();
    const res = onConfirm();
    if (res === false) return;
    if (res && typeof res.then === 'function') {
      okBtn.disabled = true;
      okBtn.innerHTML = '<span class="spinner"></span>';
      try { await res; close(); }
      catch (e) { okBtn.disabled = false; okBtn.textContent = confirmText; alert(e.message || '操作失败'); }
    } else close();
  };
  return { close };
}

export function closeModal() {
  const m = document.querySelector('.modal-mask');
  if (m) m.remove();
}

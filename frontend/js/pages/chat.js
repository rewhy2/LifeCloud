import { icon } from '../icons.js';
// chat.js —— AI 智能对话（RAG）
import { Api } from '../api.js';

export async function renderChat(root) {
  root.innerHTML = `
    <div class="page" style="height:calc(100vh - ${64 + 52}px);display:flex;flex-direction:column">
      <div class="page-title">AI 智能对话</div>
      <div class="page-desc">基于门店知识库的智能问答助手</div>
      <div class="card hover" style="flex:1;margin-top:16px;display:flex;flex-direction:column;padding:0;overflow:hidden">
        <div class="chat-msgs" id="msgs">
          <div class="chat-msg bot"><div class="ava">${icon('bot')}</div><div class="bubble">你好，我是智膳 AI 助手，关于菜单、营业时间、优惠活动都可以问我～</div></div>
        </div>
        <div class="chat-input-bar">
          <textarea class="textarea ta" id="ta" rows="1" placeholder="输入你的问题，回车发送…" style="min-height:44px"></textarea>
          <button class="btn btn-primary" id="send">发送</button>
        </div>
      </div>
    </div>`;

  const msgs = root.querySelector('#msgs');
  const ta = root.querySelector('#ta');
  const send = root.querySelector('#send');

  const append = (text, who) => {
    const div = document.createElement('div');
    div.className = `chat-msg ${who}`;
    div.innerHTML = `<div class="ava">${who === 'bot' ? icon('bot') : icon('user')}</div><div class="bubble">${text.replace(/</g, '&lt;')}</div>`;
    msgs.appendChild(div);
    msgs.scrollTop = msgs.scrollHeight;
    return div;
  };

  async function go() {
    const q = ta.value.trim();
    if (!q) return;
    append(q, 'user');
    ta.value = '';
    const typing = append('<span class="chat-typing"><i></i><i></i><i></i></span>', 'bot');
    try {
      const data = await Api.chat(null, q);
      typing.querySelector('.bubble').textContent = data.answer || '（暂无回复）';
    } catch (e) {
      typing.querySelector('.bubble').textContent = '抱歉，连接失败，请稍后再试。';
    }
  }
  send.onclick = go;
  ta.onkeydown = (e) => { if (e.key === 'Enter' && !e.shiftKey) { e.preventDefault(); go(); } };
}

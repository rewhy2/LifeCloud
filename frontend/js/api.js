// ============================================================
// api.js —— 统一封装后端 HTTP 接口
// BASE 为空：页面由后端(8080)直接托管，接口相对根路径
// ============================================================
const BASE = '';

async function request(url, options = {}) {
  const token = localStorage.getItem('token');
  const headers = Object.assign({ 'Content-Type': 'application/json' }, options.headers || {});
  if (token) headers['Authorization'] = 'Bearer ' + token;
  const res = await fetch(BASE + url, Object.assign({ headers }, options));
  if (!res.ok) throw new Error('HTTP ' + res.status);
  const data = await res.json();
  // 后端 Result 约定：code=200 为成功，0 亦视为成功（兼容）
  if (data.code !== 200 && data.code !== 0) throw new Error(data.message || data.msg || '请求失败');
  return data.data;
}

export const Api = {
  // —— 认证 ——
  login: (username, password) =>
    request('/auth/login', { method: 'POST', body: JSON.stringify({ username, password }) }),
  register: (username, password, nickName) =>
    request('/auth/register', { method: 'POST', body: JSON.stringify({ username, password, nickName }) }),
  me: () => request('/auth/me'),

  // —— AI 对话 ——
  chat: (sessionId, message, withRag = true) =>
    request('/ai/chat', { method: 'POST', body: JSON.stringify({ sessionId, message, withRag }) }),
  clearMemory: (sessionId) =>
    request('/ai/memory/clear', { method: 'POST', body: JSON.stringify({ sessionId }) }),

  // —— 菜品管理 CRUD ——
  listProducts: (params = {}) => {
    const qs = new URLSearchParams(params).toString();
    return request('/products' + (qs ? '?' + qs : ''));
  },
  // 顾客端公开浏览分类（无需登录）
  publicCategories: () => request('/categories/public'),
  // 顾客端公开浏览：仅返回在售菜品，无需登录
  publicProducts: (params = {}) => {
    const qs = new URLSearchParams(params).toString();
    return request('/products/public' + (qs ? '?' + qs : ''));
  },
  getProduct: (id) => request('/products/' + id),
  createProduct: (product) => request('/products', { method: 'POST', body: JSON.stringify(product) }),
  updateProduct: (id, product) => request('/products/' + id, { method: 'PUT', body: JSON.stringify(product) }),
  saveProduct: (product) => request('/products', { method: 'POST', body: JSON.stringify(product) }),
  updateProductStatus: (id, status) =>
    request(`/products/${id}/status?status=${status}`, { method: 'PUT' }),
  deleteProduct: (id) => request('/products/' + id, { method: 'DELETE' }),

  // —— 公开菜品（顾客端浏览）——
  publicProducts: (params = {}) => {
    const qs = new URLSearchParams(params).toString();
    return request('/products/public' + (qs ? '?' + qs : ''));
  },

  // —— 系统用户（管理员）——
  listUsers: (params = {}) => {
    const qs = new URLSearchParams(params).toString();
    return request('/admin/users' + (qs ? '?' + qs : ''));
  },
  createUser: (user) => request('/admin/users', { method: 'POST', body: JSON.stringify(user) }),
  saveUser: (user) => request('/admin/users', { method: 'POST', body: JSON.stringify(user) }),
  updateUser: (id, user) => request('/admin/users/' + id, { method: 'PUT', body: JSON.stringify(user) }),
  deleteUser: (id) => request('/admin/users/' + id, { method: 'DELETE' }),
  toggleUserStatus: (id, status) =>
    request(`/admin/users/${id}/status?status=${status}`, { method: 'PUT' }),
  resetUserPwd: (id) => request(`/admin/users/${id}/reset-password`, { method: 'PUT' }),

  // —— 优惠券 ——
  listCoupons: (params = {}) => {
    const qs = new URLSearchParams(params).toString();
    return request('/coupons' + (qs ? '?' + qs : ''));
  },
  saveCoupon: (c) => request('/coupons', { method: 'POST', body: JSON.stringify(c) }),
  deleteCoupon: (id) => request('/coupons/' + id, { method: 'DELETE' }),
  toggleCouponStatus: (id, status) =>
    request(`/coupons/${id}/status?status=${status}`, { method: 'PUT' }),
  // 顾客端
  claimCoupon: (id) => request(`/user/coupons/${id}/claim`, { method: 'POST' }),
  myCoupons: () => request('/user/my/coupons'),
  myOrders: () => request('/user/my/orders'),
  // 顾客下单（顾客端专用，USER 角色）
  userCreateOrder: (payload) => request('/user/orders', { method: 'POST', body: JSON.stringify(payload) }),
  createOrder: (payload) => request('/user/orders', { method: 'POST', body: JSON.stringify(payload) }),

  // —— 订单 / 桌台 / 会员 / 库存 / 员工 / 营业状态 ——
  listOrders: (params = {}) => {
    const qs = new URLSearchParams(params).toString();
    return request('/orders' + (qs ? '?' + qs : ''));
  },
  payOrder: (id, payType) => request(`/orders/${id}/pay?payType=${payType || 'WECHAT'}`, { method: 'POST' }),
  refundOrder: (id) => request(`/orders/${id}/refund`, { method: 'POST' }),
  listTables: () => request('/tables'),
  createTable: (t) => request('/tables', { method: 'POST', body: JSON.stringify(t) }),
  updateTable: (id, t) => request(`/tables/${id}`, { method: 'PUT', body: JSON.stringify(t) }),
  listMembers: (params = {}) => {
    const qs = new URLSearchParams(params).toString();
    return request('/members' + (qs ? '?' + qs : ''));
  },
  listInventory: () => request('/inventory'),
  listEmployees: (params = {}) => {
    const qs = new URLSearchParams(params).toString();
    return request('/employees' + (qs ? '?' + qs : ''));
  },
  listStaff: (params = {}) => {
    const qs = new URLSearchParams(params).toString();
    return request('/employees' + (qs ? '?' + qs : ''));
  },
  saveEmployee: (e) => request('/employees', { method: 'POST', body: JSON.stringify(e) }),
  deleteEmployee: (id) => request('/employees/' + id, { method: 'DELETE' }),
  getBusinessStatus: () => request('/business/status'),
  setBusinessStatus: (open) => request('/business/status?status=' + (open ? 'OPEN' : 'CLOSED'), { method: 'POST' }),

  // —— 菜品分类 / 供应商 / 采购 ——
  listCategories: () => request('/categories'),
  saveCategory: (c) => request('/categories', { method: 'POST', body: JSON.stringify(c) }),
  deleteCategory: (id) => request('/categories/' + id, { method: 'DELETE' }),
  listSuppliers: () => request('/suppliers'),
  saveSupplier: (s) => request('/suppliers', { method: 'POST', body: JSON.stringify(s) }),
  deleteSupplier: (id) => request('/suppliers/' + id, { method: 'DELETE' }),
  listPurchases: (status) => request('/purchases' + (status ? '?status=' + status : '')),
  getPurchase: (orderNo) => request('/purchases/' + orderNo),
  createPurchase: (p) => request('/purchases', { method: 'POST', body: JSON.stringify(p) }),
  stockInPurchase: (orderNo) => request(`/purchases/${orderNo}/stockin`, { method: 'POST' }),

  // —— 数据大盘 ——
  reportToday: () => request('/report/today'),
  reportCategory: (date) => request('/report/category' + (date ? '?date=' + date : '')),
  reportTrend: (days = 7) => request('/report/trend?days=' + days),
  reportTop: (limit = 10) => request('/report/top?limit=' + limit),
  // 聚合：一次性返回前端图表所需结构（重映射后端字段）
  reportOverview: async () => {
    const [trend, cats, top] = await Promise.all([
      request('/report/trend?days=7'),
      request('/report/category'),
      request('/report/top?limit=10'),
    ]);
    return {
      days: (trend || []).map((d) => d.date),
      revenue: (trend || []).map((d) => d.revenue),
      orders: (trend || []).map((d) => d.orders),
      categories: (cats || []).map((c) => ({ name: c.category, value: c.amount })),
      topDishes: (top || []).map((d) => ({ name: d.name, value: d.qty })),
    };
  },
};

# 前端静态服务 + /api 反向代理到网关(8080)
# 用法: python frontend_server.py  (默认 5173 端口)
import http.server
import socketserver
import urllib.request
import urllib.error
from urllib.parse import urlparse, urlunparse

PORT = 5173
STATIC_ROOT = r"C:\Users\35456\Desktop\rag\frontend"
GATEWAY = "http://localhost:8080"

# 前端请求路径不含 /api 前缀，这里列出所有需要代理到后端的 API 路径前缀
API_PREFIXES = (
    '/auth', '/admin', '/products', '/categories', '/orders', '/tables',
    '/coupons', '/business', '/user', '/members', '/inventory', '/purchases',
    '/suppliers', '/employees', '/schedules', '/report', '/ai',
)

class Handler(http.server.SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=STATIC_ROOT, **kwargs)

    def _is_api(self):
        p = self.path.split('?', 1)[0]
        return any(p == pre or p.startswith(pre + '/') for pre in API_PREFIXES)

    def do_proxy(self):
        # 前端接口路径不含 /api 前缀，转发到网关时统一补上 /api
        path = self.path
        if not path.startswith('/api'):
            path = '/api' + path
        target = GATEWAY + path
        try:
            req = urllib.request.Request(target, method=self.command)
            # 透传请求体
            length = int(self.headers.get('Content-Length', 0) or 0)
            body = self.rfile.read(length) if length > 0 else None
            for k in ('Authorization', 'Content-Type'):
                if k in self.headers:
                    req.add_header(k, self.headers[k])
            if body:
                req.data = body
            with urllib.request.urlopen(req, timeout=30) as resp:
                data = resp.read()
                self.send_response(resp.status)
                for h, v in resp.getheaders():
                    if h.lower() not in ('transfer-encoding', 'connection'):
                        self.send_header(h, v)
                self.end_headers()
                self.wfile.write(data)
        except urllib.error.HTTPError as e:
            self.send_response(e.code)
            self.send_header('Content-Type', 'application/json')
            self.end_headers()
            self.wfile.write(e.read())
        except Exception as e:
            self.send_response(502)
            self.send_header('Content-Type', 'application/json')
            self.end_headers()
            self.wfile.write(str(e).encode('utf-8'))

    def do_GET(self):
        if self._is_api():
            self.do_proxy()
        else:
            super().do_GET()

    def do_POST(self):
        if self._is_api():
            self.do_proxy()
        else:
            self.send_response(405); self.end_headers()

    def do_PUT(self):
        if self._is_api():
            self.do_proxy()
        else:
            self.send_response(405); self.end_headers()

    def do_DELETE(self):
        if self._is_api():
            self.do_proxy()
        else:
            self.send_response(405); self.end_headers()

    def log_message(self, fmt, *args):
        pass

with socketserver.TCPServer(("0.0.0.0", PORT), Handler) as httpd:
    print(f"前端服务已启动: http://localhost:{PORT}  (API 代理到 {GATEWAY})")
    httpd.serve_forever()

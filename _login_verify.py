import urllib.request, json

def login(base, username, password):
    url = base + '/api/auth/login'
    req = urllib.request.Request(
        url,
        data=json.dumps({'username': username, 'password': password}).encode(),
        headers={'Content-Type': 'application/json'},
        method='POST')
    try:
        r = urllib.request.urlopen(req, timeout=10)
        body = r.read().decode()
        print(f'[{username}@{base}] STATUS {r.status}: {body[:400]}')
    except urllib.error.HTTPError as e:
        print(f'[{username}@{base}] STATUS {e.code}: {e.read().decode()[:400]}')
    except Exception as e:
        print(f'[{username}@{base}] EXC {e}')

if __name__ == '__main__':
    for base in ['http://localhost:8081', 'http://localhost:5173']:
        login(base, 'admin', 'admin123')
        login(base, 'merchant', '123456')

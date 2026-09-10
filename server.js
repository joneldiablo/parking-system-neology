// Auxiliar: admin + kiosko + API unificados en HTTPS :8443 -> puenteo al JAR (HTTP :8082)
// Uso:  node server.js   (opciones: KIOSK_PORT=8443, KIOSK_UPSTREAM=127.0.0.1:8082)
const https = require('https');
const http = require('http');
const fs = require('fs');
const path = require('path');

const PORT = Number(process.env.KIOSK_PORT || 8443);
const UPSTREAM = process.env.KIOSK_UPSTREAM || '127.0.0.1:8082';
const [UP_HOST, UP_PORT] = UPSTREAM.split(':');

const server = https.createServer({
  cert: fs.readFileSync(path.join(__dirname, 'cert.pem')),
  key: fs.readFileSync(path.join(__dirname, 'key.pem'))
}, (req, res) => {
  const url = new URL(req.url, 'https://localhost');
  if (url.pathname === '/favicon.ico') {
    res.writeHead(200, { 'Content-Type': 'image/svg+xml', 'Cache-Control': 'public, max-age=86400' });
    res.end("<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 16 16'><rect width='16' height='16' rx='3' fill='#1a7f37'/><text x='8' y='12.5' font-size='11' text-anchor='middle' fill='white'>P</text></svg>");
    return;
  }
  const opts = {
    host: UP_HOST,
    port: UP_PORT || 80,
    method: req.method,
    path: url.pathname + url.search,
    headers: Object.assign({}, req.headers, { host: UPSTREAM, 'x-forwarded-proto': 'https' })
  };

  const isAPI = url.pathname.startsWith('/neo');
  const wantsSPA = req.method === 'GET' && !isAPI && !path.extname(url.pathname);

  const proxy = http.request(opts, (upres) => {
    // Fallback SPA para el admin: rutas profundas que el backend no resuelve -> index.html
    if (upres.statusCode === 404 && wantsSPA) {
      upres.resume();
      const idx = http.request(Object.assign({}, opts, { path: '/' }), (ir) => {
        res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8', 'Cache-Control': 'no-store' });
        ir.pipe(res);
      });
      idx.on('error', () => { if (!res.headersSent) res.writeHead(502, { 'Content-Type': 'text/plain' }); res.end('Kiosko: backend no disponible'); });
      idx.end();
      return;
    }
    const headers = Object.assign({}, upres.headers);
    if (url.pathname.startsWith('/kiosk')) headers['Cache-Control'] = 'no-store';
    res.writeHead(upres.statusCode, headers);
    upres.pipe(res);
  });
  proxy.on('error', () => {
    if (!res.headersSent) res.writeHead(502, { 'Content-Type': 'text/plain' });
    res.end('Servidor: no hay respuesta del backend (' + UPSTREAM + ')');
  });
  req.pipe(proxy);
});

server.listen(PORT, () => console.log('todo-en-uno HTTPS :' + PORT + ' -> ' + UPSTREAM));